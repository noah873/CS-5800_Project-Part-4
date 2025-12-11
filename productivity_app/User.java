package productivity_app;

public class User {
  private String name;

  public User(String name) {
    setName(name);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name == null) {
      throw new NullPointerException("Name cannot be null");
    }
    String trimmed = name.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
    this.name = trimmed;
  }
}
