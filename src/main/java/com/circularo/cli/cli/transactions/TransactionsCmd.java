package com.circularo.cli.cli.transactions;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Parent command group for all transaction-related subcommands.
 */
@Component
@Command(
        name = "transactions",
        mixinStandardHelpOptions = true,
        header = "Search and filter document workflow transactions.",
        description = "Provides access to transaction search and filtering operations.",
        subcommands = {
                TransactionsSearchCmd.class
        }
)
public class TransactionsCmd implements Runnable {

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
