package minty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Minty's response API used by the graphical interface.
 */
public class MintyTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addThenList_returnsStoredTask() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));

        String addResponse = minty.getResponse("todo read book");
        String listResponse = minty.getResponse("list");

        assertTrue(addResponse.contains("I've added this task"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));

        String response = minty.getResponse("not a command");

        assertEquals("Sorry, I don't understand that command.", response);
    }

    @Test
    public void getResponse_bye_returnsGoodbyeMessage() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"));

        String response = minty.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
    }
}
