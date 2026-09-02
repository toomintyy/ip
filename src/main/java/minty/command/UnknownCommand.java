package minty.command;

import minty.exception.MintyException;
import minty.storage.Storage;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Represents input that does not match any command Minty understands.
 */
public class UnknownCommand extends Command {

    /**
     * Reports that the input cannot be understood.
     *
     * @param tasks task list, which is not changed.
     * @param ui command-line interface, which is not used directly.
     * @param storage task storage, which is not used.
     * @throws MintyException always, because the command is unknown.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException {
        throw new MintyException("Sorry, I don't understand that command.");
    }
}
