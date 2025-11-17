package productivity_app;

import java.util.ArrayList;
import java.util.concurrent.*;

public class SimpleApplicationFacade implements ApplicationFacade {

    private final KanbanBoard board = KanbanBoard.getInstance();
    private final PomodoroTimer timer = PomodoroTimer.getInstance();
    private final Data data = new Data();

    private volatile User currentUser = null;

    private ScheduledExecutorService eyeExec;
    private ScheduledFuture<?> eyeTask;
    private volatile boolean eyeNoticePending = false;

    public SimpleApplicationFacade() {}

    @Override
    public void createOrSetCurrentUser(String name) {
        currentUser = new User(name.trim());
    }

    @Override public User getCurrentUser() {
        return currentUser;
    }

    @Override public void addTask(String label) {
        board.addTask(label);
    }

    @Override public void removeTask(String label) {
        board.removeTask(label);
    }

    @Override public void updateTask(String label, String newColumn) {

        board.updateTask(label, newColumn);
    }

    @Override public ArrayList<Task> listTasks() {
        return board.getTasks();
    }

    @Override public void setWorkDurationSeconds(int seconds) {
        timer.setWorkDurationInSeconds(seconds);
    }

    @Override public void setBreakDurationSeconds(int seconds) {
        timer.setBreakDurationInSeconds(seconds);
    }

    @Override public void startTimer() {
        timer.startTimer();
    }

    @Override public void pauseTimer() {
        timer.pauseTimer();
    }

    @Override public void stopTimer() {
        timer.stopTimer();
    }

    @Override public PomodoroTimer.State getTimerState() {
        return timer.getState();
    }

    @Override public boolean isWorkPhase() {
        return timer.isWorkPhase();
    }

    @Override public int getSecondsRemaining() {
        return timer.getSecondsRemaining();
    }

    @Override public int getWorkDurationSeconds() {
        return timer.getWorkDurationInSeconds();
    }

    @Override public int getBreakDurationSeconds() {
        return timer.getBreakDurationInSeconds();
    }

    @Override
    public boolean exportData(String path) {
        return data.exportData(
                path,
                currentUser,
                board.getTasks(),
                timer.getWorkDurationInSeconds(),
                timer.getBreakDurationInSeconds()
        );
    }

    @Override
    public boolean importData(String path) {
        Data.ImportResult res = data.importData(path);
        if (res == null) return false;
        currentUser = res.user;
        board.setTasks(res.tasks);
        timer.setWorkDurationInSeconds(res.workSeconds);
        timer.setBreakDurationInSeconds(res.breakSeconds);
        timer.stopTimer();
        return true;
    }

    @Override
    public void startEyeStrainReminder() {
        if (eyeExec == null || eyeExec.isShutdown()) {
            eyeExec = Executors.newSingleThreadScheduledExecutor();
        }
        if (eyeTask == null || eyeTask.isCancelled()) {
            eyeTask = eyeExec.scheduleAtFixedRate(() -> eyeNoticePending = true, 20, 20, TimeUnit.MINUTES);
        }
    }

    @Override
    public void stopEyeStrainReminder() {
        if (eyeTask != null) eyeTask.cancel(true);
        if (eyeExec != null) eyeExec.shutdownNow();
        eyeNoticePending = false;
    }

    @Override
    public boolean checkAndConsumeEyeNotice() {
        boolean wasPending = eyeNoticePending;
        eyeNoticePending = false;
        return wasPending;
    }

    @Override
    public String getAppTitle() {
        return currentUser.getName() + "'s Productivity App";
    }
}
