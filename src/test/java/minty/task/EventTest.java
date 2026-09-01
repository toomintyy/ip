package minty.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void occursOn_dateWithinEventPeriod_returnsTrue() {
        Event event = new Event(
                "Conference",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 12));

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 11)));
    }

    @Test
    public void occursOn_dateBeforeEvent_returnsFalse() {
        Event event = new Event(
                "Conference",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 12));

        assertFalse(event.occursOn(LocalDate.of(2026, 9 , 9)));
    }
}
