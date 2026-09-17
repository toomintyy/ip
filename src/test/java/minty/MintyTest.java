package minty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Minty's response API used by the graphical interface.
 */
public class MintyTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-17T00:00:00Z"), ZoneOffset.UTC);
    private static final String HEADER = "Here are your reminders for today and the next 6 days:";
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

    @Test
    public void reminders_boundariesAndTypes_returnsSortedOriginalNumbers() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String data = String.join("\n",
                "T | 0 | undated",
                "D | 0 | last day | 2026-09-23",
                "E | 0 | starts today | 2026-09-17 | 2026-09-25",
                "D | 0 | today | 2026-09-17",
                "D | 0 | outside | 2026-09-24",
                "D | 0 | overdue | 2026-09-16",
                "E | 0 | ongoing | 2026-09-16 | 2026-09-20",
                "D | 1 | completed | 2026-09-18",
                "E | 1 | completed event | 2026-09-18 | 2026-09-19");
        Files.writeString(file, data);
        Minty minty = new Minty(file, FIXED_CLOCK);
        String expected = HEADER + "\n"
                + "3.[E][ ] starts today (from: Sep 17 2026 to: Sep 25 2026)\n"
                + "4.[D][ ] today (by: Sep 17 2026)\n"
                + "2.[D][ ] last day (by: Sep 23 2026)";

        assertEquals(expected, minty.getStartupReminders());
        assertEquals(expected, minty.getResponse("reminders"));
        assertEquals(expected, minty.getResponse("remind"));
        assertEquals(data, Files.readString(file));
    }

    @Test
    public void reminders_taskLifecycle_updatesNextQuery() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"), FIXED_CLOCK);
        String empty = HEADER + "\nThere are no incomplete deadlines or events in this period.";
        assertEquals("", minty.getStartupReminders());
        assertEquals(empty, minty.getResponse("reminders"));
        minty.getResponse("deadline work /by 2026-09-17");
        String populated = HEADER + "\n1.[D][ ] work (by: Sep 17 2026)";
        assertEquals(populated, minty.getResponse("reminders"));
        minty.getResponse("mark 1");
        assertEquals(empty, minty.getResponse("reminders"));
        minty.getResponse("unmark 1");
        assertEquals(populated, minty.getResponse("reminders"));
        minty.getResponse("delete 1");
        assertEquals(empty, minty.getResponse("reminders"));
    }

    @Test
    public void reminders_invalidForms_returnsExistingError() {
        Minty minty = new Minty(temporaryDirectory.resolve("tasks.txt"), FIXED_CLOCK);
        for (String input : new String[] {"reminders 14", "remind 2", "Reminders", "reminder"}) {
            assertEquals("Sorry, I don't understand that command.", minty.getResponse(input));
        }
    }

    @Test
    public void run_savedUpcomingTask_showsStartupReminderBeforeGoodbye() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "D | 0 | work | 2026-09-17");
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("bye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new Minty(file, FIXED_CLOCK).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains("  " + HEADER + "\n  1.[D][ ] work (by: Sep 17 2026)\n"));
        assertTrue(console.indexOf(HEADER) > console.indexOf("What can I do for you today?"));
        assertTrue(console.indexOf(HEADER) < console.indexOf("Bye."));
    }
}
