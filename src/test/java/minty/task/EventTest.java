package minty.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests whether events correctly identify the dates on which they occur.
 */
public class EventTest {

    private static final LocalDate START_DATE = LocalDate.of(2026, 9, 10);
    private static final LocalDate END_DATE = LocalDate.of(2026, 9, 12);

    @Test
    public void occursOn_dateWithinEventPeriod_returnsTrue() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 11)));
    }

    @Test
    public void occursOn_dateBeforeEvent_returnsFalse() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 9)));
    }

    @Test
    public void occursOn_eventStartDate_returnsTrue() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertTrue(event.occursOn(START_DATE));
    }

    @Test
    public void occursOn_eventEndDate_returnsTrue() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertTrue(event.occursOn(END_DATE));
    }

    @Test
    public void occursOn_dateAfterEvent_returnsFalse() {
        Event event = new Event("Conference", START_DATE, END_DATE);

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 13)));
    }
}
