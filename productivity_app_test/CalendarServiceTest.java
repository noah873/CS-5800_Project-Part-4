package productivity_app_test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import productivity_app.CalendarEvent;
import productivity_app.CalendarService;

public class CalendarServiceTest {

  private CalendarService service;

  @Before
  public void setUp() {
    service = new CalendarService();
  }

  @Test
  public void testAddAndListEventsAreSorted() {
    CalendarEvent later =
        new CalendarEvent(LocalDate.of(2025, 2, 1), "Later");
    CalendarEvent earlier =
        new CalendarEvent(LocalDate.of(2025, 1, 1), "Earlier");
    service.addEvent(later);
    service.addEvent(earlier);
    List<CalendarEvent> events = service.getEvents();
    assertEquals("Earlier", events.get(0).getTitle());
  }

  @Test
  public void testUpdateEvent() {
    CalendarEvent original =
        new CalendarEvent(LocalDate.of(2025, 1, 1), "Old");
    service.addEvent(original);
    CalendarEvent updated =
        new CalendarEvent(LocalDate.of(2025, 1, 2), "New");
    service.updateEvent(0, updated);
    List<CalendarEvent> events = service.getEvents();
    assertEquals("New", events.get(0).getTitle());
  }

  @Test
  public void testRemoveEvent() {
    service.addEvent(new CalendarEvent(LocalDate.of(2025, 1, 1), "X"));
    service.removeEvent(0);
    assertTrue(service.getEvents().isEmpty());
  }
}
