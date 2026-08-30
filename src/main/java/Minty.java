import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Runs Minty, a simple command-line chatbot.
 */
public class Minty {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "  ";
    private static final String BANNER =
              "███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗\n"
            + "████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝\n"
            + "██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝\n"
            + "██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝\n"
            + "██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║\n"
            + "╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝\n";

    /**
     * Greets the user, stores tasks, lists stored tasks, updates task completion
     * statuses, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(Path.of("data", "minty.txt"));
        ArrayList<Task> tasks = loadTasks(storage);

        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println(INDENT + "Heyyy! I'm Feeling Minty.");
        System.out.println(INDENT + "What can I do for you today?");
        System.out.println(DIVIDER);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            CommandType commandType = CommandType.from(command);
            if (commandType == CommandType.BYE) {
                break;
            }
            System.out.println(DIVIDER);

            try {
                switch (commandType) {
                case LIST:
                    System.out.println(INDENT + "Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(INDENT + (i + 1) + "." + tasks.get(i));
                    }
                    break;
                case MARK:
                    int taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    saveTasks(storage, tasks);
                    System.out.println(INDENT + "Nice! I've marked this task as done:");
                    System.out.println(INDENT + INDENT + tasks.get(taskIndex));
                    break;
                case UNMARK:
                    taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    saveTasks(storage, tasks);
                    System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                    System.out.println(INDENT + INDENT + tasks.get(taskIndex));
                    break;
                case DELETE:
                    taskIndex = parseTaskIndex(command, commandType, tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    saveTasks(storage, tasks);
                    printTaskDeleted(deletedTask, tasks.size());
                    break;
                case TODO:
                    String description = getCommandArguments(command, commandType);
                    if (description.isEmpty()) {
                        throw new MintyException("Hmm, a todo needs a description.");
                    }
                    Task todo = new Todo(description);
                    tasks.add(todo);
                    saveTasks(storage, tasks);
                    printTaskAdded(todo, tasks.size());
                    break;
                case DEADLINE:
                    Deadline deadline = parseDeadline(command);
                    tasks.add(deadline);
                    saveTasks(storage, tasks);
                    printTaskAdded(deadline, tasks.size());
                    break;
                case EVENT:
                    Event event = parseEvent(command);
                    tasks.add(event);
                    saveTasks(storage, tasks);
                    printTaskAdded(event, tasks.size());
                    break;
                case BYE:
                case UNKNOWN:
                default:
                    throw new MintyException("Sorry, I don't understand that command.");
                }
            } catch (MintyException exception) {
                System.out.println(INDENT + exception.getMessage());
            }

            System.out.println(DIVIDER);
        }

        System.out.println(DIVIDER);
        System.out.println(INDENT + "Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
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
     * Prints the confirmation shown after a task is added.
     *
     * @param task task that was added
     * @param taskCount current number of stored tasks
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(INDENT + "Got it. I've added this task:");
        System.out.println(INDENT + INDENT + task);
        printTaskCount(taskCount);
    }

    /**
     * Prints the confirmation shown after a task is deleted.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks remaining
     */
    private static void printTaskDeleted(Task task, int taskCount) {
        System.out.println(INDENT + "Noted. I've removed this task:");
        System.out.println(INDENT + INDENT + task);
        printTaskCount(taskCount);
    }

    /**
     * Prints the current task count using the correct singular or plural noun.
     *
     * @param taskCount current number of stored tasks
     */
    private static void printTaskCount(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        System.out.println(INDENT + "Now you have " + taskCount + " " + taskNoun + " in the list.");
    }

    /**
     * Writes the current task list to disk.
     *
     * @param storage destination for the task data
     * @param tasks current task list
     */
    private static void saveTasks(Storage storage, ArrayList<Task> tasks) {
        try {
            storage.saveTasks(tasks);
        } catch (IOException exception) {
            System.out.println(INDENT + "I couldn't save the tasks: " + exception.getMessage());
        }
    }

    /**
     * Loads the saved task list, or starts with an empty list if reading fails.
     *
     * @param storage source of saved task data
     * @return saved tasks, or an empty list when the file cannot be read
     */
    private static ArrayList<Task> loadTasks(Storage storage) {
        try {
            return storage.loadTasks();
        } catch (IOException | MintyException exception) {
            System.out.println(INDENT + "I couldn't load the tasks: " + exception.getMessage());
            return new ArrayList<>();
        }
    }
}
