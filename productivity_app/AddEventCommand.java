package productivity_app;

import java.time.LocalDate;

public class AddEventCommand implements Command {
  private final ApplicationFacade facade;
  private final LocalDate date;
  private final String title;

  public AddEventCommand(ApplicationFacade facade, LocalDate date, String title) {
    this.facade = facade;
    this.date = date;
    this.title = title;
  }

  @Override
  public void execute() {
    facade.addEvent(date, title);
  }
}
