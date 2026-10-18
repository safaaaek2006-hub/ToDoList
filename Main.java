import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Main.java — Styled To-Do Task List Window
 * Opens after successful login. Displays a modern task manager UI.
 */
public class Main extends JFrame {

    // ---------------------------------------------------------------
    // Colour Palette
    // ---------------------------------------------------------------
    private static final Color COLOR_HEADER_BG   = new Color(67, 97, 238);   // Blue header bar
    private static final Color COLOR_HEADER_FG   = Color.WHITE;
    private static final Color COLOR_BG          = new Color(245, 247, 250); // Light grey page bg
    private static final Color COLOR_CARD        = Color.WHITE;
    private static final Color COLOR_BTN_ADD     = new Color(67, 97, 238);   // Blue
    private static final Color COLOR_BTN_UPDATE  = new Color(234, 179, 8);   // Yellow
    private static final Color COLOR_BTN_DELETE  = new Color(239, 68, 68);   // Red
    private static final Color COLOR_BTN_DONE    = new Color(34, 197, 94);   // Green
    private static final Color COLOR_BTN_LOGOUT  = new Color(107, 114, 128); // Grey
    private static final Color COLOR_ROW_PENDING    = new Color(255, 251, 235); // Light yellow
    private static final Color COLOR_ROW_COMPLETED  = new Color(240, 253, 244); // Light green
    private static final Color COLOR_ROW_ALT        = new Color(249, 250, 251); // Alternate row
    private static final Color COLOR_TABLE_HEADER   = new Color(67, 97, 238);
    private static final Font  FONT_HEADER_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font  FONT_HEADER_USER  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_LABEL        = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font  FONT_FIELD        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_BUTTON       = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font  FONT_TABLE        = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);

    // ---------------------------------------------------------------
    // Components
    // ---------------------------------------------------------------
    private JTextField        txtTaskName;
    private JTextField        txtDueDate;
    private JTable            table;
    private DefaultTableModel tableModel;
    private TaskDAO           taskDAO;
    private String            loggedInUser;  // Username passed from LoginFrame

    // ---------------------------------------------------------------
    // Constructor — receives the logged-in username from LoginFrame
    // ---------------------------------------------------------------
    public Main(String username) {
        this.loggedInUser = username;
        this.taskDAO = new TaskDAO();

        // Apply FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            System.out.println("FlatLaf not applied: " + e.getMessage());
        }

        // Window settings
        setTitle("To-Do Task List — " + username);
        setSize(820, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // Build all sections
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);

        loadTasks();
        setVisible(true);
    }

    // ---------------------------------------------------------------
    // SECTION 1: Top Header Bar (Blue bar with title + user info)
    // ---------------------------------------------------------------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER_BG);
        header.setBorder(new EmptyBorder(14, 22, 14, 22));

        // Left: App title
        JLabel lblTitle = new JLabel("📋  To-Do Task List");
        lblTitle.setFont(FONT_HEADER_TITLE);
        lblTitle.setForeground(COLOR_HEADER_FG);

        // Right: Logged-in user + Logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        JLabel lblUser = new JLabel("👤  " + loggedInUser);
        lblUser.setFont(FONT_HEADER_USER);
        lblUser.setForeground(new Color(200, 210, 255));

        JButton btnLogout = makeButton("Logout", COLOR_BTN_LOGOUT, Color.WHITE);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame());
            }
        });

        rightPanel.add(lblUser);
        rightPanel.add(btnLogout);

        header.add(lblTitle,    BorderLayout.WEST);
        header.add(rightPanel,  BorderLayout.EAST);
        return header;
    }

    // ---------------------------------------------------------------
    // SECTION 2: Body (Form card + Task table)
    // ---------------------------------------------------------------
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(COLOR_BG);
        body.setBorder(new EmptyBorder(16, 20, 16, 20));

        body.add(buildFormCard(),  BorderLayout.NORTH);
        body.add(buildTableCard(), BorderLayout.CENTER);

        return body;
    }

    // ---------------------------------------------------------------
    // SECTION 3: Form Card (input fields + action buttons)
    // ---------------------------------------------------------------
    private JPanel buildFormCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(16, 20, 16, 20)
        ));

        // --- Input fields row ---
        JPanel fieldsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        fieldsPanel.setBackground(COLOR_CARD);

        JLabel lblName = new JLabel("Task Name");
        lblName.setFont(FONT_LABEL);

        txtTaskName = new JTextField();
        txtTaskName.setFont(FONT_FIELD);

        JLabel lblDate = new JLabel("Due Date (YYYY-MM-DD)");
        lblDate.setFont(FONT_LABEL);

        txtDueDate = new JTextField();
        txtDueDate.setFont(FONT_FIELD);

        fieldsPanel.add(lblName);
        fieldsPanel.add(txtTaskName);
        fieldsPanel.add(lblDate);
        fieldsPanel.add(txtDueDate);

        // --- Buttons row ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(COLOR_CARD);

        JButton btnAdd      = makeButton("➕  Add Task",      COLOR_BTN_ADD,    Color.WHITE);
        JButton btnUpdate   = makeButton("✏️  Update",        COLOR_BTN_UPDATE, Color.WHITE);
        JButton btnDelete   = makeButton("🗑️  Delete",        COLOR_BTN_DELETE, Color.WHITE);
        JButton btnComplete = makeButton("✔  Mark Complete", COLOR_BTN_DONE,   Color.WHITE);
        JButton btnClear    = makeButton("✖  Clear",         COLOR_BG,         new Color(107,114,128));

        btnClear.setBorder(BorderFactory.createLineBorder(new Color(209,213,219), 1, true));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnComplete);
        btnPanel.add(btnClear);

        card.add(fieldsPanel, BorderLayout.CENTER);
        card.add(btnPanel,    BorderLayout.SOUTH);

        // Button actions
        btnAdd.addActionListener(e      -> addTask());
        btnUpdate.addActionListener(e   -> updateTask());
        btnDelete.addActionListener(e   -> deleteTask());
        btnComplete.addActionListener(e -> markComplete());
        btnClear.addActionListener(e    -> clearFields());

        return card;
    }

    // ---------------------------------------------------------------
    // SECTION 4: Table Card (the task list)
    // ---------------------------------------------------------------
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Table model (non-editable cells)
        String[] columns = {"ID", "Task Name", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(FONT_TABLE);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);

        // Column widths
        table.getColumnModel().getColumn(0).setMaxWidth(50);  // ID column narrow
        table.getColumnModel().getColumn(3).setPreferredWidth(110);

        // Styled table header (blue background)
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(FONT_TABLE_HEADER);
        tableHeader.setBackground(COLOR_TABLE_HEADER);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(0, 40));
        tableHeader.setReorderingAllowed(false);

        // Row colour-coding (Pending = yellow tint, Completed = green tint)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);

                if (!isSelected) {
                    String status = tableModel.getValueAt(row, 3) != null
                            ? tableModel.getValueAt(row, 3).toString() : "";

                    if (status.equalsIgnoreCase("Completed")) {
                        c.setBackground(COLOR_ROW_COMPLETED);
                    } else if (status.equalsIgnoreCase("Pending")) {
                        c.setBackground(COLOR_ROW_PENDING);
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT);
                    }
                    c.setForeground(Color.BLACK);
                }

                // Add left padding to text
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 8));
                return c;
            }
        });

        // Click a row → fill fields
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtTaskName.setText(tableModel.getValueAt(row, 1).toString());
                    Object d = tableModel.getValueAt(row, 2);
                    txtDueDate.setText(d != null ? d.toString() : "");
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        // Legend at the bottom
        card.add(buildLegend(), BorderLayout.SOUTH);

        return card;
    }

    // ---------------------------------------------------------------
    // SECTION 5: Legend bar at the bottom of the table
    // ---------------------------------------------------------------
    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        legend.setBackground(new Color(249, 250, 251));
        legend.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        legend.add(makeLegendDot(COLOR_ROW_PENDING));
        legend.add(smallLabel("Pending"));
        legend.add(makeLegendDot(COLOR_ROW_COMPLETED));
        legend.add(smallLabel("Completed"));

        return legend;
    }

    private JLabel makeLegendDot(Color color) {
        JLabel dot = new JLabel("●");
        dot.setForeground(color.darker());
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return dot;
    }

    private JLabel smallLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(107, 114, 128));
        return lbl;
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
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 10, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ---------------------------------------------------------------
    // Load all tasks from DB into the table
    // ---------------------------------------------------------------
    private void loadTasks() {
        tableModel.setRowCount(0);
        List<Task> tasks = taskDAO.getAllTasks();
        for (Task t : tasks) {
            tableModel.addRow(new Object[]{
                t.getId(), t.getTaskName(), t.getDueDate(), t.getStatus()
            });
        }
    }

    // ---------------------------------------------------------------
    // ADD Task
    // ---------------------------------------------------------------
    private void addTask() {
        String name    = txtTaskName.getText().trim();
        String dueDate = txtDueDate.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a task name.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task task = new Task();
        task.setTaskName(name);
        task.setDueDate(dueDate.isEmpty() ? null : dueDate);
        taskDAO.addTask(task);
        loadTasks();
        clearFields();
        JOptionPane.showMessageDialog(this, "Task added successfully! ✅");
    }

    // ---------------------------------------------------------------
    // UPDATE Task
    // ---------------------------------------------------------------
    private void updateTask() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a task to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = txtTaskName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a task name.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int    id      = (int) tableModel.getValueAt(row, 0);
        String dueDate = txtDueDate.getText().trim();

        Task task = new Task();
        task.setId(id);
        task.setTaskName(name);
        task.setDueDate(dueDate.isEmpty() ? null : dueDate);
        taskDAO.updateTask(task);
        loadTasks();
        clearFields();
        JOptionPane.showMessageDialog(this, "Task updated successfully! ✏️");
    }

    // ---------------------------------------------------------------
    // DELETE Task
    // ---------------------------------------------------------------
    private void deleteTask() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a task to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this task?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            taskDAO.deleteTask(id);
            loadTasks();
            clearFields();
            JOptionPane.showMessageDialog(this, "Task deleted successfully! 🗑️");
        }
    }

    // ---------------------------------------------------------------
    // MARK COMPLETE
    // ---------------------------------------------------------------
    private void markComplete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a task to mark complete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        taskDAO.markComplete(id);
        loadTasks();
        clearFields();
        JOptionPane.showMessageDialog(this, "Task marked as completed! ✔");
    }

    // ---------------------------------------------------------------
    // CLEAR Fields
    // ---------------------------------------------------------------
    private void clearFields() {
        txtTaskName.setText("");
        txtDueDate.setText("");
        table.clearSelection();
    }

    // ---------------------------------------------------------------
    // Entry Point — starts with Login screen
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
