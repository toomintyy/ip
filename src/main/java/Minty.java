import java.util.Scanner;

/**
 * Runs Minty, a simple command-line chatbot.
 */
public class Minty {
    private static final int MAX_TASKS = 100;
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
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println(INDENT + "Heyyy! I'm Feeling Minty.");
        System.out.println(INDENT + "What can I do for you today?");
        System.out.println(DIVIDER);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                break;
            }
            System.out.println(DIVIDER);

            try {
                if (command.equals("list")) {
                    System.out.println(INDENT + "Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println(INDENT + (i + 1) + "." + tasks[i]);
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskIndex = parseTaskIndex(command, "mark", taskCount);
                    tasks[taskIndex].markAsDone();
                    System.out.println(INDENT + "Nice! I've marked this task as done:");
                    System.out.println(INDENT + INDENT + tasks[taskIndex]);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskIndex = parseTaskIndex(command, "unmark", taskCount);
                    tasks[taskIndex].markAsNotDone();
                    System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                    System.out.println(INDENT + INDENT + tasks[taskIndex]);
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new MintyException("Hmm, a todo needs a description.");
                    }
                    ensureTaskCapacity(taskCount);
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    printTaskAdded(tasks[taskCount - 1], taskCount);
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    Deadline deadline = parseDeadline(command);
                    ensureTaskCapacity(taskCount);
                    tasks[taskCount] = deadline;
                    taskCount++;
                    printTaskAdded(tasks[taskCount - 1], taskCount);
                } else if (command.equals("event") || command.startsWith("event ")) {
                    Event event = parseEvent(command);
                    ensureTaskCapacity(taskCount);
                    tasks[taskCount] = event;
                    taskCount++;
                    printTaskAdded(tasks[taskCount - 1], taskCount);
                } else {
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
     * Parses and validates the task number supplied to a mark or unmark command.
     *
     * @param command complete command entered by the user
     * @param commandWord command name, either {@code mark} or {@code unmark}
     * @param taskCount current number of stored tasks
     * @return zero-based index of the selected task
     * @throws MintyException if the task number is missing, non-numeric, or out of range
     */
    private static int parseTaskIndex(String command, String commandWord, int taskCount)
            throws MintyException {
        String taskNumber = command.substring(commandWord.length()).trim();
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
        String by = details.substring(bySeparator + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new MintyException("Hmm, a deadline needs a description.");
        }
        if (by.isEmpty()) {
            throw new MintyException("Please say when the deadline is due after /by.");
        }
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
        String from = details.substring(fromSeparator + "/from".length(), toSeparator).trim();
        String to = details.substring(toSeparator + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new MintyException("Hmm, an event needs a description.");
        }
        if (from.isEmpty()) {
            throw new MintyException("Please say when the event starts after /from.");
        }
        if (to.isEmpty()) {
            throw new MintyException("Please say when the event ends after /to.");
        }
        return new Event(description, from, to);
    }

    /**
     * Ensures another task can be stored in Minty's fixed-size task list.
     *
     * @param taskCount current number of stored tasks
     * @throws MintyException if the list is already full
     */
    private static void ensureTaskCapacity(int taskCount) throws MintyException {
        if (taskCount >= MAX_TASKS) {
            throw new MintyException("Your task list is full, so I can't add another task.");
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
        System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
    }
}
