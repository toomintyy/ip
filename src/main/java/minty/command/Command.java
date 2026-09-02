package minty.command;

import java.io.IOException;

import minty.exception.MintyException;
import minty.storage.Storage;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Represents an executable command understood by Minty.
 */
public abstract class Command {

    /**
     * Performs this command using Minty's application components.
     *
     * @param tasks task list to query or update.
     * @param ui command-line interface used for responses.
     * @param storage task storage used for persistence.
     * @throws MintyException if the command cannot be executed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException;

    /**
     * Indicates whether Minty should stop after this command.
     *
     * @return {@code true} only for an exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the task list and reports a recoverable file error to the user.
     *
     * @param tasks task list to save.
     * @param ui command-line interface used to report an error.
     * @param storage destination for task data.
     */
    protected void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.saveTasks(tasks);
        } catch (IOException exception) {
            ui.showError("I couldn't save the tasks: " + exception.getMessage());
        }
    }
}
