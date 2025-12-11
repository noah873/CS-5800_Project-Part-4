package productivity_app_test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import productivity_app.Note;
import productivity_app.NotesService;

public class NotesServiceTest {

  private NotesService service;

  @Before
  public void setUp() {
    service = new NotesService();
  }

  @Test
  public void testAddAndListNotes() {
    service.addNote(new Note("A", "B"));
    service.addNote(new Note("C", "D"));
    List<Note> notes = service.getNotes();
    assertEquals(2, notes.size());
  }

  @Test
  public void testUpdateNote() {
    service.addNote(new Note("A", "B"));
    service.updateNote(0, "New", "Body");
    Note n = service.getNotes().get(0);
    assertEquals("New", n.getTitle());
    assertEquals("Body", n.getBody());
  }

  @Test
  public void testRemoveNoteWithValidIndex() {
    service.addNote(new Note("A", "B"));
    service.removeNote(0);
    assertTrue(service.getNotes().isEmpty());
  }
}
