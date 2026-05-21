package com.circularo.cli.cli;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.UserModels;
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
        name = "upload-photo",
        mixinStandardHelpOptions = true,
        header = "Upload user profile photo.",
        description = "Uploads a profile image file for the specified user ID."
)
public class UsersUploadPhotoCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user")
    private String id;

    @Option(names = {"--file"}, required = true, description = "Path to the local profile photo image file")
    private String filePath;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersUploadPhotoCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        try {
            Path path = Path.of(filePath);
            byte[] bytes = Files.readAllBytes(path);
            String filename = path.getFileName().toString();
            UserModels.UserConfiguration config = apiClient.uploadPhoto(id, bytes, filename);
            String formattedOutput;
            if ("json".equalsIgnoreCase(format)) {
                formattedOutput = jsonFormatter.formatUserConfiguration(config);
            } else if ("table".equalsIgnoreCase(format)) {
                formattedOutput = tableFormatter.formatUserConfiguration(config);
            } else if ("md".equalsIgnoreCase(format)) {
                formattedOutput = markdownFormatter.formatUserConfiguration(config);
            } else {
                throw new IllegalArgumentException("Unsupported format type: " + format);
            }
            spec.commandLine().getOut().println(formattedOutput);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file: " + filePath, e);
        }
    }
}
