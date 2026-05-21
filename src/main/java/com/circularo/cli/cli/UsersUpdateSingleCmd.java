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
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Command(
        name = "update-single",
        mixinStandardHelpOptions = true,
        header = "Update a single user.",
        description = "Updates properties for the user with the specified ID."
)
public class UsersUpdateSingleCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;
    private final ObjectMapper objectMapper;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user to update")
    private String id;

    @Option(names = {"--file"}, required = true, description = "Path to a local JSON file containing UpdateUserRequest payload")
    private String filePath;

    @Option(names = {"--disable-active-tokens"}, description = "Disable active authentication tokens for the user")
    private Boolean disableActiveTokens;

    @Option(names = {"--version"}, description = "User resource version for optimistic locking")
    private Integer version;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersUpdateSingleCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
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
            UserModels.UpdateUserRequest payload = objectMapper.readValue(content, UserModels.UpdateUserRequest.class);
            UserModels.GenericStatus status = apiClient.updateUser(id, payload, disableActiveTokens, version);
            String formattedOutput;
            if ("json".equalsIgnoreCase(format)) {
                formattedOutput = jsonFormatter.formatGenericStatus(status);
            } else if ("table".equalsIgnoreCase(format)) {
                formattedOutput = tableFormatter.formatGenericStatus(status);
            } else if ("md".equalsIgnoreCase(format)) {
                formattedOutput = markdownFormatter.formatGenericStatus(status);
            } else {
                throw new IllegalArgumentException("Unsupported format type: " + format);
            }
            spec.commandLine().getOut().println(formattedOutput);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse file: " + filePath, e);
        }
    }
}
