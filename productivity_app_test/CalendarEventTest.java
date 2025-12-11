package productivity_app_test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

import productivity_app.CalendarEvent;

public class CalendarEventTest {

  @Test
  public void testConstructorAndGetters() {
    LocalDate date = LocalDate.of(2025, 1, 1);
    CalendarEvent event = new CalendarEvent(date, "New Year");
    assertEquals(date, event.getDate());
    assertEquals("New Year", event.getTitle());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullDate() {
    new CalendarEvent(null, "Title");
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithNullTitle() {
    new CalendarEvent(LocalDate.now(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithEmptyTitle() {
    new CalendarEvent(LocalDate.now(), "   ");
  }

  @Test
  public void testCompareToOrdersByDateThenTitle() {
    CalendarEvent a = new CalendarEvent(LocalDate.of(2025, 1, 1), "A");
    CalendarEvent b = new CalendarEvent(LocalDate.of(2025, 1, 2), "A");
    CalendarEvent c = new CalendarEvent(LocalDate.of(2025, 1, 2), "B");
    assertTrue(a.compareTo(b) < 0);
    assertTrue(b.compareTo(c) < 0);
  }
}
