/**
 * Represents an executable command understood by Minty.
 */
public abstract class Command {

    /**
     * Performs this command using Minty's application components.
     *
     * @param tasks task list to query or update
     * @param ui command-line interface used for responses
     * @param storage task storage used for persistence
     * @throws MintyException if the command cannot be executed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException;

    /**
     * Indicates whether Minty should stop after this command.
     *
     * @return {@code true} only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
