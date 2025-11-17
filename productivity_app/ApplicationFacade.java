package productivity_app;

import java.util.ArrayList;

public interface ApplicationFacade {
    void createOrSetCurrentUser(String name);
    User getCurrentUser();

    void addTask(String label);
    void removeTask(String label);
    void updateTask(String label, String newColumn);
    ArrayList<Task> listTasks();

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
}
