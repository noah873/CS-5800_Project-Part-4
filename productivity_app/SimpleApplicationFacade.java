package productivity_app;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SimpleApplicationFacade implements ApplicationFacade {
  private final KanbanBoard board = KanbanBoard.getInstance();
  private final PomodoroTimer timer = PomodoroTimer.getInstance();
  private final Data data = new Data();
  private final ProductivityDomainFactory factory = new ProductivityDomainFactory();
  private final NotesService notesService = new NotesService();
  private final CalendarService calendarService = new CalendarService();

  private volatile User currentUser;

  private final ScheduledExecutorService eyeExecutor =
      Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> eyeTask;
  private volatile boolean eyeNoticePending;

  @Override
  public void createOrSetCurrentUser(String name) {
    currentUser = factory.createUser(name);
  }

  @Override
  public User getCurrentUser() {
    return currentUser;
  }

  @Override
  public void addTask(String label) {
    Task task = factory.createTask(label, KanbanBoard.COLUMN_TODO);
    board.addTask(task);
  }

  @Override
  public void removeTask(String label) {
    board.removeTaskByLabel(label);
  }

  @Override
  public void updateTask(String label, String newColumn) {
    board.updateTask(label, newColumn);
  }

  @Override
  public List<Task> listTasks() {
    return board.getTasks();
  }

  @Override
  public void setWorkDurationSeconds(int seconds) {
    timer.setWorkDurationSeconds(seconds);
  }

  @Override
  public void setBreakDurationSeconds(int seconds) {
    timer.setBreakDurationSeconds(seconds);
  }

  @Override
  public void startTimer() {
    timer.start();
  }

  @Override
  public void pauseTimer() {
    timer.pause();
  }

  @Override
  public void stopTimer() {
    timer.stop();
  }

  @Override
  public PomodoroTimer.State getTimerState() {
    return timer.getState();
  }

  @Override
  public boolean isWorkPhase() {
    return timer.isWorkPhase();
  }

  @Override
  public int getSecondsRemaining() {
    return timer.getSecondsRemaining();
  }

  @Override
  public int getWorkDurationSeconds() {
    return timer.getWorkDurationInSeconds();
  }

  @Override
  public int getBreakDurationSeconds() {
    return timer.getBreakDurationInSeconds();
  }

  @Override
  public boolean exportData(String path) {
    if (currentUser == null) {
      return false;
    }
    return data.exportData(
        path,
        currentUser,
        board.getTasks(),
        notesService.getNotes(),
        calendarService.getEvents(),
        timer.getWorkDurationInSeconds(),
        timer.getBreakDurationInSeconds());
  }

  @Override
  public boolean importData(String path) {
    Data.ImportResult result = data.importData(path);
    if (result == null) {
      return false;
    }
    currentUser = result.user;
    board.setTasks(result.tasks);
    notesService.setNotes(result.notes);
    calendarService.setEvents(result.events);
    timer.setWorkDurationSeconds(result.workSeconds);
    timer.setBreakDurationSeconds(result.breakSeconds);
    timer.stop();
    return true;
  }

  @Override
  public void startEyeStrainReminder() {
    if (eyeTask != null && !eyeTask.isDone()) {
      return;
    }
    eyeTask =
        eyeExecutor.scheduleAtFixedRate(
            () -> eyeNoticePending = true, 20, 20, TimeUnit.MINUTES);
  }

  @Override
  public void stopEyeStrainReminder() {
    if (eyeTask != null) {
      eyeTask.cancel(true);
      eyeTask = null;
    }
  }

  @Override
  public boolean checkAndConsumeEyeNotice() {
    boolean pending = eyeNoticePending;
    eyeNoticePending = false;
    return pending;
  }

  @Override
  public String getAppTitle() {
    if (currentUser == null) {
      return "Productivity App";
    }
    return currentUser.getName() + "'s Productivity App";
  }

  @Override
  public List<Note> listNotes() {
    return notesService.getNotes();
  }

  @Override
  public void addNote(String title, String body) {
    Note note = factory.createNote(title, body);
    notesService.addNote(note);
  }

  @Override
  public void updateNote(int index, String newTitle, String newBody) {
    notesService.updateNote(index, newTitle, newBody);
  }

  @Override
  public void removeNote(int index) {
    notesService.removeNote(index);
  }

  @Override
  public List<CalendarEvent> listEvents() {
    return calendarService.getEvents();
  }

  @Override
  public void addEvent(LocalDate date, String title) {
    CalendarEvent event = factory.createCalendarEvent(date, title);
    calendarService.addEvent(event);
  }

  @Override
  public void updateEvent(int index, LocalDate newDate, String newTitle) {
    CalendarEvent updated = factory.createCalendarEvent(newDate, newTitle);
    calendarService.updateEvent(index, updated);
  }

  @Override
  public void removeEvent(int index) {
    calendarService.removeEvent(index);
  }
}
