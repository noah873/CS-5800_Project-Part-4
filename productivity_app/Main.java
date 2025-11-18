package productivity_app;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    private static final String[] COLUMNS = { "To-Do", "Doing", "Done" };
    private static final String CLEAR_CONSOLE = "\033[H\033[2J";
    private static final String CLEAR_EOL = "\033[K";

    private static ScheduledExecutorService statusExec;
    private static volatile boolean stickyEyeNotice = false;
    private static volatile String lastRendered = "";

    private static void clearConsole() {
        System.out.print(CLEAR_CONSOLE);
        System.out.flush();
    }

    public static void main(String[] args) {
        ApplicationFacade app = new SimpleApplicationFacade();
        Scanner sc = new Scanner(System.in);

        clearConsole();
        String name;
        do {
            System.out.print("Welcome! Please enter your name: ");
            name = safeReadLine(sc);
            if (name != null) name = name.trim();
        } while (name == null || name.isBlank());
        app.createOrSetCurrentUser(name);

        app.startEyeStrainReminder();

        boolean running = true;
        while (running) {
            stopStatusLine();
            clearConsole();
            renderHeader(app);
            System.out.println("1) View Kanban Board");
            System.out.println("2) Manage Pomodoro Timer");
            System.out.println("3) Import / Export Data");
            System.out.println("4) Exit");
            System.out.println();
            startStatusLine(app);

            String choice = readChoice(sc);
            stickyEyeNotice = false;

            switch (choice) {
                case "1" -> kanbanMenu(app, sc);
                case "2" -> timerMenu(app, sc);
                case "3" -> dataMenu(app, sc);
                case "4" -> running = false;
                default -> {}
            }
        }

        stopStatusLine();
        app.stopEyeStrainReminder();
        System.exit(0);
    }

    private static void kanbanMenu(ApplicationFacade app, Scanner sc) {
        boolean back = false;
        while (!back) {
            stopStatusLine();
            clearConsole();
            renderHeader(app);
            renderKanbanBoard(app);
            System.out.println();
            System.out.println("Kanban Actions:");
            System.out.println("  1) Add Task");
            System.out.println("  2) Update Task Status");
            System.out.println("  3) Remove Task");
            System.out.println("  4) Back");
            System.out.println();
            startStatusLine(app);

            String c = readChoice(sc);
            stickyEyeNotice = false;

            switch (c) {
                case "1" -> {
                    stopStatusLine();
                    System.out.print("\nEnter new task label: ");
                    String label = safeReadLine(sc);
                    app.addTask(label);
                    System.out.println("Task added.");
                    pauseForASecond();
                }
                case "2" -> {
                    stopStatusLine();
                    System.out.print("\nEnter task label to update: ");
                    String label = safeReadLine(sc);
                    String status = promptColumn(sc);
                    app.updateTask(label, status);
                    System.out.println("Task updated.");
                    pauseForASecond();
                }
                case "3" -> {
                    stopStatusLine();
                    System.out.print("\nEnter task label to remove: ");
                    String label = safeReadLine(sc);
                    app.removeTask(label);
                    System.out.println("Task removed (if it existed).");
                    pauseForASecond();
                }
                case "4" -> back = true;
                default -> {}
            }
        }
    }

    private static void timerMenu(ApplicationFacade app, Scanner sc) {
        boolean back = false;
        while (!back) {
            stopStatusLine();
            clearConsole();
            renderHeader(app);
            renderTimerInfo(app);
            System.out.println();
            System.out.println("Pomodoro Actions:");
            System.out.println("  1) Set Work Minutes");
            System.out.println("  2) Set Break Minutes");
            System.out.println("  3) Start");
            System.out.println("  4) Pause");
            System.out.println("  5) Stop");
            System.out.println("  6) Back");
            System.out.println();
            startStatusLine(app);

            String c = readChoice(sc);
            stickyEyeNotice = false;

            switch (c) {
                case "1" -> {
                    stopStatusLine();
                    int w = readInt(sc, "Work minutes (1-180): ", 1, 180);
                    app.setWorkDurationSeconds(w * 60);
                    System.out.println("Work duration set.");
                    pauseForASecond();
                }
                case "2" -> {
                    stopStatusLine();
                    int b = readInt(sc, "Break minutes (1-60): ", 1, 60);
                    app.setBreakDurationSeconds(b * 60);
                    System.out.println("Break duration set.");
                    pauseForASecond();
                }
                case "3" -> app.startTimer();
                case "4" -> app.pauseTimer();
                case "5" -> app.stopTimer();
                case "6" -> back = true;
                default -> {}
            }
        }
    }

    private static void dataMenu(ApplicationFacade app, Scanner sc) {
        boolean back = false;
        while (!back) {
            stopStatusLine();
            clearConsole();
            renderHeader(app);
            System.out.println("Data:");
            System.out.println("  1) Export (.properties text file)");
            System.out.println("  2) Import (.properties text file)");
            System.out.println("  3) Back");
            System.out.println();
            startStatusLine(app);

            String c = readChoice(sc);
            stickyEyeNotice = false;

            switch (c) {
                case "1" -> {
                    stopStatusLine();
                    System.out.print("\nEnter export file path (e.g., productivity-app.data): ");
                    String path = safeReadLine(sc);
                    boolean ok = app.exportData(path);
                    System.out.println(ok ? "Export successful." : "Export failed.");
                    pauseForASecond();
                }
                case "2" -> {
                    stopStatusLine();
                    System.out.print("\nEnter import file path (e.g., productivity-app.data): ");
                    String path = safeReadLine(sc);
                    boolean ok = app.importData(path);
                    System.out.println(ok ? "Import successful." : "Import failed.");
                    pauseForASecond();
                }
                case "3" -> back = true;
                default -> {}
            }
        }
    }

    private static void renderHeader(ApplicationFacade app) {
        System.out.println(app.getAppTitle());
        System.out.println("------------------------------");
    }

    private static void renderKanbanBoard(ApplicationFacade app) {
        ArrayList<Task> tasks = app.listTasks();
        System.out.println("Kanban Board:");
        for (String col : COLUMNS) {
            System.out.println(" [" + col + "]");
            boolean any = false;
            for (Task t : tasks) {
                if (col.equals(t.getStatus())) {
                    System.out.println("   - " + t.getLabel());
                    any = true;
                }
            }
            if (!any) System.out.println("   (none)");
            System.out.println();
        }
    }

    private static void renderTimerInfo(ApplicationFacade app) {
        System.out.println("Pomodoro Timer:");
        System.out.println("  Work duration (min): " + secondsToMin(app.getWorkDurationSeconds()));
        System.out.println("  Break duration (min): " + secondsToMin(app.getBreakDurationSeconds()));
        System.out.println("  State: " + app.getTimerState() +
                " | Phase: " + (app.isWorkPhase() ? "WORK" : "BREAK"));
    }

    private static void startStatusLine(ApplicationFacade app) {
        stopStatusLine();
        statusExec = Executors.newSingleThreadScheduledExecutor();
        statusExec.scheduleAtFixedRate(() -> {
            if (!stickyEyeNotice && app.checkAndConsumeEyeNotice()) stickyEyeNotice = true;
            String line = composeLastLine(app);
            if (!line.equals(lastRendered)) {
                synchronized (System.out) {
                    System.out.print("\r" + line + CLEAR_EOL);
                    System.out.flush();
                }
                lastRendered = line;
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }

    private static void stopStatusLine() {
        if (statusExec != null) {
            statusExec.shutdownNow();
            statusExec = null;
            synchronized (System.out) {
                System.out.print("\r" + CLEAR_EOL);
                System.out.flush();
            }
            lastRendered = "";
        }
    }

    private static String composeLastLine(ApplicationFacade app) {
        StringBuilder sb = new StringBuilder();
        PomodoroTimer.State s = app.getTimerState();
        boolean showTimer = (s == PomodoroTimer.State.RUNNING || s == PomodoroTimer.State.PAUSED);
        if (showTimer) {
            sb.append("*Timer* State: ").append(s)
                    .append(" | Phase: ").append(app.isWorkPhase() ? "WORK" : "BREAK")
                    .append(" | Remaining: ").append(fmt(app.getSecondsRemaining()))
                    .append("\t      ");
        }

        sb.append("*Eye Health Service* State: RUNNING");
        if (stickyEyeNotice) sb.append(" | Notification: 20-20-20 Reminder");
        sb.append("\tSelect Option: ");
        return sb.toString();
    }

    private static String readChoice(Scanner sc) {
        String s = safeReadLine(sc);
        return s == null ? "" : s.trim();
    }

    private static String promptColumn(Scanner sc) {
        System.out.println("\nSelect column:");
        for (int i = 0; i < COLUMNS.length; i++) {
            System.out.println("  " + (i + 1) + ") " + COLUMNS[i]);
        }
        System.out.print("Choice: ");
        String s = safeReadLine(sc).trim();
        try {
            int idx = Integer.parseInt(s) - 1;
            if (idx >= 0 && idx < COLUMNS.length) return COLUMNS[idx];
        } catch (NumberFormatException ignored) {}
        System.out.println("Invalid choice.");
        return COLUMNS[0];
    }

    private static int readInt(Scanner sc, String prompt, int min, int max) {
        System.out.print(prompt);
        while (true) {
            String s = safeReadLine(sc).trim();
            int v = Integer.parseInt(s);
            if (v >= min && v <= max) return v;
        }
    }

    private static String safeReadLine(Scanner sc) {
        try { return sc.nextLine(); }
        catch (Exception e) { return ""; }
    }

    private static void pauseForASecond() {
        try { Thread.sleep(650); } catch (InterruptedException ignored) {}
    }

    private static int secondsToMin(int s) { return Math.max(0, s / 60); }
    private static String fmt(int sec) {
        int m = Math.max(0, sec) / 60, s = Math.max(0, sec) % 60;
        return String.format("%02d:%02d", m, s);
    }
}
