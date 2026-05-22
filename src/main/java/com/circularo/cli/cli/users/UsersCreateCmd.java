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
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Command(
        name = "create",
        mixinStandardHelpOptions = true,
        header = "Create new user accounts bulk.",
        description = "Creates one or more user accounts using a JSON payload file."
)
public class UsersCreateCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;
    private final ObjectMapper objectMapper;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Option(names = {"--file"}, required = true, description = "Path to a local JSON file containing CreateUserRequest payload")
    private String filePath;

    @Autowired
    public UsersCreateCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            String content = Files.readString(Path.of(filePath));
            UserModels.CreateUserRequest payload = objectMapper.readValue(content, UserModels.CreateUserRequest.class);
            UserModels.BulkResponse response = apiClient.createUsers(payload);
            spec.commandLine().getOut().println(
                    output.format(formatMixin.getFormat(),
                            t -> t.formatBulkResponse(response),
                            j -> j.formatBulkResponse(response),
                            m -> m.formatBulkResponse(response))
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse file: " + filePath, e);
        }
    }
}
