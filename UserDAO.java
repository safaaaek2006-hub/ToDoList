import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * UserDAO.java — Data Access Object for User Authentication
 * 
 * Handles all database operations related to users:
 * 1. Automatically ensures the "users" table exists
 * 2. Register a new user (INSERT INTO users ...)
 * 3. Validate login credentials (SELECT ... FROM users WHERE ...)
 */
public class UserDAO {

    public UserDAO() {
        // Automatically create the users table if it does not exist
        createTableIfNotExists();
    }

    /**
     * Creates the "users" table automatically if it is missing.
     * This avoids manual SQL setup and prevents "Table doesn't exist" errors.
     */
    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                   + "id INT PRIMARY KEY AUTO_INCREMENT, "
                   + "username VARCHAR(50) NOT NULL UNIQUE, "
                   + "password VARCHAR(100) NOT NULL"
                   + ")";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.out.println("Error initializing users table: " + e.getMessage());
        }
    }

    /**
     * Registers a new user.
     * 
     * @param username The desired username
     * @param password The user's password
     * @return true if registration succeeded, false if username already exists or error occurred
     */
    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the username and password match an existing user in the database.
     * 
     * @param username Entered username
     * @param password Entered password
     * @return true if credentials match, false otherwise
     */
    public boolean validateLogin(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                // If rs.next() is true, a matching user record was found!
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Login validation error: " + e.getMessage());
            return false;
        }
    }
}
