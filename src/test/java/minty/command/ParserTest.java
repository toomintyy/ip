package minty.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import minty.exception.MintyException;

/**
 * Tests command parsing behavior.
 */
public class ParserTest {

    @Test
    public void parseTaskIndex_validTaskNumber_returnsZeroBasedIndex()
            throws MintyException {
        int actualIndex =
                Parser.parseTaskIndex("mark 2", CommandType.MARK, 3);

        assertEquals(1, actualIndex);
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_throwsMintyException() {
        assertThrows(MintyException.class, () ->
                Parser.parseTaskIndex("mark", CommandType.MARK, 3));
    }
}
