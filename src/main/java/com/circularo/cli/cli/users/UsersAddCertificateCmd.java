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
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

@Component
@Command(
        name = "add-certificate",
        mixinStandardHelpOptions = true,
        header = "Add a certificate to a user.",
        description = "Uploads and associates a certificate with the specified user."
)
public class UsersAddCertificateCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user")
    private String id;

    @Option(names = {"--name"}, required = true, description = "Certificate display name")
    private String certName;

    @Option(names = {"--type"}, required = true, description = "Certificate type (e.g. personal, qualified)")
    private String certType;

    @Option(names = {"--file"}, required = true, description = "Path to the certificate file")
    private String filePath;

    @Autowired
    public UsersAddCertificateCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        UserModels.AddCertificateRequest certRequest = new UserModels.AddCertificateRequest(filePath, certName, certType);
        UserModels.GenericStatus status = apiClient.addCertificate(id, certRequest);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatGenericStatus(status),
                        j -> j.formatGenericStatus(status),
                        m -> m.formatGenericStatus(status))
        );
    }
}
