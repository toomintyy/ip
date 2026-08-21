/**
 * Represents a command that Minty can recognize from user input.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    UNKNOWN("", false);

    private final String commandWord;
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its input word and argument policy.
     *
     * @param commandWord word used to invoke the command
     * @param acceptsArguments whether text may follow the command word
     */
    CommandType(String commandWord, boolean acceptsArguments) {
        this.commandWord = commandWord;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Identifies the type of a complete command entered by the user.
     *
     * @param command complete command entered by the user
     * @return matching command type, or {@link #UNKNOWN} if none matches
     */
    public static CommandType from(String command) {
        for (CommandType type : values()) {
            if (command.equals(type.commandWord)
                    || type.acceptsArguments && command.startsWith(type.commandWord + " ")) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the word used to invoke this command.
     *
     * @return command word
     */
    public String getCommandWord() {
        return commandWord;
    }
}
