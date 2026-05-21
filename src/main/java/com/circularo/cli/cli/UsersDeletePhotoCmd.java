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

@Component
@Command(
        name = "delete-photo",
        mixinStandardHelpOptions = true,
        header = "Delete user profile photo.",
        description = "Deletes the profile photo image for the specified user ID."
)
public class UsersDeletePhotoCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user")
    private String id;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersDeletePhotoCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        UserModels.GenericStatus status = apiClient.deletePhoto(id);
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
    }
}
