package productivity_app;

public class EditNoteCommand implements Command {
  private final ApplicationFacade facade;
  private final int index;
  private final String title;
  private final String body;

  public EditNoteCommand(ApplicationFacade facade, int index, String title, String body) {
    this.facade = facade;
    this.index = index;
    this.title = title;
    this.body = body;
  }

  @Override
  public void execute() {
    facade.updateNote(index, title, body);
  }
}
