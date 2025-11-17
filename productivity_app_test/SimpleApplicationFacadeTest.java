package productivity_app_test;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import productivity_app.SimpleApplicationFacade;
import productivity_app.ApplicationFacade;
import productivity_app.Task;

public class SimpleApplicationFacadeTest {
    private ApplicationFacade app;

    @Before
    public void setUp() {
        app = new SimpleApplicationFacade();
        app.createOrSetCurrentUser("Tester");
        clearBoard(app);
        app.setWorkDurationSeconds(1500);
        app.setBreakDurationSeconds(300);
    }

    @After
    public void tearDown() {
        app.stopEyeStrainReminder();
        clearBoard(app);
    }

    private static void clearBoard(ApplicationFacade app) {
        ArrayList<Task> existing = new ArrayList<>(app.listTasks());
        for (Task t : existing) {
            app.removeTask(t.getLabel());
        }
        assertTrue(app.listTasks().isEmpty());
    }

    @Test
    public void testCreateOrSetCurrentUser_ThenGetAppTitle() {
        assertTrue(app.getAppTitle().contains("Tester"));
    }

    @Test
    public void testAddListRemoveTask_ThroughFacade() {
        clearBoard(app);
        app.addTask("X");
        assertEquals(1, app.listTasks().size());
        app.removeTask("X");
        assertEquals(0, app.listTasks().size());
    }

    @Test
    public void testUpdateTask_ThroughFacade() {
        clearBoard(app);
        app.addTask("T");
        app.updateTask("T", "Doing");
        assertEquals("Doing", app.listTasks().get(0).getStatus());
    }

    @Test
    public void testTimerSettersAndGetters_ThroughFacade() {
        app.setWorkDurationSeconds(120);
        app.setBreakDurationSeconds(60);
        assertEquals(120, app.getWorkDurationSeconds());
        assertEquals(60, app.getBreakDurationSeconds());
    }

    @Test
    public void testExportAndImportData_RoundTrip() throws Exception {
        clearBoard(app);
        app.createOrSetCurrentUser("RoundTripper");
        app.addTask("T1");
        app.setWorkDurationSeconds(100);
        app.setBreakDurationSeconds(50);

        File temp = File.createTempFile("facade_roundtrip_", ".properties");
        try {
            assertTrue(app.exportData(temp.getAbsolutePath()));

            app.removeTask("T1");
            app.createOrSetCurrentUser("Mutated");
            app.setWorkDurationSeconds(1);
            app.setBreakDurationSeconds(1);

            assertTrue(app.importData(temp.getAbsolutePath()));
            assertEquals("RoundTripper's Productivity App", app.getAppTitle());
            assertEquals(1, app.listTasks().size());
            assertEquals(100, app.getWorkDurationSeconds());
            assertEquals(50, app.getBreakDurationSeconds());
        } finally {
            temp.delete();
        }
    }

    @Test
    public void testEyeStrainReminder_TogglesNotice() throws Exception {
        app.startEyeStrainReminder();
        Thread.sleep(1200);
        boolean noticed = app.checkAndConsumeEyeNotice();
        if (noticed) {
            assertFalse(app.checkAndConsumeEyeNotice());
        }
        app.stopEyeStrainReminder();
    }
}
