import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * LoginFrame.java — Styled Login and Registration Screen
 * Uses FlatLaf for a modern, clean look.
 */
public class LoginFrame extends JFrame {

    // ---------------------------------------------------------------
    // Colour Palette (change these to restyle the whole login page)
    // ---------------------------------------------------------------
    private static final Color COLOR_BG         = new Color(245, 247, 250); // Light grey background
    private static final Color COLOR_CARD        = Color.WHITE;              // White card
    private static final Color COLOR_PRIMARY     = new Color(67, 97, 238);   // Blue - Login button
    private static final Color COLOR_SUCCESS     = new Color(34, 197, 94);   // Green - Register button
    private static final Color COLOR_LABEL       = new Color(55, 65, 81);    // Dark grey - labels
    private static final Color COLOR_HEADER      = new Color(67, 97, 238);   // Blue header text
    private static final Font  FONT_HEADER       = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font  FONT_SUBTITLE     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_LABEL        = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font  FONT_FIELD        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BUTTON       = new Font("Segoe UI", Font.BOLD, 13);

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private UserDAO        userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();

        // Apply FlatLaf modern Look & Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Global UI tweaks
            UIManager.put("Button.arc",              10);
            UIManager.put("TextComponent.arc",       8);
            UIManager.put("Component.focusWidth",    1);
            UIManager.put("Component.innerFocusWidth",1);
        } catch (Exception e) {
            System.out.println("FlatLaf not applied: " + e.getMessage());
        }

        // --- Window Settings ---
        setTitle("Login — To-Do Task List");
        setSize(430, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new GridBagLayout()); // Used to center the card

        // ---------------------------------------------------------------
        // CARD PANEL (white rounded card in the centre)
        // ---------------------------------------------------------------
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 18));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(30, 35, 30, 35)
        ));
        card.setPreferredSize(new Dimension(360, 280));

        // ---------------------------------------------------------------
        // HEADER SECTION: Icon + Title + Subtitle
        // ---------------------------------------------------------------
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(COLOR_CARD);

        JLabel lblTitle = new JLabel("📋  To-Do Task List");
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(COLOR_HEADER);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sign in to manage your tasks");
        lblSubtitle.setFont(FONT_SUBTITLE);
        lblSubtitle.setForeground(new Color(107, 114, 128));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(lblSubtitle);
        card.add(headerPanel, BorderLayout.NORTH);

        // ---------------------------------------------------------------
        // FORM SECTION: Username + Password fields
        // ---------------------------------------------------------------
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 6));
        formPanel.setBackground(COLOR_CARD);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(FONT_LABEL);
        lblUser.setForeground(COLOR_LABEL);

        txtUsername = new JTextField();
        txtUsername.setFont(FONT_FIELD);
        txtUsername.setPreferredSize(new Dimension(0, 36));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(FONT_LABEL);
        lblPass.setForeground(COLOR_LABEL);

        txtPassword = new JPasswordField();
        txtPassword.setFont(FONT_FIELD);
        txtPassword.setPreferredSize(new Dimension(0, 36));

        formPanel.add(lblUser);
        formPanel.add(txtUsername);
        formPanel.add(lblPass);
        formPanel.add(txtPassword);
        card.add(formPanel, BorderLayout.CENTER);

        // ---------------------------------------------------------------
        // BUTTON SECTION: Login (Blue) + Register (Green)
        // ---------------------------------------------------------------
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setBackground(COLOR_CARD);

        JButton btnLogin    = makeButton("Login",    COLOR_PRIMARY, Color.WHITE);
        JButton btnRegister = makeButton("Register", COLOR_SUCCESS,  Color.WHITE);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // ---------------------------------------------------------------
        // Place card in the center of the frame
        // ---------------------------------------------------------------
        add(card);

        // ---------------------------------------------------------------
        // Events
        // ---------------------------------------------------------------
        btnLogin.addActionListener(e -> performLogin());
        btnRegister.addActionListener(e -> performRegister());
        txtPassword.addActionListener(e -> performLogin()); // Enter key = Login

        setVisible(true);
    }

    // ---------------------------------------------------------------
    // Helper: Create a styled button
    // ---------------------------------------------------------------
    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(0, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ---------------------------------------------------------------
    // Login Action
    // ---------------------------------------------------------------
    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.validateLogin(username, password)) {
            JOptionPane.showMessageDialog(this,
                "Welcome back, " + username + "! 👋",
                "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> new Main(username));  // Pass username to Main
        } else {
            JOptionPane.showMessageDialog(this,
                "Incorrect username or password. Please try again.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    // ---------------------------------------------------------------
    // Register Action
    // ---------------------------------------------------------------
    private void performRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a username and password to register.",
                "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this,
                "Account created! You can now log in. ✅",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                "Username already taken. Please choose another.",
                "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
