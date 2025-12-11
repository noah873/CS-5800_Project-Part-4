package productivity_app;

import java.util.List;

public interface CalendarStorage {
  String formatEvents(Iterable<CalendarEvent> events);

  List<CalendarEvent> parseEvents(String icsText);
}
