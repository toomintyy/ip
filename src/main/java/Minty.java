/**
 * Runs Minty, a simple command-line chatbot.
 */
public class Minty {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = "███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗\n"
            + "████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝\n"
            + "██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝\n"
            + "██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝\n"
            + "██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║\n"
            + "╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝\n";

    /**
     * Greets the user and exits with a farewell message.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println("Hello! I'm Minty.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }
}
