package minty;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Runs Minty, a simple command-line chatbot.
 */
public class Minty {
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

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            Command command = Parser.parse(ui.readCommand());
            if (!command.isExit()) {
                ui.showDivider();
            }

            try {
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (MintyException exception) {
                ui.showError(exception.getMessage());
            }

            if (!command.isExit()) {
                ui.showDivider();
            }
        }

        ui.showGoodbye();
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
