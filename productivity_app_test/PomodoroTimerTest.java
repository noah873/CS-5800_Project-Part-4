package productivity_app_test;

import org.junit.*;
import static org.junit.Assert.*;

import productivity_app.PomodoroTimer;

public class PomodoroTimerTest {

    private PomodoroTimer timer;

    @Before
    public void setUp() {
        timer = PomodoroTimer.getInstance();
        timer.stopTimer();
        timer.setWorkDurationInSeconds(1500);
        timer.setBreakDurationInSeconds(300);
        timer.stopTimer();
    }

    @Test
    public void testGetInstance_BeforeInstanceInitialization() {
        assertNotNull(PomodoroTimer.getInstance());
    }

    @Test
    public void testGetInstance_AfterInstanceInitialization() {
        assertSame(timer, PomodoroTimer.getInstance());
    }

    @Test
    public void testGetWorkDurationInSeconds_AfterInitialization() {
        assertEquals(1500, timer.getWorkDurationInSeconds());
    }

    @Test
    public void testGetWorkDurationInSeconds_AfterUpdate() {
        timer.setWorkDurationInSeconds(120);
        assertEquals(120, timer.getWorkDurationInSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWorkDurationInSeconds_WithInvalidTime() {
        timer.setWorkDurationInSeconds(0);
    }

    @Test
    public void testGetBreakDurationInSeconds_AfterInitialization() {
        assertEquals(300, timer.getBreakDurationInSeconds());
    }

    @Test
    public void testGetBreakDurationInSeconds_AfterUpdate() {
        timer.setBreakDurationInSeconds(90);
        assertEquals(90, timer.getBreakDurationInSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBreakDurationInSeconds_WithInvalidTime() {
        timer.setBreakDurationInSeconds(-1);
    }

    @Test
    public void testStartTimer_WhenStopped_CountsDown() throws Exception {
        timer.setWorkDurationInSeconds(2);
        timer.stopTimer();
        timer.startTimer();
        Thread.sleep(1100);
        assertEquals(PomodoroTimer.State.RUNNING, timer.getState());
        assertTrue(timer.getSecondsRemaining() <= 1);
    }

    @Test
    public void testStartTimer_WhenRunning_IsIdempotent() throws Exception {
        timer.setWorkDurationInSeconds(2);
        timer.stopTimer();
        timer.startTimer();
        timer.startTimer();
        assertEquals(PomodoroTimer.State.RUNNING, timer.getState());
    }

    @Test
    public void testPauseTimer_WhenRunning() throws Exception {
        timer.setWorkDurationInSeconds(2);
        timer.stopTimer();
        timer.startTimer();
        Thread.sleep(300);
        timer.pauseTimer();
        assertEquals(PomodoroTimer.State.PAUSED, timer.getState());
        int remaining = timer.getSecondsRemaining();
        Thread.sleep(1200);
        assertEquals(remaining, timer.getSecondsRemaining());
    }

    @Test
    public void testStopTimer_FromRunning_ResetsToWorkDuration() throws Exception {
        timer.setWorkDurationInSeconds(3);
        timer.stopTimer();
        timer.startTimer();
        Thread.sleep(500);
        timer.stopTimer();
        assertEquals(PomodoroTimer.State.IDLE, timer.getState());
        assertEquals(3, timer.getSecondsRemaining());
    }
}
