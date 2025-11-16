package productivity_app;

import java.util.ArrayList;

public final class KanbanBoard {
    private static final KanbanBoard INSTANCE = new KanbanBoard();
    private final ArrayList<Task> tasks = new ArrayList<>();

    private KanbanBoard() {}

    public static KanbanBoard getInstance() {
        return INSTANCE;
    }

    public void setTasks(ArrayList<Task> tasks) {
        if (tasks == null) {
            throw new NullPointerException("Tasks cannot be null");
        }
        this.tasks.clear();
        this.tasks.addAll(tasks);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void addTask(String taskLabel) {
        if (taskLabel == null) {
            throw new NullPointerException("Task Label cannot be null");
        } else if (taskLabel.isEmpty()) {
            throw new IllegalArgumentException("Task Label cannot be empty");
        }
        tasks.add(new Task(taskLabel.trim(), "To-Do"));
    }

    public void removeTask(String taskLabel) {
        if (taskLabel == null) {
            throw new NullPointerException("Task Label cannot be null");
        }  else if (taskLabel.isEmpty()) {
            throw new IllegalArgumentException("Task Label cannot be empty");
        }
        tasks.removeIf(t -> t.getLabel().equals(taskLabel));
    }

    public void updateTask(String taskLabel, String newColumn) {
        if (taskLabel == null) {
            throw new NullPointerException("Task Label cannot be null");
        } else if (taskLabel.isEmpty()) {
            throw new IllegalArgumentException("Task Label cannot be empty");
        } else if (newColumn == null) {
            throw new NullPointerException("New column cannot be null");
        } else if (newColumn.isEmpty()) {
            throw new IllegalArgumentException("New column cannot be empty");
        }
        for (Task t : tasks) {
            if (t.getLabel().equals(taskLabel)) {
                t.setStatus(newColumn);
                break;
            }
        }
    }
}
