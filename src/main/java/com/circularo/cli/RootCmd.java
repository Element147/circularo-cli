package com.circularo.cli;

import com.circularo.cli.cli.documents.DocumentsCmd;
import com.circularo.cli.cli.reports.ReportsCmd;
import com.circularo.cli.cli.transactions.TransactionsCmd;
import com.circularo.cli.cli.users.UsersCmd;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.util.concurrent.Callable;

@Component
@Command(
        name = "circularo-cli",
        mixinStandardHelpOptions = true,
        version = {"circularo-cli 1.0.0"},
        header = {
            "==================================================",
            "        CIRCULARO CLI SEARCH UTILITY v1.0.0",
            "=================================================="
        },
        description = {
            "",
            "Query and retrieve resources from your Circularo tenant directly from the command line.",
            "Requires API URL and API Token configured in a local '.env' file.",
            "",
            "Quick Examples:",
            "  - Find all internal users:           circularo-cli users search",
            "  - Query users by email or name:      circularo-cli users search --query \"mail:*example.com\"",
            "  - Search documents by query string:  circularo-cli documents search --query \"title:Invoice*\"",
            "  - Search reports:                    circularo-cli reports search --query \"title:Invoice*\"",
            "  - Filter completed transactions:      circularo-cli transactions search --state completed",
            "  - Output results in JSON format:     circularo-cli users search --format json"
        },
        footer = {
            "",
            "Use 'circularo-cli <command> --help' for details on specific sub-commands.",
            "Read 'docs/user_manual.md' for comprehensive query syntax guide and troubleshooting."
        },
        subcommands = {UsersCmd.class, DocumentsCmd.class, ReportsCmd.class, TransactionsCmd.class}
)
public class RootCmd implements Callable<Integer> {

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        // Invoked when no sub-command is provided; print full usage to guide the user.
        spec.commandLine().usage(spec.commandLine().getOut());
        return 0;
    }
}
