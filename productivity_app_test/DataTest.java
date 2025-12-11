package productivity_app_test;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import productivity_app.CalendarEvent;
import productivity_app.Data;
import productivity_app.Note;
import productivity_app.Task;
import productivity_app.User;

public class DataTest {

  private Data data;
  private File tempFile;

  @Before
  public void setUp() throws Exception {
    data = new Data();
    tempFile = File.createTempFile("prodapp", ".data");
  }

  @After
  public void tearDown() {
    if (tempFile != null && tempFile.exists()) {
      tempFile.delete();
    }
  }

  @Test
  public void testExportDataWithValidData() {
    User user = new User("Alice");
    ArrayList<Task> tasks = new ArrayList<>();
    tasks.add(new Task("T1", "To-Do"));
    ArrayList<Note> notes = new ArrayList<>();
    notes.add(new Note("N1", "Body"));
    ArrayList<CalendarEvent> events = new ArrayList<>();
    events.add(new CalendarEvent(LocalDate.of(2025, 1, 1), "New Year"));

    boolean ok =
        data.exportData(
            tempFile.getAbsolutePath(),
            user,
            tasks,
            notes,
            events,
            1500,
            300);
    assertTrue(ok);
    assertTrue(tempFile.length() > 0);
  }

  @Test(expected = NullPointerException.class)
  public void testExportDataWithNullPath() {
    data.exportData(
        null,
        new User("A"),
        new ArrayList<Task>(),
        new ArrayList<Note>(),
        new ArrayList<CalendarEvent>(),
        1500,
        300);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportDataWithEmptyPath() {
    data.exportData(
        "",
        new User("A"),
        new ArrayList<Task>(),
        new ArrayList<Note>(),
        new ArrayList<CalendarEvent>(),
        1500,
        300);
  }

  @Test(expected = NullPointerException.class)
  public void testExportDataWithNullUser() {
    data.exportData(
        tempFile.getAbsolutePath(),
        null,
        new ArrayList<Task>(),
        new ArrayList<Note>(),
        new ArrayList<CalendarEvent>(),
        1500,
        300);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExportDataWithNonPositiveDurations() {
    data.exportData(
        tempFile.getAbsolutePath(),
        new User("A"),
        new ArrayList<Task>(),
        new ArrayList<Note>(),
        new ArrayList<CalendarEvent>(),
        0,
        300);
  }

  @Test
  public void testImportDataWithValidFilePath() {
    User user = new User("Bob");
    ArrayList<Task> tasks = new ArrayList<>();
    tasks.add(new Task("T1", "To-Do"));
    ArrayList<Note> notes = new ArrayList<>();
    notes.add(new Note("N1", "Body"));
    ArrayList<CalendarEvent> events = new ArrayList<>();
    events.add(new CalendarEvent(LocalDate.of(2025, 2, 2), "X"));

    assertTrue(
        data.exportData(
            tempFile.getAbsolutePath(), user, tasks, notes, events, 1500, 300));

    Data.ImportResult res = data.importData(tempFile.getAbsolutePath());
    assertNotNull(res);
    assertEquals("Bob", res.user.getName());
    assertEquals(1, res.tasks.size());
    assertEquals(1, res.notes.size());
    assertEquals(1, res.events.size());
    assertEquals(1500, res.workSeconds);
    assertEquals(300, res.breakSeconds);
  }

  @Test(expected = NullPointerException.class)
  public void testImportDataWithNullFilePath() {
    data.importData(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testImportDataWithEmptyFilePath() {
    data.importData("");
  }

  @Test
  public void testImportDataWithInvalidFilePath() {
    Data.ImportResult res = data.importData("/path/does/not/exist.properties");
    assertNull(res);
  }
}
