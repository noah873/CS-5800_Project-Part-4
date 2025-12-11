package productivity_app;

import java.time.LocalDate;

public class CalendarEvent implements Comparable<CalendarEvent> {
  private LocalDate date;
  private String title;

  public CalendarEvent(LocalDate date, String title) {
    setDate(date);
    setTitle(title);
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    if (date == null) {
      throw new NullPointerException("Date cannot be null");
    }
    this.date = date;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    if (title == null) {
      throw new NullPointerException("Title cannot be null");
    }
    String trimmed = title.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Title cannot be empty");
    }
    this.title = trimmed;
  }

  @Override
  public int compareTo(CalendarEvent other) {
    int cmp = date.compareTo(other.date);
    if (cmp != 0) {
      return cmp;
    }
    return title.compareTo(other.title);
  }
}
