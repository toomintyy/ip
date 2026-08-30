import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Runs Minty, a simple command-line chatbot.
 */
public class Minty {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Creates Minty with task storage at the specified path.
     *
     * @param filePath path to the task data file
     */
    public Minty(Path filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = loadTasks();
    }

    /**
     * Starts Minty using the default relative, OS-independent data path.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Minty(Path.of("data", "minty.txt")).run();
    }

    /**
     * Runs Minty's command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            CommandType commandType = Parser.parseCommandType(command);
            if (commandType == CommandType.BYE) {
                break;
            }
            ui.showDivider();

            try {
                switch (commandType) {
                case LIST:
                    ui.showTaskList(tasks);
                    break;
                case ON:
                    printTasksOnDate(command);
                    break;
                case MARK:
                    int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    Task markedTask = tasks.mark(taskIndex);
                    saveTasks();
                    ui.showTask("Nice! I've marked this task as done:", markedTask);
                    break;
                case UNMARK:
                    taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    Task unmarkedTask = tasks.unmark(taskIndex);
                    saveTasks();
                    ui.showTask("OK, I've marked this task as not done yet:", unmarkedTask);
                    break;
                case DELETE:
                    taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    Task deletedTask = tasks.delete(taskIndex);
                    saveTasks();
                    printTaskDeleted(deletedTask);
                    break;
                case TODO:
                    Task todo = Parser.parseTodo(command);
                    tasks.add(todo);
                    saveTasks();
                    printTaskAdded(todo);
                    break;
                case DEADLINE:
                    Deadline deadline = Parser.parseDeadline(command);
                    tasks.add(deadline);
                    saveTasks();
                    printTaskAdded(deadline);
                    break;
                case EVENT:
                    Event event = Parser.parseEvent(command);
                    tasks.add(event);
                    saveTasks();
                    printTaskAdded(event);
                    break;
                case BYE:
                case UNKNOWN:
                default:
                    throw new MintyException("Sorry, I don't understand that command.");
                }
            } catch (MintyException exception) {
                ui.showError(exception.getMessage());
            }

            ui.showDivider();
        }

        ui.showGoodbye();
    }

    /**
     * Prints dated tasks that occur on a requested date.
     *
     * @param command complete {@code on} command
     * @throws MintyException if the requested date is missing or invalid
     */
    private void printTasksOnDate(String command) throws MintyException {
        LocalDate date = Parser.parseOnDate(command);
        ui.showMessage("Here are the tasks occurring on " + date.format(DISPLAY_DATE_FORMAT) + ":");

        int matchCount = 0;
        for (Task task : tasks.findOn(date)) {
            matchCount++;
            ui.showNumberedTask(matchCount, task);
        }
        if (matchCount == 0) {
            ui.showMessage("There are no deadlines or events on this date.");
        }
    }

    /**
     * Prints the confirmation shown after a task is added.
     *
     * @param task task that was added
     */
    private void printTaskAdded(Task task) {
        ui.showTask("Got it. I've added this task:", task);
        ui.showTaskCount(tasks.size());
    }

    /**
     * Prints the confirmation shown after a task is deleted.
     *
     * @param task task that was deleted
     */
    private void printTaskDeleted(Task task) {
        ui.showTask("Noted. I've removed this task:", task);
        ui.showTaskCount(tasks.size());
    }

    /**
     * Writes the current task list to disk.
     */
    private void saveTasks() {
        try {
            storage.saveTasks(tasks);
        } catch (IOException exception) {
            ui.showError("I couldn't save the tasks: " + exception.getMessage());
        }
    }

    /**
     * Loads the saved task list, or starts with an empty list if reading fails.
     *
     * @return saved tasks, or an empty list when the file cannot be read
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException | MintyException exception) {
            ui.showError("I couldn't load the tasks: " + exception.getMessage());
            return new TaskList();
        }
    }
}
