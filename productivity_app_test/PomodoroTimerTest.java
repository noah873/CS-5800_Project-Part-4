package productivity_app_test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import productivity_app.PomodoroTimer;

public class PomodoroTimerTest {

  private PomodoroTimer timer;

  @Before
  public void setUp() {
    timer = PomodoroTimer.getInstance();
    timer.stop();
    timer.setWorkDurationSeconds(1500);
    timer.setBreakDurationSeconds(300);
  }

  @Test
  public void testSingletonInstance() {
    assertSame(timer, PomodoroTimer.getInstance());
  }

  @Test
  public void testInitialStateAfterStop() {
    assertEquals(PomodoroTimer.State.IDLE, timer.getState());
    assertTrue(timer.isWorkPhase());
    assertEquals(0, timer.getSecondsRemaining());
  }

  @Test
  public void testSetWorkDurationSeconds() {
    timer.setWorkDurationSeconds(120);
    assertEquals(120, timer.getWorkDurationInSeconds());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetWorkDurationSecondsWithNonPositive() {
    timer.setWorkDurationSeconds(0);
  }

  @Test
  public void testStartTransitionsToRunning() throws InterruptedException {
    timer.start();
    assertEquals(PomodoroTimer.State.RUNNING, timer.getState());
  }

  @Test
  public void testPauseFromRunning() {
    timer.start();
    timer.pause();
    assertEquals(PomodoroTimer.State.PAUSED, timer.getState());
  }

  @Test
  public void testStopResetsState() {
    timer.start();
    timer.stop();
    assertEquals(PomodoroTimer.State.IDLE, timer.getState());
    assertTrue(timer.isWorkPhase());
  }
}
