import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Loads and saves Minty's task list in a local text file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the specified file.
     *
     * @param filePath path to the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the save file.
     *
     * <p>An empty task list is returned when Minty has not created a save file yet.
     *
     * @return tasks reconstructed from the save file
     * @throws IOException if an existing save file cannot be read
     * @throws MintyException if the save file contains invalid task data
     */
    public ArrayList<Task> loadTasks() throws IOException, MintyException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        int lineNumber = 0;
        for (String taskData : Files.readAllLines(filePath)) {
            lineNumber++;
            if (taskData.isBlank()) {
                continue;
            }
            tasks.add(parseTask(taskData, lineNumber));
        }
        return tasks;
    }

    /**
     * Replaces the save file with the current task list.
     *
     * @param tasks tasks to save
     * @throws IOException if the data directory or file cannot be written
     */
    public void saveTasks(ArrayList<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> taskData = new ArrayList<>();
        for (Task task : tasks) {
            taskData.add(task.toDataString());
        }
        Files.write(filePath, taskData);
    }

    /**
     * Reconstructs and validates a task from one save-file line.
     *
     * @param taskData one line from the save file
     * @param lineNumber one-based line number used in error messages
     * @return reconstructed task
     * @throws MintyException if the saved task is malformed
     */
    private Task parseTask(String taskData, int lineNumber) throws MintyException {
        ArrayList<String> fields = splitFields(taskData, lineNumber);
        if (fields.size() < 2) {
            throw invalidLine(lineNumber, "missing task fields");
        }

        int expectedFieldCount;
        switch (fields.get(0)) {
        case "T":
            expectedFieldCount = 3;
            break;
        case "D":
            expectedFieldCount = 4;
            break;
        case "E":
            expectedFieldCount = 5;
            break;
        default:
            throw invalidLine(lineNumber, "unknown task type '" + fields.get(0) + "'");
        }

        if (fields.size() != expectedFieldCount) {
            throw invalidLine(lineNumber, "expected " + expectedFieldCount
                    + " fields but found " + fields.size());
        }
        if (!fields.get(1).equals("0") && !fields.get(1).equals("1")) {
            throw invalidLine(lineNumber, "status must be 0 or 1");
        }
        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isEmpty()) {
                throw invalidLine(lineNumber, "task details cannot be empty");
            }
        }

        Task task;
        switch (fields.get(0)) {
        case "T":
            task = new Todo(fields.get(2));
            break;
        case "D":
            task = new Deadline(fields.get(2), fields.get(3));
            break;
        case "E":
            task = new Event(fields.get(2), fields.get(3), fields.get(4));
            break;
        default:
            throw new AssertionError("Task type was already validated");
        }
        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits fields at unescaped pipe characters and removes format padding.
     *
     * @param taskData serialized task
     * @param lineNumber one-based line number used in error messages
     * @return unescaped task fields
     * @throws MintyException if an escape sequence is incomplete or unsupported
     */
    private ArrayList<String> splitFields(String taskData, int lineNumber) throws MintyException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < taskData.length(); i++) {
            char character = taskData.charAt(i);
            if (isEscaped) {
                if (character != '\\' && character != '|') {
                    throw invalidLine(lineNumber, "unsupported escape sequence '\\"
                            + character + "'");
                }
                currentField.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }

        if (isEscaped) {
            throw invalidLine(lineNumber, "unfinished escape character");
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    /**
     * Creates a consistent, user-friendly error for malformed saved data.
     *
     * @param lineNumber line containing the error
     * @param reason explanation of the invalid data
     * @return Minty-specific exception
     */
    private MintyException invalidLine(int lineNumber, String reason) {
        return new MintyException("Invalid data on line " + lineNumber + ": " + reason + ".");
    }
}
