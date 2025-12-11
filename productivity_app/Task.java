package productivity_app;

public class Task {
  private String label;
  private String column;

  public Task(String label, String column) {
    setLabel(label);
    setColumn(column);
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    if (label == null) {
      throw new NullPointerException("Label cannot be null");
    }
    String trimmed = label.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Label cannot be empty");
    }
    this.label = trimmed;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    if (column == null) {
      throw new NullPointerException("Column cannot be null");
    }
    this.column = column;
  }
}
