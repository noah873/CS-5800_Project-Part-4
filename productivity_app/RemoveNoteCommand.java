package productivity_app;

public class RemoveNoteCommand implements Command {
  private final ApplicationFacade facade;
  private final int index;

  public RemoveNoteCommand(ApplicationFacade facade, int index) {
    this.facade = facade;
    this.index = index;
  }

  @Override
  public void execute() {
    facade.removeNote(index);
  }
}
