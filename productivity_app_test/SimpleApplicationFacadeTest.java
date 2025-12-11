package productivity_app_test;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import productivity_app.ApplicationFacade;
import productivity_app.CalendarEvent;
import productivity_app.Note;
import productivity_app.SimpleApplicationFacade;
import productivity_app.Task;

public class SimpleApplicationFacadeTest {

  private ApplicationFacade app;
  private File tempFile;

  @Before
  public void setUp() throws Exception {
    app = new SimpleApplicationFacade();
    app.createOrSetCurrentUser("Tester");
    tempFile = File.createTempFile("prodapp", ".data");
  }

  @After
  public void tearDown() {
    if (tempFile != null && tempFile.exists()) {
      tempFile.delete();
    }
  }

  @Test
  public void testCreateOrSetCurrentUserThenGetAppTitle() {
    String title = app.getAppTitle();
    assertTrue(title.contains("Tester"));
  }

  @Test
  public void testAddListRemoveTaskThroughFacade() {
    clearTasks(app);
    app.addTask("Task A");
    app.addTask("Task B");
    List<Task> tasks = app.listTasks();
    assertEquals(2, tasks.size());
    app.removeTask("Task A");
    tasks = app.listTasks();
    assertEquals(1, tasks.size());
  }

  @Test
  public void testNotesLifecycleThroughFacade() {
    app.addNote("Title1", "Body1");
    List<Note> notes = app.listNotes();
    assertEquals(1, notes.size());
    app.updateNote(0, "NewTitle", "NewBody");
    Note n = app.listNotes().get(0);
    assertEquals("NewTitle", n.getTitle());
    app.removeNote(0);
    assertTrue(app.listNotes().isEmpty());
  }

  @Test
  public void testCalendarLifecycleThroughFacade() {
    LocalDate date = LocalDate.of(2025, 1, 1);
    app.addEvent(date, "Event1");
    List<CalendarEvent> events = app.listEvents();
    assertEquals(1, events.size());
    app.updateEvent(0, date.plusDays(1), "Updated");
    CalendarEvent evt = app.listEvents().get(0);
    assertEquals("Updated", evt.getTitle());
    app.removeEvent(0);
    assertTrue(app.listEvents().isEmpty());
  }

  @Test
  public void testExportImportRoundTripThroughFacade() {
    clearTasks(app);
    app.addTask("T1");
    app.addNote("N1", "Body");
    app.addEvent(LocalDate.of(2025, 3, 3), "E1");
    boolean exported = app.exportData(tempFile.getAbsolutePath());
    assertTrue(exported);

    ApplicationFacade app2 = new SimpleApplicationFacade();
    boolean imported = app2.importData(tempFile.getAbsolutePath());
    assertTrue(imported);
    assertEquals(1, app2.listTasks().size());
    assertEquals(1, app2.listNotes().size());
    assertEquals(1, app2.listEvents().size());
  }

  private void clearTasks(ApplicationFacade facade) {
    for (Task t : facade.listTasks()) {
      facade.removeTask(t.getLabel());
    }
  }
}
