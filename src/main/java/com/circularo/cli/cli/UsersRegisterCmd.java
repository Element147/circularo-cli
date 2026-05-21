package com.circularo.cli.cli;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import com.circularo.cli.model.UserModels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
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
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

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

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersRegisterCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        UserModels.RegisterUserRequest requestPayload = new UserModels.RegisterUserRequest(mail, fullname, name, phone, company, edition, language);
        UserModels.GenericStatus status = apiClient.registerUser(requestPayload);
        String formattedOutput;
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatGenericStatus(status);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatGenericStatus(status);
        } else if ("md".equalsIgnoreCase(format)) {
            formattedOutput = markdownFormatter.formatGenericStatus(status);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }
        spec.commandLine().getOut().println(formattedOutput);
    }
}
