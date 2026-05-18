package com.circularo.cli;

import com.circularo.cli.cli.DocumentsCmd;
import com.circularo.cli.cli.UsersCmd;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Component
@Command(
        name = "circularo-cli",
        mixinStandardHelpOptions = true,
        description = "Circularo CLI Search Utility",
        subcommands = {UsersCmd.class, DocumentsCmd.class}
)
public class RootCmd implements Callable<Integer> {

    @Override
    public Integer call() {
        // Invoked when no sub-command is provided; print usage hint.
        System.out.println("Use --help to see available sub-commands.");
        return 0;
    }
}
