package minty.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import minty.exception.MintyException;
import minty.task.Deadline;
import minty.task.Event;
import minty.task.Task;
import minty.task.Todo;

/**
 * Tests saving and loading task data without touching the project's real data file.
 */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws IOException, MintyException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypes_preservesTaskData()
            throws IOException, MintyException {
        Path file = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(file);
        Todo todo = new Todo("read | review \\ notes");
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 9, 10));
        deadline.markAsDone();
        Event event = new Event("conference", LocalDate.of(2026, 9, 11),
                LocalDate.of(2026, 9, 12));
        ArrayList<Task> originalTasks = new ArrayList<>(List.of(todo, deadline, event));

        storage.saveTasks(originalTasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(originalTasks.stream().map(Task::toDataString).toList(),
                loadedTasks.stream().map(Task::toDataString).toList());
    }

    @Test
    public void loadTasks_blankLines_ignoresBlankLines() throws IOException, MintyException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("", "T | 0 | read book", "   "));

        ArrayList<Task> tasks = new Storage(file).loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
    }

    @Test
    public void loadTasks_unknownTaskType_throwsMintyExceptionWithLineNumber()
            throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("T | 0 | valid", "X | 0 | invalid"));

        MintyException exception = assertThrows(MintyException.class,
                () -> new Storage(file).loadTasks());

        assertEquals("Invalid data on line 2: unknown task type 'X'.",
                exception.getMessage());
    }

    @Test
    public void loadTasks_invalidStatus_throwsMintyException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 2 | read book");

        assertThrows(MintyException.class, () -> new Storage(file).loadTasks());
    }

    @Test
    public void loadTasks_invalidDate_throwsMintyException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "D | 0 | submit | 10-09-2026");

        assertThrows(MintyException.class, () -> new Storage(file).loadTasks());
    }

    @Test
    public void loadTasks_eventEndBeforeStart_throwsMintyException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "E | 0 | conference | 2026-09-12 | 2026-09-10");

        assertThrows(MintyException.class, () -> new Storage(file).loadTasks());
    }

    @Test
    public void loadTasks_unsupportedEscapeSequence_throwsMintyException() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | invalid\\qdescription");

        assertThrows(MintyException.class, () -> new Storage(file).loadTasks());
    }
}
