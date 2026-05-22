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
import picocli.CommandLine.Spec;

@Component
@Command(
        name = "check-activation",
        mixinStandardHelpOptions = true,
        header = "Check activation token validity.",
        description = "Validates the active user activation token."
)
public class UsersCheckActivationCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Autowired
    public UsersCheckActivationCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        UserModels.UserActivation activation = apiClient.checkActivationToken();
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatUserActivation(activation),
                        j -> j.formatUserActivation(activation),
                        m -> m.formatUserActivation(activation))
        );
    }
}
