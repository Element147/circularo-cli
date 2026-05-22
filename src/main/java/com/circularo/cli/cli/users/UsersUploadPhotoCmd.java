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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Command(
        name = "upload-photo",
        mixinStandardHelpOptions = true,
        header = "Upload user profile photo.",
        description = "Uploads a profile image file for the specified user ID."
)
public class UsersUploadPhotoCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user")
    private String id;

    @Option(names = {"--file"}, required = true, description = "Path to the local profile photo image file")
    private String filePath;

    @Autowired
    public UsersUploadPhotoCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            Path path = Path.of(filePath);
            byte[] bytes = Files.readAllBytes(path);
            String filename = path.getFileName().toString();
            UserModels.UserConfiguration config = apiClient.uploadPhoto(id, bytes, filename);
            spec.commandLine().getOut().println(
                    output.format(formatMixin.getFormat(),
                            t -> t.formatUserConfiguration(config),
                            j -> j.formatUserConfiguration(config),
                            m -> m.formatUserConfiguration(config))
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file: " + filePath, e);
        }
    }
}
