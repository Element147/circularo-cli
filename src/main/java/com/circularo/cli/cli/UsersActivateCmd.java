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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
@Command(
        name = "activate",
        mixinStandardHelpOptions = true,
        header = "Activate a user account.",
        description = "Activates a user account, setting password and optional profile photo."
)
public class UsersActivateCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--password"}, required = true, description = "Password for the new account")
    private String password;

    @Option(names = {"--fullname"}, description = "Full name of the user")
    private String fullname;

    @Option(names = {"--phone"}, description = "Phone number of the user")
    private String phone;

    @Option(names = {"--role"}, description = "Role in the company")
    private String role;

    @Option(names = {"--language"}, description = "User language (e.g. en, cs)")
    private String language;

    @Option(names = {"--edition"}, description = "Name of the edition to activate")
    private String edition;

    @Option(names = {"--org-name"}, description = "Organization name")
    private String organizationName;

    @Option(names = {"--terms-accepted"}, description = "Accept terms and conditions", defaultValue = "true")
    private Boolean termsAccepted;

    @Option(names = {"--timezone"}, description = "Timezone")
    private String timezone;

    @Option(names = {"--auto-timezone"}, description = "Automatically update timezone based on browser")
    private Boolean autoTimezone;

    @Option(names = {"--return-token"}, description = "Return token in response body", defaultValue = "true")
    private Boolean returnToken;

    @Option(names = {"--file"}, description = "Path to a local profile photo image file to upload")
    private String filePath;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersActivateCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        try {
            Map<String, String> fields = new HashMap<>();
            fields.put("password", password);
            if (fullname != null) fields.put("fullname", fullname);
            if (phone != null) fields.put("phone", phone);
            if (role != null) fields.put("role", role);
            if (language != null) fields.put("language", language);
            if (edition != null) fields.put("edition", edition);
            if (organizationName != null) fields.put("organizationName", organizationName);
            if (termsAccepted != null) fields.put("termsAccepted", String.valueOf(termsAccepted));
            if (timezone != null) fields.put("timezone", timezone);
            if (autoTimezone != null) fields.put("autoTimezone", String.valueOf(autoTimezone));
            if (returnToken != null) fields.put("returnToken", String.valueOf(returnToken));

            byte[] photoBytes = null;
            String filename = null;
            if (filePath != null) {
                Path path = Path.of(filePath);
                photoBytes = Files.readAllBytes(path);
                filename = path.getFileName().toString();
            }

            UserModels.LoginResponse response = apiClient.activateUser(fields, photoBytes, filename);
            String formattedOutput;
            if ("json".equalsIgnoreCase(format)) {
                formattedOutput = jsonFormatter.formatLoginResponse(response);
            } else if ("table".equalsIgnoreCase(format)) {
                formattedOutput = tableFormatter.formatLoginResponse(response);
            } else if ("md".equalsIgnoreCase(format)) {
                formattedOutput = markdownFormatter.formatLoginResponse(response);
            } else {
                throw new IllegalArgumentException("Unsupported format type: " + format);
            }
            spec.commandLine().getOut().println(formattedOutput);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read profile photo file: " + filePath, e);
        }
    }
}
