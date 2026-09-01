/**
 * Adds one parsed task, persists the updated list, and confirms the change.
 */
public abstract class AddCommand extends Command {
    private final String fullCommand;

    /**
     * Creates an add command from the user's complete input.
     *
     * @param fullCommand complete command entered by the user
     */
    protected AddCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Parses, adds, saves, and displays the new task.
     *
     * @param tasks task list to update
     * @param ui command-line interface used for the response
     * @param storage destination for updated task data
     * @throws MintyException if the task details are invalid
     */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException {
        Task task = createTask(fullCommand);
        tasks.add(task);
        saveTasks(tasks, ui, storage);
        ui.showTask("Got it. I've added this task:", task);
        ui.showTaskCount(tasks.size());
    }

    /**
     * Creates the specific task represented by the complete command.
     *
     * @param fullCommand complete command entered by the user
     * @return validated task
     * @throws MintyException if the task details are invalid
     */
    protected abstract Task createTask(String fullCommand) throws MintyException;
}
