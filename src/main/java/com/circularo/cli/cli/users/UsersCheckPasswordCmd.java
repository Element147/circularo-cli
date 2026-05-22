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
        name = "check-password",
        mixinStandardHelpOptions = true,
        header = "Check password strength.",
        description = "Validates password strength against the tenant's password policy."
)
public class UsersCheckPasswordCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--password"}, required = true, description = "The password to check")
    private String password;

    @Option(names = {"--tenant"}, description = "The Circularo tenant context")
    private String tenant;

    @Autowired
    public UsersCheckPasswordCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        UserModels.PasswordStrength strength = apiClient.checkPasswordStrength(password, tenant);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatPasswordStrength(strength),
                        j -> j.formatPasswordStrength(strength),
                        m -> m.formatPasswordStrength(strength))
        );
    }
}
