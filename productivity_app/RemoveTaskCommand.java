package productivity_app;

public class RemoveTaskCommand implements Command {
  private final ApplicationFacade facade;
  private final String label;

  public RemoveTaskCommand(ApplicationFacade facade, String label) {
    this.facade = facade;
    this.label = label;
  }

  @Override
  public void execute() {
    facade.removeTask(label);
  }
}
