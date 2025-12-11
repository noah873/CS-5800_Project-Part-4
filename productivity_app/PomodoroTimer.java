package productivity_app;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PomodoroTimer {
  public enum State {
    IDLE,
    RUNNING,
    PAUSED
  }

  private static final PomodoroTimer INSTANCE = new PomodoroTimer();

  private final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor();

  private ScheduledFuture<?> ticker;
  private State state = State.IDLE;
  private boolean workPhase = true;
  private int workDurationSeconds = 25 * 60;
  private int breakDurationSeconds = 5 * 60;
  private int secondsRemaining = 0;

  private PomodoroTimer() {}

  public static PomodoroTimer getInstance() {
    return INSTANCE;
  }

  public synchronized void setWorkDurationSeconds(int seconds) {
    if (seconds <= 0) {
      throw new IllegalArgumentException("Work duration must be positive");
    }
    workDurationSeconds = seconds;
    if (state == State.IDLE && workPhase) {
      secondsRemaining = 0;
    }
  }

  public synchronized void setBreakDurationSeconds(int seconds) {
    if (seconds <= 0) {
      throw new IllegalArgumentException("Break duration must be positive");
    }
    breakDurationSeconds = seconds;
    if (state == State.IDLE && !workPhase) {
      secondsRemaining = 0;
    }
  }

  public synchronized void start() {
    if (state == State.RUNNING) {
      return;
    }
    if (secondsRemaining <= 0) {
      workPhase = true;
      secondsRemaining = workDurationSeconds;
    }
    state = State.RUNNING;
    startTickerIfNeeded();
  }

  public synchronized void pause() {
    if (state != State.RUNNING) {
      return;
    }
    state = State.PAUSED;
    stopTicker();
  }

  public synchronized void stop() {
    state = State.IDLE;
    workPhase = true;
    secondsRemaining = 0;
    stopTicker();
  }

  public synchronized State getState() {
    return state;
  }

  public synchronized boolean isWorkPhase() {
    return workPhase;
  }

  public synchronized int getSecondsRemaining() {
    return secondsRemaining;
  }

  public synchronized int getWorkDurationInSeconds() {
    return workDurationSeconds;
  }

  public synchronized int getBreakDurationInSeconds() {
    return breakDurationSeconds;
  }

  private synchronized void startTickerIfNeeded() {
    if (ticker != null && !ticker.isDone()) {
      return;
    }
    ticker =
        executor.scheduleAtFixedRate(
            this::tickOneSecond, 1, 1, TimeUnit.SECONDS);
  }

  private synchronized void stopTicker() {
    if (ticker != null) {
      ticker.cancel(true);
      ticker = null;
    }
  }

  private synchronized void tickOneSecond() {
    if (state != State.RUNNING) {
      return;
    }
    if (secondsRemaining > 0) {
      secondsRemaining--;
    }
    if (secondsRemaining <= 0) {
      if (workPhase) {
        workPhase = false;
        secondsRemaining = breakDurationSeconds;
      } else {
        workPhase = true;
        secondsRemaining = workDurationSeconds;
      }
    }
  }
}
