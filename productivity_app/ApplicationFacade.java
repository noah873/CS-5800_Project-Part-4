package productivity_app;

import java.time.LocalDate;
import java.util.List;

public interface ApplicationFacade {
  void createOrSetCurrentUser(String name);

  User getCurrentUser();

  void addTask(String label);

  void removeTask(String label);

  void updateTask(String label, String newColumn);

  List<Task> listTasks();

  void setWorkDurationSeconds(int seconds);

  void setBreakDurationSeconds(int seconds);

  void startTimer();

  void pauseTimer();

  void stopTimer();

  PomodoroTimer.State getTimerState();

  boolean isWorkPhase();

  int getSecondsRemaining();

  int getWorkDurationSeconds();

  int getBreakDurationSeconds();

  boolean exportData(String path);

  boolean importData(String path);

  void startEyeStrainReminder();

  void stopEyeStrainReminder();

  boolean checkAndConsumeEyeNotice();

  String getAppTitle();

  List<Note> listNotes();

  void addNote(String title, String body);

  void updateNote(int index, String newTitle, String newBody);

  void removeNote(int index);

  List<CalendarEvent> listEvents();

  void addEvent(LocalDate date, String title);

  void updateEvent(int index, LocalDate newDate, String newTitle);

  void removeEvent(int index);
}
