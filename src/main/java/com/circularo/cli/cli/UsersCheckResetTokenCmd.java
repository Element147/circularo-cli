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
        name = "check-reset-token",
        mixinStandardHelpOptions = true,
        header = "Check validity of reset token.",
        description = "Checks if the provided password reset token is still valid."
)
public class UsersCheckResetTokenCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--token"}, required = true, description = "The password reset token to check")
    private String resetToken;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersCheckResetTokenCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        UserModels.UserResetToken tokenInfo = apiClient.checkResetToken(resetToken);
        String formattedOutput;
        if ("json".equalsIgnoreCase(format)) {
            formattedOutput = jsonFormatter.formatUserResetToken(tokenInfo);
        } else if ("table".equalsIgnoreCase(format)) {
            formattedOutput = tableFormatter.formatUserResetToken(tokenInfo);
        } else if ("md".equalsIgnoreCase(format)) {
            formattedOutput = markdownFormatter.formatUserResetToken(tokenInfo);
        } else {
            throw new IllegalArgumentException("Unsupported format type: " + format);
        }
        spec.commandLine().getOut().println(formattedOutput);
    }
}
