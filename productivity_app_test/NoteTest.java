package productivity_app_test;

import org.junit.Test;
import static org.junit.Assert.*;

import productivity_app.Note;

public class NoteTest {

  @Test
  public void testNoteConstructorWithValidInputs() {
    Note note = new Note("Title", "Body");
    assertEquals("Title", note.getTitle());
    assertEquals("Body", note.getBody());
  }

  @Test(expected = NullPointerException.class)
  public void testNoteConstructorWithNullTitle() {
    new Note(null, "Body");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoteConstructorWithEmptyTitle() {
    new Note("   ", "Body");
  }

  @Test
  public void testBodyCanBeNullAndBecomesEmpty() {
    Note note = new Note("Title", null);
    assertEquals("", note.getBody());
  }

  @Test
  public void testSettersUpdateValues() {
    Note note = new Note("Title", "Body");
    note.setTitle("New");
    note.setBody("NewBody");
    assertEquals("New", note.getTitle());
    assertEquals("NewBody", note.getBody());
  }
}
