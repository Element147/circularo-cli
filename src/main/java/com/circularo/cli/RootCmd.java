package com.circularo.cli;

import picocli.CommandLine.Command;
import org.springframework.stereotype.Component;
import java.util.concurrent.Callable;

@Component
@Command(name = "circularo-cli", mixinStandardHelpOptions = true, description = "Circularo CLI Search Utility")
public class RootCmd implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Circularo CLI executed successfully.");
        return 0;
    }
}
