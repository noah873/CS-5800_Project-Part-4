package productivity_app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Data {
  private static final String ICS_BEGIN = "BEGIN:VCALENDAR";

  private final CalendarStorage calendarStorage = new BasicIcsAdapter();
  private final ProductivityDomainFactory factory = new ProductivityDomainFactory();

  public boolean exportData(
      String path,
      User user,
      List<Task> tasks,
      List<Note> notes,
      List<CalendarEvent> events,
      int workSeconds,
      int breakSeconds) {
    if (path == null) {
      throw new NullPointerException("Path cannot be null");
    }
    if (path.isEmpty()) {
      throw new IllegalArgumentException("Path cannot be empty");
    }
    if (user == null) {
      throw new NullPointerException("User cannot be null");
    }
    if (workSeconds <= 0) {
      throw new IllegalArgumentException("Work seconds must be positive");
    }
    if (breakSeconds <= 0) {
      throw new IllegalArgumentException("Break seconds must be positive");
    }

    Properties properties = new Properties();
    properties.setProperty("user.name", user.getName());
    properties.setProperty("timer.workSeconds", Integer.toString(workSeconds));
    properties.setProperty("timer.breakSeconds", Integer.toString(breakSeconds));

    int taskCount = tasks == null ? 0 : tasks.size();
    properties.setProperty("tasks.count", Integer.toString(taskCount));
    if (tasks != null) {
      for (int i = 0; i < tasks.size(); i++) {
        Task task = tasks.get(i);
        String prefix = "task." + i + ".";
        properties.setProperty(prefix + "label", task.getLabel());
        properties.setProperty(prefix + "column", task.getColumn());
      }
    }

    int noteCount = notes == null ? 0 : notes.size();
    properties.setProperty("notes.count", Integer.toString(noteCount));
    if (notes != null) {
      for (int i = 0; i < notes.size(); i++) {
        Note note = notes.get(i);
        String prefix = "note." + i + ".";
        properties.setProperty(prefix + "title", note.getTitle());
        properties.setProperty(prefix + "body", note.getBody());
      }
    }

    try (Writer writer =
        new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)) {
      properties.store(writer, "Productivity App Export");
      if (events != null && !events.isEmpty()) {
        String ics = calendarStorage.formatEvents(events);
        writer.write(System.lineSeparator());
        writer.write(ics);
      }
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public ImportResult importData(String path) {
    if (path == null) {
      throw new NullPointerException("Path cannot be null");
    }
    if (path.isEmpty()) {
      throw new IllegalArgumentException("Path cannot be empty");
    }

    StringBuilder contentBuilder = new StringBuilder();
    try (Reader reader =
        new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
        BufferedReader buffered = new BufferedReader(reader)) {
      String line;
      while ((line = buffered.readLine()) != null) {
        contentBuilder.append(line).append(System.lineSeparator());
      }
    } catch (IOException e) {
      return null;
    }

    String content = contentBuilder.toString();
    String propertiesPart;
    String icsPart = "";
    int icsIndex = content.indexOf(ICS_BEGIN);
    if (icsIndex >= 0) {
      propertiesPart = content.substring(0, icsIndex);
      icsPart = content.substring(icsIndex);
    } else {
      propertiesPart = content;
    }

    Properties properties = new Properties();
    try (Reader reader =
        new java.io.StringReader(propertiesPart)) {
      properties.load(reader);
    } catch (IOException e) {
      return null;
    }

    String name = properties.getProperty("user.name");
    if (name == null || name.isBlank()) {
      return null;
    }
    User user = factory.createUser(name);

    Integer work = parsePositive(properties.getProperty("timer.workSeconds"));
    Integer brk = parsePositive(properties.getProperty("timer.breakSeconds"));
    if (work == null || brk == null) {
      return null;
    }

    int taskCount = parseNonNegative(properties.getProperty("tasks.count"), 0);
    List<Task> tasks = new ArrayList<>();
    for (int i = 0; i < taskCount; i++) {
      String prefix = "task." + i + ".";
      String label = properties.getProperty(prefix + "label");
      String column = properties.getProperty(prefix + "column");
      if (label != null && column != null) {
        tasks.add(factory.createTask(label, column));
      }
    }

    int noteCount = parseNonNegative(properties.getProperty("notes.count"), 0);
    List<Note> notes = new ArrayList<>();
    for (int i = 0; i < noteCount; i++) {
      String prefix = "note." + i + ".";
      String title = properties.getProperty(prefix + "title");
      String body = properties.getProperty(prefix + "body");
      if (title != null) {
        if (body == null) {
          body = "";
        }
        notes.add(factory.createNote(title, body));
      }
    }

    List<CalendarEvent> events = calendarStorage.parseEvents(icsPart);

    return new ImportResult(user, tasks, notes, events, work, brk);
  }

  private Integer parsePositive(String value) {
    if (value == null) {
      return null;
    }
    try {
      int parsed = Integer.parseInt(value.trim());
      return parsed > 0 ? parsed : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private int parseNonNegative(String value, int defaultValue) {
    if (value == null) {
      return defaultValue;
    }
    try {
      int parsed = Integer.parseInt(value.trim());
      return Math.max(parsed, 0);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public static class ImportResult {
    public final User user;
    public final List<Task> tasks;
    public final List<Note> notes;
    public final List<CalendarEvent> events;
    public final int workSeconds;
    public final int breakSeconds;

    public ImportResult(
        User user,
        List<Task> tasks,
        List<Note> notes,
        List<CalendarEvent> events,
        int workSeconds,
        int breakSeconds) {
      this.user = user;
      this.tasks = tasks;
      this.notes = notes;
      this.events = events;
      this.workSeconds = workSeconds;
      this.breakSeconds = breakSeconds;
    }
  }
}
