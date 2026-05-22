package com.circularo.cli.cli.reports;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Parent command group for all report-related subcommands.
 */
@Component
@Command(
        name = "reports",
        mixinStandardHelpOptions = true,
        header = "Generate and search document workflow reports.",
        description = "Provides access to workflow report search and analysis operations.",
        subcommands = {
                ReportsSearchCmd.class
        }
)
public class ReportsCmd implements Runnable {

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
