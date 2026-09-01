package minty;

import java.time.LocalDate;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display the task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Checks whether this task occurs on a given date.
     *
     * <p>Tasks without dates do not occur on any particular date.
     *
     * @param date date to check
     * @return {@code true} if the task occurs on the date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the task in the format used by the save file.
     *
     * @return serialized task
     */
    public String toDataString() {
        return "T | " + (isDone ? "1" : "0") + " | " + escapeDataField(description);
    }

    /**
     * Escapes characters that have special meaning in the save-file format.
     *
     * @param value task field to escape
     * @return escaped field
     */
    protected static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the task in the format used by Minty's responses.
     *
     * @return status icon followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
