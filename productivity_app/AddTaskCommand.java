package productivity_app;

public class AddTaskCommand implements Command {
  private final ApplicationFacade facade;
  private final String label;

  public AddTaskCommand(ApplicationFacade facade, String label) {
    this.facade = facade;
    this.label = label;
  }

  @Override
  public void execute() {
    facade.addTask(label);
  }
}
