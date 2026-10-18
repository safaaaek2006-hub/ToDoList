/**
 * Task.java — Model class
 * 
 * This class represents one task (one row in the "tasks" table).
 * It simply stores the data and provides getter/setter methods.
 * No database code goes here.
 */
public class Task {

    // These fields match the columns in your "tasks" table
    private int id;             // id INT PRIMARY KEY AUTO_INCREMENT
    private String taskName;    // task_name VARCHAR(100) NOT NULL
    private String dueDate;     // due_date DATE (stored as String in "YYYY-MM-DD" format)
    private String status;      // status VARCHAR(20) DEFAULT 'Pending'

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------

    /** Empty constructor — useful when creating a Task and setting fields later */
    public Task() {
    }

    /** Full constructor — useful when reading a task from the database */
    public Task(int id, String taskName, String dueDate, String status) {
        this.id = id;
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.status = status;
    }

    // ---------------------------------------------------------------
    // Getters — used to READ the value of each field
    // ---------------------------------------------------------------

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    // ---------------------------------------------------------------
    // Setters — used to WRITE / UPDATE the value of each field
    // ---------------------------------------------------------------

    public void setId(int id) {
        this.id = id;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
