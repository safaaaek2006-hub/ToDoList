import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java — Database Connection Helper
 * 
 * This class has one job: connect Java to your MySQL database "todo_db".
 * It uses the JDBC driver (the JAR file in your lib/ folder) to do this.
 * 
 * ⚠️ IMPORTANT: Replace "YOUR_PASSWORD_HERE" below with your actual
 *    MySQL password before running the project.
 */
public class DBConnection {

    // ---------------------------------------------------------------
    // Connection details — change these if your setup is different
    // ---------------------------------------------------------------
    private static final String URL = "jdbc:mysql://localhost:3306/todo_db";
    private static final String USER = "root";
    private static final String PASSWORD = "@todolist@1234";  // ← Put your MySQL password here

    /**
     * Opens and returns a connection to the database.
     * Other classes (like TaskDAO) call this method whenever
     * they need to talk to MySQL.
     *
     * @return a Connection object linked to todo_db
     * @throws SQLException if the connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
