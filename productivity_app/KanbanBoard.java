package productivity_app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KanbanBoard {
  public static final String COLUMN_TODO = "To-Do";
  public static final String COLUMN_DOING = "Doing";
  public static final String COLUMN_DONE = "Done";

  private static final KanbanBoard INSTANCE = new KanbanBoard();

  private final List<Task> tasks = new ArrayList<>();

  private KanbanBoard() {}

  public static KanbanBoard getInstance() {
    return INSTANCE;
  }

  public synchronized void addTask(Task task) {
    if (task == null) {
      throw new NullPointerException("Task cannot be null");
    }
    tasks.add(task);
  }

  public synchronized void removeTaskByLabel(String label) {
    if (label == null) {
      return;
    }
    tasks.removeIf(task -> task.getLabel().equalsIgnoreCase(label.trim()));
  }

  public synchronized void updateTask(String label, String newColumn) {
    if (label == null || newColumn == null) {
      return;
    }
    String trimmed = label.trim();
    for (Task task : tasks) {
      if (task.getLabel().equalsIgnoreCase(trimmed)) {
        task.setColumn(newColumn);
        return;
      }
    }
  }

  public synchronized List<Task> getTasks() {
    return Collections.unmodifiableList(new ArrayList<>(tasks));
  }

  public synchronized void setTasks(List<Task> newTasks) {
    tasks.clear();
    if (newTasks != null) {
      tasks.addAll(newTasks);
    }
  }
}
