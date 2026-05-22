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
import picocli.CommandLine.Spec;

@Component
@Command(
        name = "reset-password",
        mixinStandardHelpOptions = true,
        header = "Reset user password with code.",
        description = "Resets the user password using a verification code."
)
public class UsersResetPasswordCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--username"}, required = true, description = "The username of the user")
    private String username;

    @Option(names = {"--code"}, required = true, description = "The verification code")
    private String code;

    @Option(names = {"--new-password"}, required = true, description = "The new password to set")
    private String newPassword;

    @Autowired
    public UsersResetPasswordCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        UserModels.ResetPasswordRequest requestPayload = new UserModels.ResetPasswordRequest(code, newPassword);
        UserModels.GenericStatus status = apiClient.resetPassword(requestPayload);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatGenericStatus(status),
                        j -> j.formatGenericStatus(status),
                        m -> m.formatGenericStatus(status))
        );
    }
}
