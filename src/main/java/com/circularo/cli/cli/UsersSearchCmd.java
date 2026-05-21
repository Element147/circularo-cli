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
import picocli.CommandLine.Spec;

import java.util.List;

@Component
@Command(
        name = "search",
        mixinStandardHelpOptions = true,
        header = "Search Circularo user accounts via Elasticsearch query.",
        description = "Queries the user database using query string syntax."
)
public class UsersSearchCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"--limit", "-l"}, description = "Maximum number of results to return (default: 50)", defaultValue = "50")
    private int limit;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersSearchCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        List<User> users = apiClient.searchUsers(query, limit);
        String formattedOutput;
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatUsers(users);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatUsers(users);
        } else if ("md".equalsIgnoreCase(format)) {
            formattedOutput = markdownFormatter.formatUsers(users);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }
        spec.commandLine().getOut().println(formattedOutput);
    }
}
