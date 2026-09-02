package minty.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import minty.exception.MintyException;
import minty.task.Deadline;
import minty.task.Event;
import minty.task.Todo;

/**
 * Interprets and validates commands entered by the user.
 */
public final class Parser {

    /**
     * Prevents creation of a stateless parser instance.
     */
    private Parser() {
    }

    /**
     * Converts complete user input into an executable command object.
     *
     * @param fullCommand complete command entered by the user.
     * @return command ready to execute.
     */
    public static Command parse(String fullCommand) {
        CommandType commandType = parseCommandType(fullCommand);
        switch (commandType) {
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case ON:
                return new OnCommand(fullCommand);
            case MARK:
                return new MarkCommand(fullCommand);
            case UNMARK:
                return new UnmarkCommand(fullCommand);
            case DELETE:
                return new DeleteCommand(fullCommand);
            case TODO:
                return new TodoCommand(fullCommand);
            case DEADLINE:
                return new DeadlineCommand(fullCommand);
            case EVENT:
                return new EventCommand(fullCommand);
            case UNKNOWN:
            default:
                return new UnknownCommand();
        }
    }

    /**
     * Identifies the type of a complete command.
     *
     * @param command complete command entered by the user.
     * @return recognized command type, or {@link CommandType#UNKNOWN}.
     */
    public static CommandType parseCommandType(String command) {
        return CommandType.from(command);
    }

    /**
     * Parses and validates the task number supplied to a task-selection command.
     *
     * @param command complete command entered by the user.
     * @param commandType task-selection command type.
     * @param taskCount current number of stored tasks.
     * @return zero-based index of the selected task.
     * @throws MintyException if the task number is missing, non-numeric, or out of range.
     */
    public static int parseTaskIndex(String command, CommandType commandType, int taskCount)
            throws MintyException {
        String commandWord = commandType.getCommandWord();
        String taskNumber = getCommandArguments(command, commandType);
        if (taskNumber.isEmpty()) {
            throw new MintyException("Please provide a task number to " + commandWord + ".");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new MintyException("The task number must be a whole number.");
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new MintyException("That task number is not in your list.");
        }
        return taskIndex;
    }

    /**
     * Creates a todo after validating its description.
     *
     * @param command complete todo command.
     * @return validated todo.
     * @throws MintyException if the description is missing.
     */
    public static Todo parseTodo(String command) throws MintyException {
        String description = getCommandArguments(command, CommandType.TODO);
        if (description.isEmpty()) {
            throw new MintyException("Hmm, a todo needs a description.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline after validating its description and {@code /by} value.
     *
     * @param command complete deadline command.
     * @return validated deadline.
     * @throws MintyException if any required deadline detail is missing.
     */
    public static Deadline parseDeadline(String command) throws MintyException {
        String details = getCommandArguments(command, CommandType.DEADLINE);
        int bySeparator = details.indexOf("/by");
        if (bySeparator < 0) {
            throw new MintyException("A deadline needs a /by date or time.");
        }

        String description = details.substring(0, bySeparator).trim();
        String byText = details.substring(bySeparator + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new MintyException("Hmm, a deadline needs a description.");
        }
        if (byText.isEmpty()) {
            throw new MintyException("Please say when the deadline is due after /by.");
        }
        LocalDate by = parseDate(byText, "deadline");
        return new Deadline(description, by);
    }

    /**
     * Creates an event after validating its description, start, and end values.
     *
     * @param command complete event command.
     * @return validated event.
     * @throws MintyException if any required event detail is missing or out of order.
     */
    public static Event parseEvent(String command) throws MintyException {
        String details = getCommandArguments(command, CommandType.EVENT);
        int fromSeparator = details.indexOf("/from");
        int toSeparator = details.indexOf("/to");

        if (toSeparator >= 0 && (fromSeparator < 0 || toSeparator < fromSeparator)) {
            throw new MintyException("Put /from before /to when adding an event.");
        }
        if (fromSeparator < 0) {
            throw new MintyException("An event needs a /from date or time.");
        }
        if (toSeparator < 0) {
            throw new MintyException("An event needs a /to date or time.");
        }

        String description = details.substring(0, fromSeparator).trim();
        String fromText = details.substring(fromSeparator + "/from".length(), toSeparator).trim();
        String toText = details.substring(toSeparator + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new MintyException("Hmm, an event needs a description.");
        }
        if (fromText.isEmpty()) {
            throw new MintyException("Please say when the event starts after /from.");
        }
        if (toText.isEmpty()) {
            throw new MintyException("Please say when the event ends after /to.");
        }
        LocalDate from = parseDate(fromText, "event start");
        LocalDate to = parseDate(toText, "event end");
        if (to.isBefore(from)) {
            throw new MintyException("The event end date cannot be before its start date.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses the date supplied to an {@code on} command.
     *
     * @param command complete on command.
     * @return requested date.
     * @throws MintyException if the date is missing or invalid.
     */
    public static LocalDate parseOnDate(String command) throws MintyException {
        String dateText = getCommandArguments(command, CommandType.ON);
        if (dateText.isEmpty()) {
            throw new MintyException("Please provide a date after on.");
        }
        return parseDate(dateText, "requested");
    }

    /**
     * Returns the trimmed text following a command's identifying word.
     *
     * @param command complete command entered by the user.
     * @param commandType recognized type of the command.
     * @return trimmed command arguments, or an empty string if none were supplied.
     */
    private static String getCommandArguments(String command, CommandType commandType) {
        return command.substring(commandType.getCommandWord().length()).trim();
    }

    /**
     * Parses a date in Minty's required ISO format.
     *
     * @param dateText date entered by the user.
     * @param dateName name used to identify the date in an error message.
     * @return parsed date.
     * @throws MintyException if the date is not a valid {@code yyyy-MM-dd} value.
     */
    private static LocalDate parseDate(String dateText, String dateName) throws MintyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new MintyException("Please use yyyy-MM-dd for the " + dateName + " date.");
        }
    }
}
