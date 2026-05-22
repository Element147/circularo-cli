package com.circularo.cli.cli;

import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Shared output formatting dispatcher for all CLI commands.
 * Wraps the three formatter implementations and provides a single
 * {@link #format(String, Function, Function, Function)} method that
 * replaces the copy-pasted if/else format dispatch chain.
 *
 * <p>Inject this component into every command instead of injecting
 * {@link TableFormatter}, {@link JsonFormatter}, and {@link MarkdownFormatter}
 * individually.</p>
 */
@Component
public class OutputHelper {

    private final TableFormatter table;
    private final JsonFormatter json;
    private final MarkdownFormatter md;

    @Autowired
    public OutputHelper(TableFormatter table, JsonFormatter json, MarkdownFormatter md) {
        this.table = table;
        this.json = json;
        this.md = md;
    }

    /**
     * Dispatches formatting to the appropriate formatter based on the format type string.
     *
     * @param formatType the format string (table, json, md)
     * @param tableFn    function that invokes the desired method on {@link TableFormatter}
     * @param jsonFn     function that invokes the desired method on {@link JsonFormatter}
     * @param mdFn       function that invokes the desired method on {@link MarkdownFormatter}
     * @return the formatted output string
     * @throws IllegalArgumentException if formatType is not recognized
     */
    public String format(String formatType,
                         Function<TableFormatter, String> tableFn,
                         Function<JsonFormatter, String> jsonFn,
                         Function<MarkdownFormatter, String> mdFn) {
        return switch (formatType.toLowerCase()) {
            case "table" -> tableFn.apply(table);
            case "json" -> jsonFn.apply(json);
            case "md" -> mdFn.apply(md);
            default -> throw new IllegalArgumentException("Unsupported format type: " + formatType);
        };
    }
}
