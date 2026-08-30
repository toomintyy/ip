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
     * Creates storage that writes to the specified file.
     *
     * @param filePath path to the task data file
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the save file.
     *
     * <p>An empty task list is returned when Minty has not created a save file yet.
     *
     * @return tasks reconstructed from the save file
     * @throws IOException if an existing save file cannot be read
     */
    public ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String taskData : Files.readAllLines(filePath)) {
            String[] fields = taskData.split(" \\| ");
            Task task = createTask(fields);
            if (fields[1].equals("1")) {
                task.markAsDone();
            }
            tasks.add(task);
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
     * Reconstructs a task from its saved fields.
     *
     * @param fields fields from one line of the save file
     * @return reconstructed task
     */
    private Task createTask(String[] fields) {
        switch (fields[0]) {
        case "T":
            return new Todo(fields[2]);
        case "D":
            return new Deadline(fields[2], fields[3]);
        case "E":
            return new Event(fields[2], fields[3], fields[4]);
        default:
            throw new IllegalArgumentException("Unknown saved task type: " + fields[0]);
        }
    }
}
