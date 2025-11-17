package productivity_app;

import java.util.concurrent.*;

public final class PomodoroTimer {
    public enum State { IDLE, RUNNING, PAUSED }

    private static final PomodoroTimer INSTANCE = new PomodoroTimer();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> ticker;

    private int workDurationInSeconds = 1500;
    private int breakDurationInSeconds = 300;

    private int secondsRemaining;
    private boolean workPhase = true;
    private volatile State state = State.IDLE;

    private PomodoroTimer() {}

    public static PomodoroTimer getInstance() {
        return INSTANCE;
    }

    public int getWorkDurationInSeconds() {
        return workDurationInSeconds;
    }

    public void setWorkDurationInSeconds(int time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Work duration must be greater than zero");
        }
        workDurationInSeconds = time;
        if (state == State.IDLE && workPhase) {
            secondsRemaining = workDurationInSeconds;
        }
    }

    public int getBreakDurationInSeconds() {
        return breakDurationInSeconds;
    }

    public void setBreakDurationInSeconds(int time) {
        if (time <= 0) {
            throw new IllegalArgumentException("Break duration must be greater than zero");
        }
        breakDurationInSeconds = time;
        if (state == State.IDLE && !workPhase) {
            secondsRemaining = breakDurationInSeconds;
        }
    }

    public void startTimer() {
        if (state == State.RUNNING) {
            return;
        } else if ((workPhase && workDurationInSeconds <= 0) || (!workPhase && breakDurationInSeconds <= 0)) {
            return;
        } else if (secondsRemaining <= 0) {
            secondsRemaining = workPhase ? workDurationInSeconds : breakDurationInSeconds;
        }

        state = State.RUNNING;

        if (ticker == null || ticker.isCancelled()) {
            ticker = scheduler.scheduleAtFixedRate(this::tick, 1, 1, TimeUnit.SECONDS);
        }
    }

    public void pauseTimer() {
        if (state != State.RUNNING) return;
        state = State.PAUSED;
    }

    public void stopTimer() {
        state = State.IDLE;
        workPhase = true;
        secondsRemaining = workDurationInSeconds;
    }

    private void tick() {
        if (state != State.RUNNING) {
            return;
        }

        secondsRemaining = Math.max(0, secondsRemaining - 1);

        if (secondsRemaining == 0) {
            workPhase = !workPhase;
            secondsRemaining = workPhase ? workDurationInSeconds : breakDurationInSeconds;
        }
    }

    public State getState() {
        return state;
    }

    public boolean isWorkPhase() {
        return workPhase;
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }
}
