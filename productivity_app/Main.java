package productivity_app;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
  private static final String[] COLUMNS = {
    KanbanBoard.COLUMN_TODO, KanbanBoard.COLUMN_DOING, KanbanBoard.COLUMN_DONE
  };
  private static final String CLEAR_CONSOLE = "\033[H\033[2J";
  private static final String CLEAR_EOL = "\033[K";

  private static ScheduledExecutorService statusExecutor;
  private static volatile boolean stickyEyeNotice;
  private static volatile String lastRendered = "";

  public static void main(String[] args) {
    ApplicationFacade app = new SimpleApplicationFacade();
    Scanner scanner = new Scanner(System.in);

    System.out.print("Welcome! Please enter your name: ");
    String name = safeReadLine(scanner).trim();
    if (name.isEmpty()) {
      name = "User";
    }
    app.createOrSetCurrentUser(name);
    app.startEyeStrainReminder();

    boolean running = true;
    while (running) {
      stopStatusLine();
      clearConsole();
      renderHeader(app);
      System.out.println("Main Menu:");
      System.out.println("  1) Kanban Board");
      System.out.println("  2) Pomodoro Timer");
      System.out.println("  3) Notes");
      System.out.println("  4) Calendar");
      System.out.println("  5) Data (Import / Export)");
      System.out.println("  6) Exit");
      System.out.println();
      startStatusLine(app);

      String choice = readChoice(scanner);
      stickyEyeNotice = false;

      switch (choice) {
        case "1":
          kanbanMenu(app, scanner);
          break;
        case "2":
          timerMenu(app, scanner);
          break;
        case "3":
          notesMenu(app, scanner);
          break;
        case "4":
          calendarMenu(app, scanner);
          break;
        case "5":
          dataMenu(app, scanner);
          break;
        case "6":
          running = false;
          System.exit(0);
          break;
        default:
          pauseForASecond();
          break;
      }
    }

    stopStatusLine();
    app.stopEyeStrainReminder();
    scanner.close();
  }

  private static void kanbanMenu(ApplicationFacade app, Scanner scanner) {
    boolean back = false;
    while (!back) {
      stopStatusLine();
      clearConsole();
      renderHeader(app);
      System.out.println("Kanban Board:");
      renderKanbanBoard(app.listTasks());
      System.out.println();
      System.out.println("  1) Add Task");
      System.out.println("  2) Update Task");
      System.out.println("  3) Remove Task");
      System.out.println("  4) Back");
      System.out.println();
      startStatusLine(app);

      String choice = readChoice(scanner);
      stickyEyeNotice = false;

      switch (choice) {
        case "1":
          stopStatusLine();
          System.out.print("\nEnter new task label: ");
          String newLabel = safeReadLine(scanner);
          if (!newLabel.isBlank()) {
            Command command = new AddTaskCommand(app, newLabel);
            command.execute();
            System.out.println("Task added.");
          } else {
            System.out.println("Label cannot be blank.");
          }
          pauseForASecond();
          break;
        case "2":
          stopStatusLine();
          System.out.print("\nEnter task label to update: ");
          String labelToUpdate = safeReadLine(scanner);
          String newColumn = promptColumn(scanner);
          Command updateCommand = new UpdateTaskCommand(app, labelToUpdate, newColumn);
          updateCommand.execute();
          System.out.println("Task updated (if it existed).");
          pauseForASecond();
          break;
        case "3":
          stopStatusLine();
          System.out.print("\nEnter task label to remove: ");
          String labelToRemove = safeReadLine(scanner);
          Command removeCommand = new RemoveTaskCommand(app, labelToRemove);
          removeCommand.execute();
          System.out.println("Task removed (if it existed).");
          pauseForASecond();
          break;
        case "4":
          back = true;
          break;
        default:
          pauseForASecond();
          break;
      }
    }
  }

  private static void timerMenu(ApplicationFacade app, Scanner scanner) {
    boolean back = false;
    while (!back) {
      stopStatusLine();
      clearConsole();
      renderHeader(app);
      System.out.println("Pomodoro Timer:");
      System.out.println("  Work duration: "
          + secondsToMin(app.getWorkDurationSeconds()) + " min");
      System.out.println("  Break duration: "
          + secondsToMin(app.getBreakDurationSeconds()) + " min");
      System.out.println("  Current state: " + app.getTimerState());
      System.out.println();
      System.out.println("  1) Set work duration (minutes)");
      System.out.println("  2) Set break duration (minutes)");
      System.out.println("  3) Start");
      System.out.println("  4) Pause");
      System.out.println("  5) Stop");
      System.out.println("  6) Back");
      System.out.println();
      startStatusLine(app);

      String choice = readChoice(scanner);
      stickyEyeNotice = false;

      switch (choice) {
        case "1":
          stopStatusLine();
          int work = readInt(scanner, "\nEnter work duration (minutes): ");
          if (work > 0) {
            app.setWorkDurationSeconds(work * 60);
            System.out.println("Work duration updated.");
          } else {
            System.out.println("Invalid duration.");
          }
          pauseForASecond();
          break;
        case "2":
          stopStatusLine();
          int brk = readInt(scanner, "\nEnter break duration (minutes): ");
          if (brk > 0) {
            app.setBreakDurationSeconds(brk * 60);
            System.out.println("Break duration updated.");
          } else {
            System.out.println("Invalid duration.");
          }
          pauseForASecond();
          break;
        case "3":
          app.startTimer();
          break;
        case "4":
          app.pauseTimer();
          break;
        case "5":
          app.stopTimer();
          break;
        case "6":
          back = true;
          break;
        default:
          pauseForASecond();
          break;
      }
    }
  }

  private static void dataMenu(ApplicationFacade app, Scanner scanner) {
    boolean back = false;
    while (!back) {
      stopStatusLine();
      clearConsole();
      renderHeader(app);
      System.out.println("Data:");
      System.out.println("  1) Export (.properties + .ics)");
      System.out.println("  2) Import (.properties + .ics)");
      System.out.println("  3) Back");
      System.out.println();
      startStatusLine(app);

      String choice = readChoice(scanner);
      stickyEyeNotice = false;

      switch (choice) {
        case "1":
          stopStatusLine();
          System.out.print("\nEnter export file path (e.g., productivity-app.data): ");
          String exportPath = safeReadLine(scanner);
          boolean exported = app.exportData(exportPath);
          System.out.println(exported ? "Export successful." : "Export failed.");
          pauseForASecond();
          break;
        case "2":
          stopStatusLine();
          System.out.print("\nEnter import file path: ");
          String importPath = safeReadLine(scanner);
          boolean imported = app.importData(importPath);
          System.out.println(imported ? "Import successful." : "Import failed.");
          pauseForASecond();
          break;
        case "3":
          back = true;
          break;
        default:
          pauseForASecond();
          break;
      }
    }
  }

  
  private static void notesMenu(ApplicationFacade app, Scanner scanner) {
    boolean back = false;
    while (!back) {
      stopStatusLine();
      clearConsole();
      renderHeader(app);
      System.out.println("Notes:");
      List<Note> notes = app.listNotes();
      if (notes.isEmpty()) {
        System.out.println("  (No notes)");
      } else {
        for (int i = 0; i < notes.size(); i++) {
          Note note = notes.get(i);
          System.out.println("  " + (i + 1) + ") " + note.getTitle());
        }
      }
      System.out.println();
      System.out.println("  1) Add Note");
      System.out.println("  2) Edit Note");
      System.out.println("  3) Remove Note");
      System.out.println("  4) Read Note");
      System.out.println("  5) Back");
      System.out.println();
      startStatusLine(app);

      String choice = readChoice(scanner);
      stickyEyeNotice = false;

      switch (choice) {
        case "1":
          stopStatusLine();
          System.out.print("\nEnter note title: ");
          String title = safeReadLine(scanner);
          System.out.println("Enter note body (single line): ");
          String body = safeReadLine(scanner);
          if (!title.isBlank()) {
            Command addNote = new AddNoteCommand(app, title, body);
            addNote.execute();
            System.out.println("Note added.");
          } else {
            System.out.println("Title cannot be blank.");
          }
          pauseForASecond();
          break;
        case "2":
          stopStatusLine();
          int editIndex =
              readInt(scanner, "\nEnter note number to edit: ") - 1;
          if (editIndex >= 0 && editIndex < notes.size()) {
            System.out.print("Enter new title (leave blank to keep): ");
            String newTitle = safeReadLine(scanner);
            System.out.println("Enter new body (leave blank to keep): ");
            String newBody = safeReadLine(scanner);
            Note existing = notes.get(editIndex);
            if (newTitle.isBlank()) {
              newTitle = existing.getTitle();
            }
            if (newBody.isBlank()) {
              newBody = existing.getBody();
            }
            Command editNote =
                new EditNoteCommand(app, editIndex, newTitle, newBody);
            editNote.execute();
            System.out.println("Note updated.");
          } else {
            System.out.println("Invalid note number.");
          }
          pauseForASecond();
          break;
        case "3":
          stopStatusLine();
          int removeIndex =
              readInt(scanner, "\nEnter note number to remove: ") - 1;
          if (removeIndex >= 0 && removeIndex < notes.size()) {
            Command removeNote = new RemoveNoteCommand(app, removeIndex);
            removeNote.execute();
            System.out.println("Note removed.");
          } else {
            System.out.println("Invalid note number.");
          }
          pauseForASecond();
          break;
        case "4":
          stopStatusLine();
          int readIndex =
              readInt(scanner, "\nEnter note number to read: ") - 1;
          if (readIndex >= 0 && readIndex < notes.size()) {
            noteDetailMenu(app, scanner, readIndex);
          } else {
            System.out.println("Invalid note number.");
            pauseForASecond();
          }
          break;
        case "5":
          back = true;
          break;
        default:
          pauseForASecond();
          break;
      }
    }
  }

  private static void noteDetailMenu(
      ApplicationFacade app, Scanner scanner, int index) {
    boolean back = false;
    while (!back) {
      stopStatusLine();
      clearConsole();
      renderHeader(app);
      List<Note> notes = app.listNotes();
      if (index < 0 || index >= notes.size()) {
        System.out.println("Note no longer exists.");
        pauseForASecond();
        return;
      }
      Note note = notes.get(index);
      System.out.println("Title:");
      System.out.println("  " + note.getTitle());
      System.out.println();
      System.out.println("Body:");
      System.out.println("  " + note.getBody());
      System.out.println();
      System.out.println("  1) Edit Note");
      System.out.println("  2) Remove Note");
      System.out.println("  3) Back");
      System.out.println();
      startStatusLine(app);

      String choice = readChoice(scanner);
      stickyEyeNotice = false;

      switch (choice) {
        case "1":
          stopStatusLine();
          System.out.print("\nEnter new title (leave blank to keep): ");
          String newTitle = safeReadLine(scanner);
          System.out.println("Enter new body (leave blank to keep): ");
          String newBody = safeReadLine(scanner);
          Note existing = notes.get(index);
          if (newTitle.isBlank()) {
            newTitle = existing.getTitle();
          }
          if (newBody.isBlank()) {
            newBody = existing.getBody();
          }
          Command editNote =
              new EditNoteCommand(app, index, newTitle, newBody);
          editNote.execute();
          System.out.println("Note updated.");
          pauseForASecond();
          break;
        case "2":
          stopStatusLine();
          Command removeNote = new RemoveNoteCommand(app, index);
          removeNote.execute();
          System.out.println("Note removed.");
          pauseForASecond();
          back = true;
          break;
        case "3":
          back = true;
          break;
        default:
          pauseForASecond();
          break;
      }
    }
  }

  private static void calendarMenu(ApplicationFacade app, Scanner scanner) {
    boolean back = false;
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    while (!back) {
      stopStatusLine();
      clearConsole();
      renderHeader(app);
      LocalDate today = LocalDate.now();
      DayOfWeek dayOfWeek = today.getDayOfWeek();
      System.out.println(
          "Today: "
              + dayOfWeek
              + ", "
              + today.format(dateFormatter));
      System.out.println();
      System.out.println("Calendar Events:");
      List<CalendarEvent> events = app.listEvents();
      if (events.isEmpty()) {
        System.out.println("  (No events)");
      } else {
        for (int i = 0; i < events.size(); i++) {
          CalendarEvent event = events.get(i);
          System.out.println(
              "  "
                  + (i + 1)
                  + ") "
                  + event.getDate().format(dateFormatter)
                  + " - "
                  + event.getTitle());
        }
      }
      System.out.println();
      System.out.println("  1) Add Event");
      System.out.println("  2) Edit Event");
      System.out.println("  3) Remove Event");
      System.out.println("  4) Back");
      System.out.println();
      startStatusLine(app);

      String choice = readChoice(scanner);
      stickyEyeNotice = false;

      switch (choice) {
        case "1":
          stopStatusLine();
          LocalDate newDate = promptDate(scanner, dateFormatter);
          System.out.print("Enter event title: ");
          String eventTitle = safeReadLine(scanner);
          if (!eventTitle.isBlank()) {
            Command addEvent = new AddEventCommand(app, newDate, eventTitle);
            addEvent.execute();
            System.out.println("Event added.");
          } else {
            System.out.println("Title cannot be blank.");
          }
          pauseForASecond();
          break;
        case "2":
          stopStatusLine();
          int editIndex =
              readInt(scanner, "\nEnter event number to edit: ") - 1;
          if (editIndex >= 0 && editIndex < events.size()) {
            LocalDate date = promptDate(scanner, dateFormatter);
            System.out.print("Enter new title (leave blank to keep): ");
            String newTitle = safeReadLine(scanner);
            CalendarEvent existing = events.get(editIndex);
            if (newTitle.isBlank()) {
              newTitle = existing.getTitle();
            }
            Command editEvent =
                new EditEventCommand(app, editIndex, date, newTitle);
            editEvent.execute();
            System.out.println("Event updated.");
          } else {
            System.out.println("Invalid event number.");
          }
          pauseForASecond();
          break;
        case "3":
          stopStatusLine();
          int removeIndex =
              readInt(scanner, "\nEnter event number to remove: ") - 1;
          if (removeIndex >= 0 && removeIndex < events.size()) {
            Command removeEvent = new RemoveEventCommand(app, removeIndex);
            removeEvent.execute();
            System.out.println("Event removed.");
          } else {
            System.out.println("Invalid event number.");
          }
          pauseForASecond();
          break;
        case "4":
          back = true;
          break;
        default:
          pauseForASecond();
          break;
      }
    }
  }

  private static void renderKanbanBoard(List<Task> tasks) {
    for (String column : COLUMNS) {
      System.out.println(column + ":");
      if (tasks.isEmpty()) {
        System.out.println("  (No tasks)");
      } else {
        for (Task task : tasks) {
          if (column.equals(task.getColumn())) {
            System.out.println("  - " + task.getLabel());
          }
        }
      }
      System.out.println();
    }
  }

  private static void renderHeader(ApplicationFacade app) {
    System.out.print(CLEAR_CONSOLE);
    System.out.flush();
    System.out.println(app.getAppTitle());
    System.out.println("=".repeat(app.getAppTitle().length()));
    System.out.println();
  }

  private static void clearConsole() {
    System.out.print(CLEAR_CONSOLE);
    System.out.flush();
  }

  private static void startStatusLine(ApplicationFacade app) {
    stopStatusLine();
    statusExecutor = Executors.newSingleThreadScheduledExecutor();
    statusExecutor.scheduleAtFixedRate(
        () -> {
          if (!stickyEyeNotice && app.checkAndConsumeEyeNotice()) {
            stickyEyeNotice = true;
          }
          String line = composeLastLine(app);
          synchronized (System.out) {
            System.out.print("\r" + line + CLEAR_EOL);
            System.out.flush();
          }
          lastRendered = line;
        },
        0,
        200,
        TimeUnit.MILLISECONDS);
  }

  private static void stopStatusLine() {
    if (statusExecutor != null) {
      statusExecutor.shutdownNow();
      statusExecutor = null;
      synchronized (System.out) {
        System.out.print("\r" + CLEAR_EOL);
        System.out.flush();
      }
      lastRendered = "";
    }
  }

  private static String composeLastLine(ApplicationFacade app) {
    StringBuilder builder = new StringBuilder();
    PomodoroTimer.State state = app.getTimerState();
    boolean showTimer =
        state == PomodoroTimer.State.RUNNING || state == PomodoroTimer.State.PAUSED;
    if (showTimer) {
      builder
          .append("*Timer* State: ")
          .append(state)
          .append(" | Phase: ")
          .append(app.isWorkPhase() ? "WORK" : "BREAK")
          .append(" | Remaining: ")
          .append(fmt(app.getSecondsRemaining()))
          .append("\t      ");
    }
    builder.append("*Eye Health Service* State: RUNNING");
    if (stickyEyeNotice) {
      builder.append(
          " | Notification: Please look at something 20 feet away for 20 seconds\t");
    }
    builder.append("\tSelect Option: ");
    return builder.toString();
  }

  private static String readChoice(Scanner scanner) {
    String s = safeReadLine(scanner);
    if (s == null) {
      return "";
    }
    return s.trim();
  }

  private static String promptColumn(Scanner scanner) {
    System.out.println("\nSelect column:");
    for (int i = 0; i < COLUMNS.length; i++) {
      System.out.println("  " + (i + 1) + ") " + COLUMNS[i]);
    }
    System.out.print("Choice: ");
    String s = safeReadLine(scanner).trim();
    try {
      int index = Integer.parseInt(s) - 1;
      if (index >= 0 && index < COLUMNS.length) {
        return COLUMNS[index];
      }
    } catch (NumberFormatException ignored) {
    }
    System.out.println("Invalid choice.");
    return COLUMNS[0];
  }

  private static int readInt(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      String s = safeReadLine(scanner).trim();
      if (s.isEmpty()) {
        return 0;
      }
      try {
        return Integer.parseInt(s);
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid integer.");
      }
    }
  }

  private static LocalDate promptDate(Scanner scanner, DateTimeFormatter formatter) {
    while (true) {
      System.out.print("Enter date (yyyy-MM-dd): ");
      String s = safeReadLine(scanner).trim();
      try {
        return LocalDate.parse(s, formatter);
      } catch (Exception e) {
        System.out.println("Invalid date format.");
      }
    }
  }

  private static String safeReadLine(Scanner scanner) {
    try {
      return scanner.nextLine();
    } catch (Exception e) {
      return "";
    }
  }

  private static void pauseForASecond() {
    try {
      Thread.sleep(650);
    } catch (InterruptedException ignored) {
    }
  }

  private static int secondsToMin(int seconds) {
    if (seconds <= 0) {
      return 0;
    }
    return seconds / 60;
  }

  private static String fmt(int sec) {
    int s = Math.max(0, sec);
    int m = s / 60;
    int r = s % 60;
    return String.format("%02d:%02d", m, r);
  }
}
