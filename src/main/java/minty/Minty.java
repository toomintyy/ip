package minty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import minty.command.Command;
import minty.command.Parser;
import minty.command.RemindersCommand;
import minty.exception.MintyException;
import minty.storage.Storage;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Runs Minty, a simple command-line chatbot.
 */
public class Minty {
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final Clock clock;

    /**
     * Creates Minty with task storage at the specified path.
     *
     * @param filePath path to the task data file.
     */
    public Minty(Path filePath) {
        this(filePath, Clock.systemDefaultZone());
    }

    /**
     * Creates Minty with a supplied clock for repeatable reminder tests.
     *
     * @param filePath task storage path.
     * @param clock source of the current local date.
     */
    Minty(Path filePath, Clock clock) {
        this.clock = clock;
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = loadTasks();
    }

    /**
     * Executes one command and returns Minty's response for a graphical interface.
     *
     * @param input complete command entered by the user.
     * @return Minty's response without command-line dividers.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        PrintStream responseOutput = new PrintStream(responseBuffer, true, StandardCharsets.UTF_8);
        Ui responseUi = new Ui(responseOutput);
        Command command = Parser.parse(input, clock);

        try {
            command.execute(tasks, responseUi, storage);
            if (command.isExit()) {
                responseUi.showMessage("Stay fresh! Catch you next time!");
            }
        } catch (MintyException exception) {
            responseUi.showError(exception.getMessage());
        }

        String response = responseBuffer.toString(StandardCharsets.UTF_8);
        if (command instanceof RemindersCommand) {
            return response.stripTrailing().stripIndent().strip();
        }
        return response.stripIndent().strip();
    }

    /**
     * Starts Minty using the default relative, OS-independent data path.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Minty(Path.of("data", "minty.txt")).run();
    }

    /**
     * Runs Minty's command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        List<TaskList.NumberedTask> reminders = tasks.findReminders(LocalDate.now(clock));
        if (!reminders.isEmpty()) {
            ui.showDivider();
            RemindersCommand.showReminders(reminders, ui);
            ui.showDivider();
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            Command command = Parser.parse(ui.readCommand(), clock);
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
     * @return saved tasks, or an empty list when the file cannot be read.
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.loadTasks());
        } catch (IOException | MintyException exception) {
            ui.showError("I've hit a snag loading your saved tasks. Starting with an empty list for"
                    + " this session. Details: " + exception.getMessage());
            return new TaskList();
        }
    }

    /**
     * Returns the startup reminder message, or an empty string when no tasks qualify.
     *
     * @return optional startup chat text.
     */
    public String getStartupReminders() {
        List<TaskList.NumberedTask> reminders = tasks.findReminders(LocalDate.now(clock));
        if (reminders.isEmpty()) {
            return "";
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Ui responseUi = new Ui(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        RemindersCommand.showReminders(reminders, responseUi);
        return buffer.toString(StandardCharsets.UTF_8).stripTrailing().stripIndent().strip();
    }
}
