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
        name = "check-password",
        mixinStandardHelpOptions = true,
        header = "Check password strength.",
        description = "Evaluates the strength of the provided password against policies."
)
public class UsersCheckPasswordCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--password"}, required = true, description = "The password string to check")
    private String password;

    @Option(names = {"--tenant"}, description = "Optional tenant context")
    private String tenant;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersCheckPasswordCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        UserModels.PasswordStrength strength = apiClient.checkPasswordStrength(password, tenant);
        String formattedOutput;
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatPasswordStrength(strength);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatPasswordStrength(strength);
        } else if ("md".equalsIgnoreCase(format)) {
            formattedOutput = markdownFormatter.formatPasswordStrength(strength);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }
        spec.commandLine().getOut().println(formattedOutput);
    }
}
