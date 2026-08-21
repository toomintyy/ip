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

        String command = scanner.nextLine();
        while (!command.equals("bye")) {
            System.out.println(DIVIDER);

            if (command.equals("list")) {
                System.out.println(INDENT + "Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(INDENT + (i + 1) + "." + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(command.substring(5)) - 1;
                tasks[taskIndex].markAsDone();
                System.out.println(INDENT + "Nice! I've marked this task as done:");
                System.out.println(INDENT + INDENT + tasks[taskIndex]);
            } else if (command.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(command.substring(7)) - 1;
                tasks[taskIndex].markAsNotDone();
                System.out.println(INDENT + "OK, I've marked this task as not done yet:");
                System.out.println(INDENT + INDENT + tasks[taskIndex]);
            } else if (command.equals("todo") || command.startsWith("todo ")) {
                String description = command.substring(4).trim();
                if (description.isEmpty()) {
                    System.out.println(INDENT + "Hmm, a todo needs a description.");
                } else {
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    printTaskAdded(tasks[taskCount - 1], taskCount);
                }
            } else if (command.startsWith("deadline ")) {
                String details = command.substring(9);
                int bySeparator = details.indexOf(" /by ");
                String description = details.substring(0, bySeparator);
                String by = details.substring(bySeparator + 5);
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else if (command.startsWith("event ")) {
                String details = command.substring(6);
                int fromSeparator = details.indexOf(" /from ");
                int toSeparator = details.indexOf(" /to ", fromSeparator + 7);
                String description = details.substring(0, fromSeparator);
                String from = details.substring(fromSeparator + 7, toSeparator);
                String to = details.substring(toSeparator + 5);
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else {
                System.out.println(INDENT + "Sorry, I don't understand that command.");
            }

            System.out.println(DIVIDER);
            command = scanner.nextLine();
        }

        System.out.println(DIVIDER);
        System.out.println(INDENT + "Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
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
