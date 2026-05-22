package com.circularo.cli.cli.users;

import com.circularo.cli.api.UsersApiClient;
import com.circularo.cli.cli.FormatMixin;
import com.circularo.cli.cli.OutputHelper;
import com.circularo.cli.model.UserModels;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        name = "update-single",
        mixinStandardHelpOptions = true,
        header = "Update a single user.",
        description = "Updates properties for the user with the specified ID."
)
public class UsersUpdateSingleCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;
    private final ObjectMapper objectMapper;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user to update")
    private String id;

    @Option(names = {"--file"}, required = true, description = "Path to a local JSON file containing UpdateUserRequest payload")
    private String filePath;

    @Option(names = {"--disable-active-tokens"}, description = "Disable active authentication tokens for the user")
    private Boolean disableActiveTokens;

    @Option(names = {"--version"}, description = "User resource version for optimistic locking")
    private Integer version;

    @Autowired
    public UsersUpdateSingleCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            String content = Files.readString(Path.of(filePath));
            UserModels.UpdateUserRequest payload = objectMapper.readValue(content, UserModels.UpdateUserRequest.class);
            UserModels.GenericStatus status = apiClient.updateUser(id, payload, disableActiveTokens, version);
            spec.commandLine().getOut().println(
                    output.format(formatMixin.getFormat(),
                            t -> t.formatGenericStatus(status),
                            j -> j.formatGenericStatus(status),
                            m -> m.formatGenericStatus(status))
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse file: " + filePath, e);
        }
    }
}
