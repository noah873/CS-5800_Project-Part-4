package productivity_app_test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import productivity_app.KanbanBoard;
import productivity_app.Task;

public class KanbanBoardTest {

  private KanbanBoard board;

  @Before
  public void setUp() {
    board = KanbanBoard.getInstance();
    board.setTasks(new java.util.ArrayList<Task>());
  }

  @Test
  public void testSingletonInstance() {
    assertSame(board, KanbanBoard.getInstance());
  }

  @Test
  public void testAddAndListTasks() {
    board.addTask(new Task("A", KanbanBoard.COLUMN_TODO));
    board.addTask(new Task("B", KanbanBoard.COLUMN_DOING));
    List<Task> tasks = board.getTasks();
    assertEquals(2, tasks.size());
  }

  @Test
  public void testRemoveTaskByLabel() {
    board.addTask(new Task("ToRemove", KanbanBoard.COLUMN_TODO));
    board.removeTaskByLabel("ToRemove");
    boolean exists = board.getTasks().stream()
        .anyMatch(t -> t.getLabel().equals("ToRemove"));
    assertFalse(exists);
  }

  @Test
  public void testUpdateTaskColumn() {
    board.addTask(new Task("X", KanbanBoard.COLUMN_TODO));
    board.updateTask("X", KanbanBoard.COLUMN_DONE);
    Task updated = board.getTasks().stream()
        .filter(t -> t.getLabel().equals("X"))
        .findFirst()
        .orElse(null);
    assertNotNull(updated);
    assertEquals(KanbanBoard.COLUMN_DONE, updated.getColumn());
  }
}
