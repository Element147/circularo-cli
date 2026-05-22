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

import java.util.List;

@Component
@Command(
        name = "rights",
        mixinStandardHelpOptions = true,
        header = "Get user rights.",
        description = "Retrieves the access control lists and rights assigned to the user."
)
public class UsersRightsCmd implements Runnable {

    private final UsersApiClient apiClient;
    private final OutputHelper output;

    @Mixin
    private FormatMixin formatMixin;

    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", description = "The ID of the user")
    private String id;

    @Option(names = {"--detailed"}, description = "Include detailed ACL definitions")
    private Boolean detailed;

    @Autowired
    public UsersRightsCmd(UsersApiClient apiClient, OutputHelper output) {
        this.apiClient = apiClient;
        this.output = output;
    }

    @Override
    public void run() {
        List<UserModels.UserRightItem> rights = apiClient.getUserRights(id, detailed);
        spec.commandLine().getOut().println(
                output.format(formatMixin.getFormat(),
                        t -> t.formatUserRights(rights),
                        j -> j.formatUserRights(rights),
                        m -> m.formatUserRights(rights))
        );
    }
}
