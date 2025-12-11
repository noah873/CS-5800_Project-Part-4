package productivity_app_test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import productivity_app.BasicIcsAdapter;
import productivity_app.CalendarEvent;
import productivity_app.CalendarStorage;

public class BasicIcsAdapterTest {

  @Test
  public void testFormatAndParseRoundTrip() {
    CalendarStorage storage = new BasicIcsAdapter();
    CalendarEvent event =
        new CalendarEvent(LocalDate.of(2025, 1, 1), "New Year");
    String ics = storage.formatEvents(List.of(event));
    assertTrue(ics.contains("BEGIN:VCALENDAR"));
    assertTrue(ics.contains("DTSTART;VALUE=DATE:20250101"));
    List<CalendarEvent> parsed = storage.parseEvents(ics);
    assertEquals(1, parsed.size());
    assertEquals(LocalDate.of(2025, 1, 1), parsed.get(0).getDate());
    assertEquals("New Year", parsed.get(0).getTitle());
  }

  @Test
  public void testParseEmptyTextReturnsEmptyList() {
    CalendarStorage storage = new BasicIcsAdapter();
    assertTrue(storage.parseEvents("").isEmpty());
  }
}
