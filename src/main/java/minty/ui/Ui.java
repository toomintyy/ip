package minty.ui;

import java.io.PrintStream;
import java.util.Scanner;

import minty.task.Task;

/**
 * Handles Minty's command-line input and output.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENT = "  ";
    private static final String BANNER =
            "███╗   ███╗██╗███╗   ██╗████"
                    + "████╗██╗   ██╗\n"
                    + "████╗ ████║██║████╗  ██║╚══██"
                    + "╔══╝╚██╗ ██╔╝\n"
                    + "██╔████╔██║██║██╔██╗ ██║   ██"
                    + "║    ╚████╔╝\n"
                    + "██║╚██╔╝██║██║██║╚██╗██║   ██"
                    + "║     ╚██╔╝\n"
                    + "██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║\n"
                    + "╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝\n";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.output = System.out;
    }

    /**
     * Creates a UI that writes responses to the specified output stream.
     *
     * @param output destination for responses.
     */
    public Ui(PrintStream output) {
        this.scanner = null;
        this.output = output;
    }

    /**
     * Returns whether another command is available.
     *
     * @return true when standard input contains another line.
     */
    public boolean hasNextCommand() {
        assert scanner != null : "A command-line scanner is required to read input";
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return command entered by the user.
     */
    public String readCommand() {
        assert scanner != null : "A command-line scanner is required to read input";
        return scanner.nextLine();
    }

    /**
     * Shows Minty's greeting.
     */
    public void showWelcome() {
        output.println(DIVIDER);
        output.print(BANNER);
        output.println(INDENT + "Heyyy! I'm Feeling Minty.");
        output.println(INDENT + "What can I do for you today?");
        output.println(DIVIDER);
    }

    /**
     * Shows Minty's farewell.
     */
    public void showGoodbye() {
        output.println(DIVIDER);
        output.println(INDENT + "Bye. Hope to see you again soon!");
        output.println(DIVIDER);
    }

    /**
     * Shows a divider between commands and responses.
     */
    public void showDivider() {
        output.println(DIVIDER);
    }

    /**
     * Shows an error message using Minty's standard indentation.
     *
     * @param message error details to show.
     */
    public void showError(String message) {
        output.println(INDENT + message);
    }

    /**
     * Shows every task in the list with a one-based number.
     *
     * @param tasks tasks to show.
     */
    public void showTaskList(Iterable<Task> tasks) {
        output.println(INDENT + "Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param tasks matching tasks to show.
     */
    public void showMatchingTasks(Iterable<Task> tasks) {
        output.println(INDENT + "Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Shows tasks with one-based numbers.
     *
     * @param tasks tasks to show.
     */
    private void showNumberedTasks(Iterable<Task> tasks) {
        int taskNumber = 1;
        for (Task task : tasks) {
            output.println(INDENT + taskNumber + "." + task);
            taskNumber++;
        }
    }

    /**
     * Shows a message followed by one indented task.
     *
     * @param message confirmation message.
     * @param task task affected by the command.
     */
    public void showTask(String message, Task task) {
        output.println(INDENT + message);
        output.println(INDENT + INDENT + task);
    }

    /**
     * Shows the current number of tasks with correct singular or plural wording.
     *
     * @param taskCount current number of tasks.
     */
    public void showTaskCount(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        output.println(INDENT + "Now you have " + taskCount + " "
                + taskNoun + " in the list.");
    }

    /**
     * Shows a normal response line using Minty's standard indentation.
     *
     * @param message response to show.
     */
    public void showMessage(String message) {
        output.println(INDENT + message);
    }

    /**
     * Shows a numbered task within a filtered result.
     *
     * @param number one-based result number.
     * @param task matching task.
     */
    public void showNumberedTask(int number, Task task) {
        output.println(INDENT + number + "." + task);
    }
}
