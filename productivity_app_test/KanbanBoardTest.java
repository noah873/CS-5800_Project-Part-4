package productivity_app_test;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import productivity_app.KanbanBoard;
import productivity_app.Task;

public class KanbanBoardTest {

    private KanbanBoard board;

    @Before
    public void setUp() {
        board = KanbanBoard.getInstance();
        board.setTasks(new ArrayList<Task>());
    }

    @Test
    public void testGetInstance_BeforeInstanceInitialization() {
        KanbanBoard b = KanbanBoard.getInstance();
        assertNotNull(b);
    }

    @Test
    public void testGetInstance_AfterInstanceInitialization() {
        KanbanBoard a = KanbanBoard.getInstance();
        KanbanBoard b = KanbanBoard.getInstance();
        assertSame(a, b);
    }

    @Test
    public void testSetTasks_WithValidList() {
        ArrayList<Task> list = new ArrayList<>();
        list.add(new Task("A", "To-Do"));
        list.add(new Task("B", "Doing"));
        board.setTasks(list);
        assertEquals(2, board.getTasks().size());
    }

    @Test (expected = NullPointerException.class)
    public void testSetTasks_WithNullList() {
        board.setTasks(null);
    }

    @Test
    public void testSetTasks_WithEmptyList() {
        ArrayList<Task> empty = new ArrayList<>();
        board.setTasks(empty);
        assertTrue(board.getTasks().isEmpty());
    }

    @Test
    public void testGetTasks_ReturnsExistingTasks() {
        ArrayList<Task> list = new ArrayList<>();
        list.add(new Task("A", "To-Do"));
        board.setTasks(list);
        assertEquals(1, board.getTasks().size());
        assertEquals("A", board.getTasks().get(0).getLabel());
    }

    @Test
    public void testGetTasks_ReturnsEmptyTaskList() {
        board.setTasks(new ArrayList<Task>());
        assertTrue(board.getTasks().isEmpty());
    }

    @Test
    public void testAddTask_WithValidString() {
        board.addTask("New Task");
        assertEquals(1, board.getTasks().size());
        assertEquals("New Task", board.getTasks().get(0).getLabel());
        assertEquals("To-Do", board.getTasks().get(0).getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void testAddTask_WithNullString() {
        board.addTask(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddTask_WithEmptyString() {
        board.addTask("");
    }

    @Test
    public void testRemoveTask_WithValidString() {
        board.addTask("X");
        board.removeTask("X");
        assertEquals(0, board.getTasks().size());
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveTask_WithNullString() {
        board.removeTask(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTask_WithEmptyString() {
        board.removeTask("");
    }

    @Test
    public void testUpdateTask_WithValidInputs() {
        board.addTask("T");
        board.updateTask("T", "Doing");
        assertEquals("Doing", board.getTasks().get(0).getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateTask_WithANullInput() {
        board.updateTask(null, "Doing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateTask_WithAnEmptyInput() {
        board.updateTask("", "Doing");
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateTask_WithNullNewColumn() {
        board.addTask("T");
        board.updateTask("T", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateTask_WithEmptyNewColumn() {
        board.addTask("T");
        board.updateTask("T", "");
    }
}
