package com.circularo.cli.cli.users;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.cli.FormatMixin;
import com.circularo.cli.cli.OutputHelper;
import com.circularo.cli.model.UserModels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Component
@Command(
        name = "reset-password-username",
        mixinStandardHelpOptions = true,
        header = "Initiate password reset by username.",
        description = "Sends a password reset email to the specified user."
)
public class UsersResetPasswordUsernameCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The username of the user")
    private String username;

    @Option(names = {"--tenant"}, description = "The Circularo tenant context")
    private String tenant;

    @Autowired
    public UsersResetPasswordUsernameCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        UserModels.GenericStatus status = apiClient.resetPasswordUsername(username, tenant);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatGenericStatus(status),
                        j -> j.formatGenericStatus(status),
                        m -> m.formatGenericStatus(status))
        );
    }
}
