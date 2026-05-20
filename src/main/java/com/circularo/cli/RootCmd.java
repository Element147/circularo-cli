package com.circularo.cli;

import com.circularo.cli.cli.DocumentsCmd;
import com.circularo.cli.cli.ReportsCmd;
import com.circularo.cli.cli.TransactionsCmd;
import com.circularo.cli.cli.UsersCmd;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Component
@Command(
        name = "circularo-cli",
        mixinStandardHelpOptions = true,
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
            "  - Find all internal users:           circularo-cli users",
            "  - Query users by email or name:      circularo-cli users --query \"mail:*example.com\"",
            "  - Search documents by query string:  circularo-cli documents --query \"title:Invoice*\"",
            "  - Search reports:                    circularo-cli reports --query \"title:Invoice*\"",
            "  - Filter completed transactions:      circularo-cli transactions --state completed",
            "  - Output results in JSON format:     circularo-cli users --format json"
        },
        footer = {
            "",
            "Use 'circularo-cli <command> --help' for details on specific sub-commands.",
            "Read 'docs/user_manual.md' for comprehensive query syntax guide and troubleshooting."
        },
        subcommands = {UsersCmd.class, DocumentsCmd.class, ReportsCmd.class, TransactionsCmd.class}
)
public class RootCmd implements Callable<Integer> {

    @Override
    public Integer call() {
        // Invoked when no sub-command is provided; print usage hint.
        System.out.println("Use --help to see available sub-commands.");
        return 0;
    }
}
