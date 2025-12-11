package productivity_app;

import java.time.LocalDate;

public class ProductivityDomainFactory {
  public User createUser(String name) {
    return new User(name);
  }

  public Task createTask(String label, String column) {
    return new Task(label, column);
  }

  public Note createNote(String title, String body) {
    return new Note(title, body);
  }

  public CalendarEvent createCalendarEvent(LocalDate date, String title) {
    return new CalendarEvent(date, title);
  }
}
