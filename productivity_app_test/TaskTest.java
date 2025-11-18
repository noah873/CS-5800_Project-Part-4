package productivity_app_test;

import org.junit.Test;
import static org.junit.Assert.*;

import productivity_app.Task;

public class TaskTest {
    @Test
    public void testTaskConstructor_WithValidInputs() {
        Task t = new Task("A", "To-Do");
        assertNotNull(t);
    }

    @Test(expected = NullPointerException.class)
    public void testTaskConstructor_WithNullLabel() {
        new Task(null, "To-Do");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTaskConstructor_WithEmptyLabel() {
        new Task("", "To-Do");
    }

    @Test(expected = NullPointerException.class)
    public void testTaskConstructor_WithNullStatus() {
        new Task("A", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTaskConstructor_WithEmptyStatus() {
        new Task("A", "");
    }

    @Test
    public void testGetLabel_ReturnsCorrectLabel() {
        Task t = new Task("A", "To-Do");
        assertEquals("A", t.getLabel());
    }

    @Test
    public void testSetLabel_WithValidLabel() {
        Task t = new Task("A", "To-Do");
        t.setLabel("B");
        assertEquals("B", t.getLabel());
    }

    @Test(expected = NullPointerException.class)
    public void testSetLabel_WithNullLabel() {
        Task t = new Task("A", "To-Do");
        t.setLabel(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLabel_WithEmptyLabel() {
        Task t = new Task("A", "To-Do");
        t.setLabel("");
    }

    @Test
    public void testGetStatus_AfterInitialization() {
        Task t = new Task("A", "To-Do");
        assertEquals("To-Do", t.getStatus());
    }

    @Test
    public void testGetStatus_AfterUpdate() {
        Task t = new Task("A", "To-Do");
        t.setStatus("Doing");
        assertEquals("Doing", t.getStatus());
    }

    @Test
    public void testSetStatus_WithValidStatus() {
        Task t = new Task("A", "To-Do");
        t.setStatus("Done");
        assertEquals("Done", t.getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void testSetStatus_WithNullStatus() {
        Task t = new Task("A", "To-Do");
        t.setStatus(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetStatus_WithEmptyStatus() {
        Task t = new Task("A", "To-Do");
        t.setStatus("");
    }

    @Test
    public void testToString_Format() {
        Task t = new Task("A", "Doing");
        assertEquals("[Doing] A", t.toString());
    }
}
