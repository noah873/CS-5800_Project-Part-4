package productivity_app_test;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import productivity_app.Data;
import productivity_app.Task;
import productivity_app.User;

public class DataTest {
    private Data data;
    private File tempFile;

    @Before
    public void setUp() throws Exception {
        data = new Data();
        tempFile = File.createTempFile("productivity_test_", ".properties");
    }

    @After
    public void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testExportData_WithValidData() {
        User user = new User("Alice");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("T1", "To-Do"));
        boolean ok = data.exportData(tempFile.getAbsolutePath(), user, tasks, 1500, 300);
        assertTrue(ok);
        assertTrue(tempFile.length() > 0);
    }

    @Test(expected = NullPointerException.class)
    public void testExportData_WithNullPath() {
        data.exportData(null, new User("A"), new ArrayList<Task>(), 1500, 300);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportData_WithEmptyPath() {
        data.exportData("", new User("A"), new ArrayList<Task>(), 1500, 300);
    }

    @Test(expected = NullPointerException.class)
    public void testExportData_WithNullUser() {
        data.exportData(tempFile.getAbsolutePath(), null, new ArrayList<Task>(), 1500, 300);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportData_WithNonPositiveDurations() {
        data.exportData(tempFile.getAbsolutePath(), new User("A"), new ArrayList<Task>(), 0, 300);
    }

    @Test
    public void testImportData_WithValidFilePath() {
        User user = new User("Bob");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("T1", "To-Do"));
        assertTrue(data.exportData(tempFile.getAbsolutePath(), user, tasks, 1500, 300));

        Data.ImportResult res = data.importData(tempFile.getAbsolutePath());
        assertNotNull(res);
        assertEquals("Bob", res.user.getName());
        assertEquals(1, res.tasks.size());
        assertEquals(1500, res.workSeconds);
        assertEquals(300, res.breakSeconds);
    }

    @Test(expected = NullPointerException.class)
    public void testImportData_WithNullFilePath() {
        data.importData(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportData_WithEmptyFilePath() {
        data.importData("");
    }

    @Test
    public void testImportData_WithInvalidFilePath() {
        Data.ImportResult res = data.importData("/path/does/not/exist.properties");
        assertNull(res);
    }
}
