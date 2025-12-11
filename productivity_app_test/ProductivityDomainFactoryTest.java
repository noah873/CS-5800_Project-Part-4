package productivity_app_test;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

import productivity_app.ProductivityDomainFactory;
import productivity_app.User;
import productivity_app.Task;
import productivity_app.Note;
import productivity_app.CalendarEvent;

public class ProductivityDomainFactoryTest {

  @Test
  public void testCreateUser() {
    ProductivityDomainFactory factory = new ProductivityDomainFactory();
    User user = factory.createUser("Alice");
    assertEquals("Alice", user.getName());
  }

  @Test
  public void testCreateTask() {
    ProductivityDomainFactory factory = new ProductivityDomainFactory();
    Task task = factory.createTask("A", "To-Do");
    assertEquals("A", task.getLabel());
    assertEquals("To-Do", task.getColumn());
  }

  @Test
  public void testCreateNote() {
    ProductivityDomainFactory factory = new ProductivityDomainFactory();
    Note note = factory.createNote("T", "B");
    assertEquals("T", note.getTitle());
    assertEquals("B", note.getBody());
  }

  @Test
  public void testCreateCalendarEvent() {
    ProductivityDomainFactory factory = new ProductivityDomainFactory();
    CalendarEvent event =
        factory.createCalendarEvent(LocalDate.of(2025, 1, 1), "New");
    assertEquals("New", event.getTitle());
  }
}
