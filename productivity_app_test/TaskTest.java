package productivity_app_test;

import org.junit.Test;
import static org.junit.Assert.*;

import productivity_app.Task;

public class TaskTest {

  @Test
  public void testTaskConstructorWithValidInputs() {
    Task task = new Task("A", "To-Do");
    assertNotNull(task);
    assertEquals("A", task.getLabel());
    assertEquals("To-Do", task.getColumn());
  }

  @Test(expected = NullPointerException.class)
  public void testTaskConstructorWithNullLabel() {
    new Task(null, "To-Do");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTaskConstructorWithEmptyLabel() {
    new Task("   ", "To-Do");
  }

  @Test(expected = NullPointerException.class)
  public void testTaskConstructorWithNullColumn() {
    new Task("A", null);
  }

  @Test
  public void testSettersUpdateValues() {
    Task task = new Task("A", "To-Do");
    task.setLabel("B");
    task.setColumn("Doing");
    assertEquals("B", task.getLabel());
    assertEquals("Doing", task.getColumn());
  }
}
