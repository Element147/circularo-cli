package com.circularo.cli.cli;

import com.circularo.cli.format.JsonFormatter;
import com.circularo.cli.format.MarkdownFormatter;
import com.circularo.cli.format.TableFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OutputHelperTest {

    private final OutputHelper helper = new OutputHelper(
            new TableFormatter(), new JsonFormatter(), new MarkdownFormatter());

    @Test
    void format_withTableType_invokesTableFunction() {
        String result = helper.format("table",
                t -> "table-result",
                j -> "json-result",
                m -> "md-result");
        assertEquals("table-result", result);
    }

    @Test
    void format_withJsonType_invokesJsonFunction() {
        String result = helper.format("json",
                t -> "table-result",
                j -> "json-result",
                m -> "md-result");
        assertEquals("json-result", result);
    }

    @Test
    void format_withMdType_invokesMdFunction() {
        String result = helper.format("md",
                t -> "table-result",
                j -> "json-result",
                m -> "md-result");
        assertEquals("md-result", result);
    }

    @Test
    void format_withUnknownType_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                helper.format("csv",
                        t -> "table-result",
                        j -> "json-result",
                        m -> "md-result"));
    }

    @Test
    void format_isCaseInsensitive() {
        String result = helper.format("JSON",
                t -> "table-result",
                j -> "json-result",
                m -> "md-result");
        assertEquals("json-result", result);
    }
}
