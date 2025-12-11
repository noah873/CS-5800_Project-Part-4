package productivity_app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesService {
  private final List<Note> notes = new ArrayList<>();

  public synchronized List<Note> getNotes() {
    return Collections.unmodifiableList(new ArrayList<>(notes));
  }

  public synchronized void addNote(Note note) {
    if (note == null) {
      throw new NullPointerException("Note cannot be null");
    }
    notes.add(note);
  }

  public synchronized void updateNote(int index, String title, String body) {
    if (index < 0 || index >= notes.size()) {
      return;
    }
    Note existing = notes.get(index);
    existing.setTitle(title);
    existing.setBody(body);
  }

  public synchronized void removeNote(int index) {
    if (index < 0 || index >= notes.size()) {
      return;
    }
    notes.remove(index);
  }

  public synchronized void setNotes(List<Note> newNotes) {
    notes.clear();
    if (newNotes != null) {
      notes.addAll(newNotes);
    }
  }
}
