import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TaskDAO.java — Data Access Object
 * 
 * "DAO" means this class handles all database operations.
 * Each method runs one SQL query on your "tasks" table.
 * 
 * Methods:
 *   addTask()      — INSERT a new task
 *   getAllTasks()   — SELECT all tasks
 *   updateTask()   — UPDATE task_name and due_date
 *   deleteTask()   — DELETE a task by id
 *   markComplete() — SET status = 'Completed' for a task
 */
public class TaskDAO {

    // ---------------------------------------------------------------
    // 1. ADD a new task
    // ---------------------------------------------------------------
    public void addTask(Task task) {
        String sql = "INSERT INTO tasks (task_name, due_date) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getTaskName());

            // due_date can be null (optional field)
            if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
                ps.setDate(2, java.sql.Date.valueOf(task.getDueDate()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            ps.executeUpdate();
            System.out.println("Task added successfully.");

        } catch (SQLException e) {
            System.out.println("Error adding task: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 2. GET all tasks (returns a list)
    // ---------------------------------------------------------------
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, task_name, due_date, status FROM tasks ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setTaskName(rs.getString("task_name"));

                // due_date might be NULL in the database
                java.sql.Date date = rs.getDate("due_date");
                task.setDueDate(date != null ? date.toString() : "");

                task.setStatus(rs.getString("status"));
                tasks.add(task);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching tasks: " + e.getMessage());
        }

        return tasks;
    }

    // ---------------------------------------------------------------
    // 3. UPDATE a task's name and due date
    // ---------------------------------------------------------------
    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET task_name = ?, due_date = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getTaskName());

            if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
                ps.setDate(2, java.sql.Date.valueOf(task.getDueDate()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            ps.setInt(3, task.getId());
            ps.executeUpdate();
            System.out.println("Task updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 4. DELETE a task by its ID
    // ---------------------------------------------------------------
    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Task deleted successfully.");

        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // 5. MARK a task as "Completed"
    // ---------------------------------------------------------------
    public void markComplete(int id) {
        String sql = "UPDATE tasks SET status = 'Completed' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Task marked as completed.");

        } catch (SQLException e) {
            System.out.println("Error marking task complete: " + e.getMessage());
        }
    }
}
