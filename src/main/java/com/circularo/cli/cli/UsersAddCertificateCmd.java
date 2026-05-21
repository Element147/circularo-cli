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
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Component
@Command(
        name = "add-certificate",
        mixinStandardHelpOptions = true,
        header = "Add certificate to a user.",
        description = "Associates a new security certificate with the specified user account."
)
public class UsersAddCertificateCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final TableFormatter tableFormatter;
    private final JsonFormatter jsonFormatter;
    private final MarkdownFormatter markdownFormatter;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user")
    private String id;

    @Option(names = {"--name"}, required = true, description = "The name of the certificate")
    private String name;

    @Option(names = {"--type"}, required = true, description = "The type of the certificate (e.g. personal, signature)")
    private String type;

    @Option(names = {"--file"}, required = true, description = "Path to the local certificate file (which will be Base64-encoded)")
    private String filePath;

    @Option(names = {"-f", "--format"}, description = "Output format (table, json, md)", defaultValue = "table")
    private String format;

    @Autowired
    public UsersAddCertificateCmd(UsersApiClient apiClient, TableFormatter tableFormatter, JsonFormatter jsonFormatter, MarkdownFormatter markdownFormatter) {
        this.apiClient = apiClient;
        this.tableFormatter = tableFormatter;
        this.jsonFormatter = jsonFormatter;
        this.markdownFormatter = markdownFormatter;
    }

    @Override
    public void run() {
        try {
            byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
            String base64Cert = Base64.getEncoder().encodeToString(fileBytes);
            UserModels.AddCertificateRequest requestPayload = new UserModels.AddCertificateRequest(base64Cert, name, type);
            UserModels.GenericStatus status = apiClient.addCertificate(id, requestPayload);
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
        } catch (IOException e) {
            throw new RuntimeException("Failed to read certificate file: " + filePath, e);
        }
    }
}
