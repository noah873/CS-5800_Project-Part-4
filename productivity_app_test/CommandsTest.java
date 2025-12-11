package productivity_app_test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

import productivity_app.AddEventCommand;
import productivity_app.AddNoteCommand;
import productivity_app.AddTaskCommand;
import productivity_app.ApplicationFacade;
import productivity_app.CalendarEvent;
import productivity_app.EditEventCommand;
import productivity_app.EditNoteCommand;
import productivity_app.Note;
import productivity_app.RemoveEventCommand;
import productivity_app.RemoveNoteCommand;
import productivity_app.RemoveTaskCommand;
import productivity_app.SimpleApplicationFacade;
import productivity_app.Task;
import productivity_app.UpdateTaskCommand;

public class CommandsTest {

    private ApplicationFacade app;

    @Before
    public void setUp() {
        app = new SimpleApplicationFacade();
        app.createOrSetCurrentUser("CmdUser");
        java.util.List<productivity_app.Task> existingTasks =
                new java.util.ArrayList<productivity_app.Task>(app.listTasks());
        for (productivity_app.Task t : existingTasks) {
            app.removeTask(t.getLabel());
        }
    }

    @Test
    public void testTaskCommands() {
        new AddTaskCommand(app, "Task1").execute();
        assertEquals(1, app.listTasks().size());

        new UpdateTaskCommand(app, "Task1", "Doing").execute();
        Task t = app.listTasks().get(0);
        assertEquals("Doing", t.getColumn());

        new RemoveTaskCommand(app, "Task1").execute();
        assertTrue(app.listTasks().isEmpty());
    }

    @Test
    public void testNoteCommands() {
        new AddNoteCommand(app, "Title", "Body").execute();
        assertEquals(1, app.listNotes().size());

        new EditNoteCommand(app, 0, "NewTitle", "NewBody").execute();
        Note n = app.listNotes().get(0);
        assertEquals("NewTitle", n.getTitle());

        new RemoveNoteCommand(app, 0).execute();
        assertTrue(app.listNotes().isEmpty());
    }

    @Test
    public void testEventCommands() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        new AddEventCommand(app, date, "E1").execute();
        assertEquals(1, app.listEvents().size());

        new EditEventCommand(app, 0, date.plusDays(1), "Updated").execute();
        CalendarEvent e = app.listEvents().get(0);
        assertEquals("Updated", e.getTitle());

        new RemoveEventCommand(app, 0).execute();
        assertTrue(app.listEvents().isEmpty());
    }
}
