package productivity_app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BasicIcsAdapter implements CalendarStorage {
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd");

  @Override
  public String formatEvents(Iterable<CalendarEvent> events) {
    StringBuilder builder = new StringBuilder();
    builder.append("BEGIN:VCALENDAR\n");
    builder.append("VERSION:2.0\n");
    for (CalendarEvent event : events) {
      builder.append("BEGIN:VEVENT\n");
      String dateText = DATE_FORMATTER.format(event.getDate());
      builder.append("DTSTART;VALUE=DATE:").append(dateText).append("\n");
      builder.append("SUMMARY:").append(event.getTitle()).append("\n");
      builder.append("END:VEVENT\n");
    }
    builder.append("END:VCALENDAR\n");
    return builder.toString();
  }

  @Override
  public List<CalendarEvent> parseEvents(String icsText) {
    List<CalendarEvent> events = new ArrayList<>();
    if (icsText == null || icsText.isBlank()) {
      return events;
    }
    try (BufferedReader reader = new BufferedReader(new StringReader(icsText))) {
      String line;
      boolean inEvent = false;
      LocalDate date = null;
      String title = null;
      while ((line = reader.readLine()) != null) {
        String trimmed = line.trim();
        if (trimmed.equalsIgnoreCase("BEGIN:VEVENT")) {
          inEvent = true;
          date = null;
          title = null;
        } else if (trimmed.equalsIgnoreCase("END:VEVENT")) {
          if (inEvent && date != null && title != null) {
            events.add(new CalendarEvent(date, title));
          }
          inEvent = false;
          date = null;
          title = null;
        } else if (inEvent && trimmed.startsWith("DTSTART")) {
          int idx = Math.max(trimmed.lastIndexOf(':'), trimmed.lastIndexOf('='));
          if (idx >= 0 && idx + 1 < trimmed.length()) {
            String dateText = trimmed.substring(idx + 1).trim();
            try {
              date = LocalDate.parse(dateText, DATE_FORMATTER);
            } catch (Exception ignored) {
              date = null;
            }
          }
        } else if (inEvent && trimmed.startsWith("SUMMARY:")) {
          title = trimmed.substring("SUMMARY:".length()).trim();
        }
      }
    } catch (IOException ignored) {
      return events;
    }
    return events;
  }
}
