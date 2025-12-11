package productivity_app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarService {
  private final List<CalendarEvent> events = new ArrayList<>();

  public synchronized List<CalendarEvent> getEvents() {
    List<CalendarEvent> copy = new ArrayList<>(events);
    Collections.sort(copy);
    return Collections.unmodifiableList(copy);
  }

  public synchronized void addEvent(CalendarEvent event) {
    if (event == null) {
      throw new NullPointerException("Event cannot be null");
    }
    events.add(event);
  }

  public synchronized void updateEvent(int index, CalendarEvent updated) {
    if (index < 0 || index >= events.size()) {
      return;
    }
    events.set(index, updated);
  }

  public synchronized void removeEvent(int index) {
    if (index < 0 || index >= events.size()) {
      return;
    }
    events.remove(index);
  }

  public synchronized void setEvents(List<CalendarEvent> newEvents) {
    events.clear();
    if (newEvents != null) {
      events.addAll(newEvents);
    }
  }
}
