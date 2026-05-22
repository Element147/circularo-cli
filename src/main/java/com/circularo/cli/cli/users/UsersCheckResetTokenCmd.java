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
        name = "check-reset-token",
        mixinStandardHelpOptions = true,
        header = "Check password reset token validity.",
        description = "Validates a password reset token."
)
public class UsersCheckResetTokenCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--token"}, required = true, description = "The password reset token to validate")
    private String token;

    @Autowired
    public UsersCheckResetTokenCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        UserModels.UserResetToken resetToken = apiClient.checkResetToken(token);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatUserResetToken(resetToken),
                        j -> j.formatUserResetToken(resetToken),
                        m -> m.formatUserResetToken(resetToken))
        );
    }
}
