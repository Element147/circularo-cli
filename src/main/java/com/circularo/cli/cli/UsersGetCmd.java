package com.circularo.cli.cli;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;

@Component
@Command(
        name = "get",
        mixinStandardHelpOptions = true,
        header = "Retrieve a single user by ID.",
        description = "Gets complete details for the user with the specified ID."
)
public class UsersGetCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user to retrieve")
    private String id;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersGetCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        User user = apiClient.getUser(id);
        String formattedOutput;
        List<User> list = user != null ? List.of(user) : List.of();
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatUsers(list);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatUsers(list);
        } else if ("md".equalsIgnoreCase(format)) {
            formattedOutput = markdownFormatter.formatUsers(list);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }
        spec.commandLine().getOut().println(formattedOutput);
    }
}
