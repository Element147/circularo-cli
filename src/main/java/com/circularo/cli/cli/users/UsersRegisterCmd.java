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
        name = "register",
        mixinStandardHelpOptions = true,
        header = "Register a new user account.",
        description = "Initiates user self-registration."
)
public class UsersRegisterCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--email"}, required = true, description = "The email address of the new user")
    private String mail;

    @Option(names = {"--fullname"}, required = true, description = "The full name of the new user")
    private String fullname;

    @Option(names = {"--name"}, required = true, description = "The username of the new user")
    private String name;

    @Option(names = {"--phone"}, description = "Optional phone number")
    private String phone;

    @Option(names = {"--company"}, description = "Optional company name")
    private String company;

    @Option(names = {"--edition"}, description = "Optional edition name")
    private String edition;

    @Option(names = {"--language"}, description = "Optional user language (e.g. en, cs)")
    private String language;

    @Autowired
    public UsersRegisterCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        UserModels.RegisterUserRequest requestPayload = new UserModels.RegisterUserRequest(mail, fullname, name, phone, company, edition, language);
        UserModels.GenericStatus status = apiClient.registerUser(requestPayload);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatGenericStatus(status),
                        j -> j.formatGenericStatus(status),
                        m -> m.formatGenericStatus(status))
        );
    }
}
