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
        name = "reset-password",
        mixinStandardHelpOptions = true,
        header = "Reset user password.",
        description = "Resets password using a verification code received by the user."
)
public class UsersResetPasswordCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--username"}, required = true, description = "The username (email) of the user")
    private String username;

    @Option(names = {"--code"}, required = true, description = "The password reset verification code")
    private String code;

    @Option(names = {"--new-password"}, required = true, description = "The new password to set")
    private String newPassword;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersResetPasswordCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        UserModels.ResetPasswordRequest requestPayload = new UserModels.ResetPasswordRequest(username, code, newPassword);
        UserModels.GenericStatus status = apiClient.resetPassword(requestPayload);
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
