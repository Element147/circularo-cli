package com.circularo.cli.cli;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.UserModels;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Command(
        name = "update",
        mixinStandardHelpOptions = true,
        header = "Update user accounts bulk.",
        description = "Updates multiple user accounts using a bulk JSON payload file."
)
public class UsersUpdateCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;
    private final ObjectMapper objectMapper;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--file"}, required = true, description = "Path to a local JSON file containing UpdateUsersBulkRequest payload")
    private String filePath;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersUpdateCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            String content = Files.readString(Path.of(filePath));
            UserModels.UpdateUsersBulkRequest payload = objectMapper.readValue(content, UserModels.UpdateUsersBulkRequest.class);
            UserModels.BulkResponse response = apiClient.updateUsersBulk(payload);
            String formattedOutput;
            if ("json".equalsIgnoreCase(format)) {
                formattedOutput = jsonFormatter.formatBulkResponse(response);
            } else if ("table".equalsIgnoreCase(format)) {
                formattedOutput = tableFormatter.formatBulkResponse(response);
            } else if ("md".equalsIgnoreCase(format)) {
                formattedOutput = markdownFormatter.formatBulkResponse(response);
            } else {
                throw new IllegalArgumentException("Unsupported format type: " + format);
            }
            spec.commandLine().getOut().println(formattedOutput);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse file: " + filePath, e);
        }
    }
}
