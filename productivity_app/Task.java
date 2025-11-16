package productivity_app;

public class Task {
    private String label;
    private String status;

    public Task(String label, String status) {
        setLabel(label);
        setStatus(status);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        if (label == null) {
            throw new NullPointerException("Label cannot be null");
        } else if (label.isEmpty()) {
            throw new IllegalArgumentException("Label cannot be empty");
        }
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null) {
            throw new NullPointerException("Status cannot be null");
        } else if (status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return "[" + status + "] " + label;
    }
}
