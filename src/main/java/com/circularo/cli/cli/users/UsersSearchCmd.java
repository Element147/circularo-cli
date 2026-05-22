package com.circularo.cli.cli.users;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.cli.FormatMixin;
import com.circularo.cli.cli.OutputHelper;
import com.circularo.cli.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
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
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"-q", "--query"}, description = "Elasticsearch query string")
    private String query;

    @Option(names = {"--limit", "-l"}, description = "Maximum number of results to return (default: 50)", defaultValue = "50")
    private int limit;

    @Autowired
    public UsersSearchCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        List<User> users = apiClient.searchUsers(query, limit);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatUsers(users),
                        j -> j.formatUsers(users),
                        m -> m.formatUsers(users))
        );
    }
}
