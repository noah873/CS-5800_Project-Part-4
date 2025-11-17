package productivity_app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

public class Data {
    public boolean exportData(String path, User user, ArrayList<Task> tasks, int workSec, int breakSec) {
        if (path == null) {
            throw new NullPointerException("Path cannot be null");
        } else if (path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        } else  if (user == null) {
            throw new NullPointerException("User cannot be null");
        } else if (workSec <= 0 ) {
            throw new IllegalArgumentException("Work sec cannot be less than 0");
        } else if (breakSec <= 0) {
            throw new IllegalArgumentException("Break sec cannot be less than 0");
        }

        Properties p = new Properties();
        p.setProperty("user.name", user.getName());
        p.setProperty("timer.workSeconds", Integer.toString(workSec));
        p.setProperty("timer.breakSeconds", Integer.toString(breakSec));

        p.setProperty("tasks.count", Integer.toString(tasks == null ? 0 : tasks.size()));
        if (tasks != null) {
            for (int i = 0; i < tasks.size(); i++) {
                Task t = tasks.get(i);
                p.setProperty("task." + i + ".label", safe(t.getLabel()));
                p.setProperty("task." + i + ".status", safe(t.getStatus()));
            }
        }

        try (OutputStream out = new FileOutputStream(path)) {
            p.store(new OutputStreamWriter(out, StandardCharsets.UTF_8), "Productivity App Export");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public ImportResult importData(String path) {
        if (path == null) {
            throw new NullPointerException("Path cannot be null");
        }  else if (path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }
        Properties p = new Properties();
        try (InputStream in = new FileInputStream(path)) {
            p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return null;
        }

        String name = p.getProperty("user.name");
        if (name == null || name.isBlank()) return null;
        User user = new User(name);

        Integer work = parsePositive(p.getProperty("timer.workSeconds"));
        Integer brk = parsePositive(p.getProperty("timer.breakSeconds"));
        if (work == null || brk == null) return null;

        int count = parseNonNegative(p.getProperty("tasks.count"), 0);
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String label = p.getProperty("task." + i + ".label", "");
            String status = p.getProperty("task." + i + ".status", "To-Do");
            tasks.add(new Task(label, status));
        }

        return new ImportResult(user, tasks, work, brk);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static Integer parsePositive(String s) {
        try {
            int v = Integer.parseInt(s);
            return v > 0 ? v : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static int parseNonNegative(String s, int def) {
        try {
            int v = Integer.parseInt(s);
            return Math.max(0, v);
        } catch (Exception e) {
            return def;
        }
    }

    public static class ImportResult {
        public final User user;
        public final ArrayList<Task> tasks;
        public final int workSeconds;
        public final int breakSeconds;

        public ImportResult(User user, ArrayList<Task> tasks, int workSeconds, int breakSeconds) {
            this.user = user;
            this.tasks = tasks;
            this.workSeconds = workSeconds;
            this.breakSeconds = breakSeconds;
        }
    }
}
