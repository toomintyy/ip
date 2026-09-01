/**
 * Temporarily executes task-changing commands that have not yet been extracted.
 *
 * <p>This transitional command keeps Minty's loop independent of command details
 * while focused command classes are introduced incrementally.
 */
public class LegacyCommand extends Command {
    private final String fullCommand;
    private final CommandType commandType;

    /**
     * Creates a transitional command with its recognized type and complete input.
     *
     * @param fullCommand complete command entered by the user
     * @param commandType recognized command type
     */
    public LegacyCommand(String fullCommand, CommandType commandType) {
        this.fullCommand = fullCommand;
        this.commandType = commandType;
    }

    /**
     * Executes one of the task-changing commands awaiting its own command class.
     *
     * @param tasks task list to update
     * @param ui command-line interface used for responses
     * @param storage destination for updated task data
     * @throws MintyException if the command arguments are invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException {
        switch (commandType) {
        case MARK:
            int taskIndex = Parser.parseTaskIndex(fullCommand, commandType, tasks.size());
            Task markedTask = tasks.mark(taskIndex);
            saveTasks(tasks, ui, storage);
            ui.showTask("Nice! I've marked this task as done:", markedTask);
            break;
        case UNMARK:
            taskIndex = Parser.parseTaskIndex(fullCommand, commandType, tasks.size());
            Task unmarkedTask = tasks.unmark(taskIndex);
            saveTasks(tasks, ui, storage);
            ui.showTask("OK, I've marked this task as not done yet:", unmarkedTask);
            break;
        case DELETE:
            taskIndex = Parser.parseTaskIndex(fullCommand, commandType, tasks.size());
            Task deletedTask = tasks.delete(taskIndex);
            saveTasks(tasks, ui, storage);
            ui.showTask("Noted. I've removed this task:", deletedTask);
            ui.showTaskCount(tasks.size());
            break;
        case UNKNOWN:
        default:
            throw new MintyException("Sorry, I don't understand that command.");
        }
    }
}
