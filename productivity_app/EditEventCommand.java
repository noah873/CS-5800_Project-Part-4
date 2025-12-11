package productivity_app;

import java.time.LocalDate;

public class EditEventCommand implements Command {
  private final ApplicationFacade facade;
  private final int index;
  private final LocalDate date;
  private final String title;

  public EditEventCommand(ApplicationFacade facade, int index, LocalDate date, String title) {
    this.facade = facade;
    this.index = index;
    this.date = date;
    this.title = title;
  }

  @Override
  public void execute() {
    facade.updateEvent(index, date, title);
  }
}
