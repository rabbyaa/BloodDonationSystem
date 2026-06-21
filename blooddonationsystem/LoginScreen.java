package blooddonationsystem;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    private AuthManager authManager;
    private boolean adminMode;
    private JFrame parent;

    private JTextField emailField;
    private JPasswordField passField;
    private JLabel statusLabel;

    public LoginScreen(AuthManager authManager, boolean adminMode, JFrame parent) {
        this.authManager = authManager;
        this.adminMode   = adminMode;
        this.parent      = parent;
        buildUI();
    }

    private void buildUI() {
        setTitle(adminMode ? "Admin Login — BloodLink" : "Sign In — BloodLink");
        setSize(480, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,UITheme.BG_DARK,0,getHeight(),new Color(0x1a0000));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        root.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 0, 8, 0);

        String title  = adminMode ? "🛡  Admin Portal" : "🔑  Sign In";
        String subTxt = adminMode ? "Enter admin credentials" : "Welcome back to BloodLink";

        JLabel titleLbl = UITheme.makeLabel(title, UITheme.FONT_TITLE, UITheme.RED_ACCENT);
        JLabel subLbl   = UITheme.makeLabel(subTxt, UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        subLbl.setHorizontalAlignment(SwingConstants.CENTER);

        emailField = UITheme.makeField(20);
        passField  = UITheme.makePassField(20);

        statusLabel = UITheme.makeLabel("", UITheme.FONT_SMALL, UITheme.RED_LIGHT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton loginBtn = UITheme.makeButton("Sign In", UITheme.RED_PRIMARY, UITheme.WHITE);
        JButton backBtn  = UITheme.makeButton("← Back", UITheme.BG_SURFACE, UITheme.WHITE);
        loginBtn.setPreferredSize(new Dimension(340, 44));
        backBtn.setPreferredSize(new Dimension(340, 36));

        // Hint label for admin
        JLabel hintLbl = UITheme.makeLabel(
            adminMode ? "Default: admin@bloodbank.com / admin123" : "",
            UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        hintLbl.setHorizontalAlignment(SwingConstants.CENTER);

        gc.gridy = 0; gc.insets = new Insets(0,0,4,0); root.add(titleLbl, gc);
        gc.gridy = 1; root.add(subLbl, gc);
        gc.gridy = 2; gc.insets = new Insets(20,0,4,0);
            root.add(UITheme.makeLabel("Email Address", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = 3; gc.insets = new Insets(0,0,8,0); root.add(emailField, gc);
        gc.gridy = 4; gc.insets = new Insets(8,0,4,0);
            root.add(UITheme.makeLabel("Password", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = 5; gc.insets = new Insets(0,0,8,0); root.add(passField, gc);
        gc.gridy = 6; root.add(hintLbl, gc);
        gc.gridy = 7; root.add(statusLabel, gc);
        gc.gridy = 8; gc.insets = new Insets(16,0,6,0); root.add(loginBtn, gc);
        gc.gridy = 9; gc.insets = new Insets(4,0,0,0);  root.add(backBtn, gc);

        if (!adminMode) {
            JLabel signupLink = UITheme.makeLabel(
                "<html><center>Don't have an account? <font color='#FF1744'>Sign up</font></center></html>",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
            signupLink.setHorizontalAlignment(SwingConstants.CENTER);
            signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            signupLink.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    dispose();
                    new SignupScreen(authManager, parent).setVisible(true);
                }
            });
            gc.gridy = 10; gc.insets = new Insets(12,0,0,0); root.add(signupLink, gc);
        }

        loginBtn.addActionListener(e -> doLogin());
        backBtn.addActionListener(e -> { dispose(); parent.setVisible(true); });

        passField.addActionListener(e -> doLogin());

        setContentPane(root);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("⚠ Please fill in all fields.");
            return;
        }

        if (authManager.login(email, pass)) {
            User u = authManager.getCurrentUser();
            if (adminMode && !u.getRole().equals("ADMIN")) {
                authManager.logout();
                statusLabel.setText("⚠ This account does not have admin access.");
                return;
            }
            dispose();
            if (u.getRole().equals("ADMIN")) {
                new AdminDashboard(authManager).setVisible(true);
            } else {
                new UserDashboard(authManager).setVisible(true);
            }
        } else {
            statusLabel.setText("✗ Invalid email or password.");
        }
    }
}
