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
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.List;

@Component
@Command(
        name = "get-by-username",
        mixinStandardHelpOptions = true,
        header = "Get user info by username.",
        description = "Retrieves details for a user based on their username."
)
public class UsersGetByUsernameCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The username to look up")
    private String username;

    @Option(names = {"--photo-size"}, description = "Requested profile photo size (e.g. small, medium, large)")
    private String photoSize;

    @Autowired
    public UsersGetByUsernameCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        User user = apiClient.getUserByUsername(username, photoSize);
        List<User> list = user != null ? List.of(user) : List.of();
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatUsers(list),
                        j -> j.formatUsers(list),
                        m -> m.formatUsers(list))
        );
    }
}
