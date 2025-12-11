package productivity_app;

public class RemoveEventCommand implements Command {
  private final ApplicationFacade facade;
  private final int index;

  public RemoveEventCommand(ApplicationFacade facade, int index) {
    this.facade = facade;
    this.index = index;
  }

  @Override
  public void execute() {
    facade.removeEvent(index);
  }
}
