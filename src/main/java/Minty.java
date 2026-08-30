import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
            CommandType commandType = CommandType.from(command);
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
                    printTasksOnDate(command, commandType, tasks, ui);
                    break;
                case MARK:
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    saveTasks(storage, tasks, ui);
                    ui.showTask("Nice! I've marked this task as done:", tasks.get(taskIndex));
                    break;
                case UNMARK:
                    taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    saveTasks(storage, tasks, ui);
                    ui.showTask("OK, I've marked this task as not done yet:", tasks.get(taskIndex));
                    break;
                case DELETE:
                    taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    saveTasks(storage, tasks, ui);
                    printTaskDeleted(deletedTask, tasks.size(), ui);
                    break;
                case TODO:
                    String description = getCommandArguments(command, commandType);
                    if (description.isEmpty()) {
                        throw new MintyException("Hmm, a todo needs a description.");
                    }
                    Task todo = new Todo(description);
                    tasks.add(todo);
                    saveTasks(storage, tasks, ui);
                    printTaskAdded(todo, tasks.size(), ui);
                    break;
                case DEADLINE:
                    Deadline deadline = parseDeadline(command);
                    tasks.add(deadline);
                    saveTasks(storage, tasks, ui);
                    printTaskAdded(deadline, tasks.size(), ui);
                    break;
                case EVENT:
                    Event event = parseEvent(command);
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
     * Parses and validates the task number supplied to a task-selection command.
     *
     * @param command complete command entered by the user
     * @param commandType task-selection command type
     * @param taskCount current number of stored tasks
     * @return zero-based index of the selected task
     * @throws MintyException if the task number is missing, non-numeric, or out of range
     */
    private static int parseTaskIndex(String command, CommandType commandType, int taskCount)
            throws MintyException {
        String commandWord = commandType.getCommandWord();
        String taskNumber = getCommandArguments(command, commandType);
        if (taskNumber.isEmpty()) {
            throw new MintyException("Please provide a task number to " + commandWord + ".");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new MintyException("The task number must be a whole number.");
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new MintyException("That task number is not in your list.");
        }
        return taskIndex;
    }

    /**
     * Returns the trimmed text following a command's identifying word.
     *
     * @param command complete command entered by the user
     * @param commandType recognized type of the command
     * @return trimmed command arguments, or an empty string if none were supplied
     */
    private static String getCommandArguments(String command, CommandType commandType) {
        return command.substring(commandType.getCommandWord().length()).trim();
    }

    /**
     * Creates a deadline after validating its description and {@code /by} value.
     *
     * @param command complete deadline command
     * @return validated deadline
     * @throws MintyException if any required deadline detail is missing
     */
    private static Deadline parseDeadline(String command) throws MintyException {
        String details = command.substring("deadline".length()).trim();
        int bySeparator = details.indexOf("/by");
        if (bySeparator < 0) {
            throw new MintyException("A deadline needs a /by date or time.");
        }

        String description = details.substring(0, bySeparator).trim();
        String byText = details.substring(bySeparator + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new MintyException("Hmm, a deadline needs a description.");
        }
        if (byText.isEmpty()) {
            throw new MintyException("Please say when the deadline is due after /by.");
        }
        LocalDate by = parseDate(byText, "deadline");
        return new Deadline(description, by);
    }

    /**
     * Creates an event after validating its description, start, and end values.
     *
     * @param command complete event command
     * @return validated event
     * @throws MintyException if any required event detail is missing or out of order
     */
    private static Event parseEvent(String command) throws MintyException {
        String details = command.substring("event".length()).trim();
        int fromSeparator = details.indexOf("/from");
        int toSeparator = details.indexOf("/to");

        if (toSeparator >= 0 && (fromSeparator < 0 || toSeparator < fromSeparator)) {
            throw new MintyException("Put /from before /to when adding an event.");
        }
        if (fromSeparator < 0) {
            throw new MintyException("An event needs a /from date or time.");
        }
        if (toSeparator < 0) {
            throw new MintyException("An event needs a /to date or time.");
        }

        String description = details.substring(0, fromSeparator).trim();
        String fromText = details.substring(fromSeparator + "/from".length(), toSeparator).trim();
        String toText = details.substring(toSeparator + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new MintyException("Hmm, an event needs a description.");
        }
        if (fromText.isEmpty()) {
            throw new MintyException("Please say when the event starts after /from.");
        }
        if (toText.isEmpty()) {
            throw new MintyException("Please say when the event ends after /to.");
        }
        LocalDate from = parseDate(fromText, "event start");
        LocalDate to = parseDate(toText, "event end");
        if (to.isBefore(from)) {
            throw new MintyException("The event end date cannot be before its start date.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses a date in Minty's required ISO format.
     *
     * @param dateText date entered by the user
     * @param dateName name used to identify the date in an error message
     * @return parsed date
     * @throws MintyException if the date is not a valid {@code yyyy-MM-dd} value
     */
    private static LocalDate parseDate(String dateText, String dateName) throws MintyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new MintyException("Please use yyyy-MM-dd for the " + dateName + " date.");
        }
    }

    /**
     * Prints dated tasks that occur on a requested date.
     *
     * @param command complete {@code on} command
     * @param commandType recognized command type
     * @param tasks current task list
     * @param ui command-line interface used to display matching tasks
     * @throws MintyException if the requested date is missing or invalid
     */
    private static void printTasksOnDate(String command, CommandType commandType,
            ArrayList<Task> tasks, Ui ui) throws MintyException {
        String dateText = getCommandArguments(command, commandType);
        if (dateText.isEmpty()) {
            throw new MintyException("Please provide a date after on.");
        }
        LocalDate date = parseDate(dateText, "requested");
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
