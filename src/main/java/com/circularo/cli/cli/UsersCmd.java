package com.circularo.cli.cli;

import com.circularo.cli.api.CircularoApiClient;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.List;

/**
 * Picocli sub-command for user search operations.
 * Registered as a Spring {@link Component} so that Picocli's Spring factory
 * can inject dependencies when they are added.
 */
@Component
@Command(
        name = "users",
        description = "Search and list Circularo users",
        mixinStandardHelpOptions = true
)
public class UsersCmd implements Runnable {

    private final CircularoApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersCmd(CircularoApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
    }

    @Override
    public void run() {
        List<User> users = apiClient.searchUsers(query);
        
        String formattedOutput;
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatUsers(users);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatUsers(users);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }

        spec.commandLine().getOut().println(formattedOutput);
    }
}
