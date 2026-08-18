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
     * Greets the user, echoes commands, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println(INDENT + "Heyyy! I'm Feeling Minty.");
        System.out.println(INDENT + "What can I do for you today?");
        System.out.println(DIVIDER);

        String command = scanner.nextLine();
        while (!command.equals("bye")) {
            System.out.println(DIVIDER);
            System.out.println(INDENT + command);
            System.out.println(DIVIDER);
            command = scanner.nextLine();
        }

        System.out.println(DIVIDER);
        System.out.println(INDENT + "Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
