package com.circularo.cli.cli.documents;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Parent command group for all document-related subcommands.
 */
@Component
@Command(
        name = "documents",
        mixinStandardHelpOptions = true,
        header = "Search and manage Circularo documents.",
        description = "Provides access to document search and management operations.",
        subcommands = {
                DocumentsSearchCmd.class
        }
)
public class DocumentsCmd implements Runnable {

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
