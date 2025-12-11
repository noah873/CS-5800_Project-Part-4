package productivity_app;

public class AddNoteCommand implements Command {
  private final ApplicationFacade facade;
  private final String title;
  private final String body;

  public AddNoteCommand(ApplicationFacade facade, String title, String body) {
    this.facade = facade;
    this.title = title;
    this.body = body;
  }

  @Override
  public void execute() {
    facade.addNote(title, body);
  }
}
