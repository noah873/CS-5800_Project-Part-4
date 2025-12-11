package productivity_app;

public class UpdateTaskCommand implements Command {
  private final ApplicationFacade facade;
  private final String label;
  private final String newColumn;

  public UpdateTaskCommand(ApplicationFacade facade, String label, String newColumn) {
    this.facade = facade;
    this.label = label;
    this.newColumn = newColumn;
  }

  @Override
  public void execute() {
    facade.updateTask(label, newColumn);
  }
}
