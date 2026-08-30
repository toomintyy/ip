import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Runs Minty, a simple command-line chatbot.
 */
public class Minty {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Greets the user, stores tasks, lists stored tasks, updates task completion
     * statuses, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Path.of("data", "minty.txt"));
        ArrayList<Task> tasks = loadTasks(storage, ui);

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
                    printTasksOnDate(command, tasks, ui);
                    break;
                case MARK:
                    int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    saveTasks(storage, tasks, ui);
                    ui.showTask("Nice! I've marked this task as done:", tasks.get(taskIndex));
                    break;
                case UNMARK:
                    taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    saveTasks(storage, tasks, ui);
                    ui.showTask("OK, I've marked this task as not done yet:", tasks.get(taskIndex));
                    break;
                case DELETE:
                    taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    saveTasks(storage, tasks, ui);
                    printTaskDeleted(deletedTask, tasks.size(), ui);
                    break;
                case TODO:
                    Task todo = Parser.parseTodo(command);
                    tasks.add(todo);
                    saveTasks(storage, tasks, ui);
                    printTaskAdded(todo, tasks.size(), ui);
                    break;
                case DEADLINE:
                    Deadline deadline = Parser.parseDeadline(command);
                    tasks.add(deadline);
                    saveTasks(storage, tasks, ui);
                    printTaskAdded(deadline, tasks.size(), ui);
                    break;
                case EVENT:
                    Event event = Parser.parseEvent(command);
                    tasks.add(event);
                    saveTasks(storage, tasks, ui);
                    printTaskAdded(event, tasks.size(), ui);
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
     * @param tasks current task list
     * @param ui command-line interface used to display matching tasks
     * @throws MintyException if the requested date is missing or invalid
     */
    private static void printTasksOnDate(String command, ArrayList<Task> tasks, Ui ui)
            throws MintyException {
        LocalDate date = Parser.parseOnDate(command);
        ui.showMessage("Here are the tasks occurring on " + date.format(DISPLAY_DATE_FORMAT) + ":");

        int matchCount = 0;
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matchCount++;
                ui.showNumberedTask(matchCount, task);
            }
        }
        if (matchCount == 0) {
            ui.showMessage("There are no deadlines or events on this date.");
        }
    }

    /**
     * Prints the confirmation shown after a task is added.
     *
     * @param task task that was added
     * @param taskCount current number of stored tasks
     * @param ui command-line interface used to show the confirmation
     */
    private static void printTaskAdded(Task task, int taskCount, Ui ui) {
        ui.showTask("Got it. I've added this task:", task);
        ui.showTaskCount(taskCount);
    }

    /**
     * Prints the confirmation shown after a task is deleted.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks remaining
     * @param ui command-line interface used to show the confirmation
     */
    private static void printTaskDeleted(Task task, int taskCount, Ui ui) {
        ui.showTask("Noted. I've removed this task:", task);
        ui.showTaskCount(taskCount);
    }

    /**
     * Writes the current task list to disk.
     *
     * @param storage destination for the task data
     * @param tasks current task list
     * @param ui command-line interface used to report a save error
     */
    private static void saveTasks(Storage storage, ArrayList<Task> tasks, Ui ui) {
        try {
            storage.saveTasks(tasks);
        } catch (IOException exception) {
            ui.showError("I couldn't save the tasks: " + exception.getMessage());
        }
    }

    /**
     * Loads the saved task list, or starts with an empty list if reading fails.
     *
     * @param storage source of saved task data
     * @param ui command-line interface used to report a loading error
     * @return saved tasks, or an empty list when the file cannot be read
     */
    private static ArrayList<Task> loadTasks(Storage storage, Ui ui) {
        try {
            return storage.loadTasks();
        } catch (IOException | MintyException exception) {
            ui.showError("I couldn't load the tasks: " + exception.getMessage());
            return new ArrayList<>();
        }
    }
}
