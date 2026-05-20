package com.circularo.cli.cli;

import com.circularo.cli.api.UsersApiClient;
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
        mixinStandardHelpOptions = true,
        header = "Search and retrieve Circularo user accounts.",
        description = {
            "",
            "Queries the Circularo users database using Elasticsearch query_string syntax.",
            "You can search for text matches across all fields, or filter by specific attributes.",
            "",
            "Supported Query Examples:",
            "  - Simple keyword search:       -q \"Lubo\"",
            "  - Filter by specific email:    -q \"mail:klauberl@seznam.cz\"",
            "  - Wildcard domain filter:      -q \"mail:*@seznam.cz\"",
            "  - Combine fields using AND:    -q \"recordType:internal AND mail:*seznam*\"",
            "",
            "Formatting Options:",
            "  - Use '-f table' (default) to show results in a readable grid.",
            "  - Use '-f json' to output raw JSON objects for integration scripts."
        },
        footer = {
            "",
            "Refer to 'docs/user_manual.md' for the complete user manual and troubleshooting steps."
        }
)
public class UsersCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter) {
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
