package productivity_app;

public class Note {
  private String title;
  private String body;

  public Note(String title, String body) {
    setTitle(title);
    setBody(body);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    if (title == null) {
      throw new NullPointerException("Title cannot be null");
    }
    String trimmed = title.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException("Title cannot be empty");
    }
    this.title = trimmed;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    if (body == null) {
      this.body = "";
    } else {
      this.body = body;
    }
  }
}
