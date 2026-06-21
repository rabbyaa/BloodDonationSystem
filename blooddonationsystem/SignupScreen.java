package blooddonationsystem;

import javax.swing.*;
import java.awt.*;

public class SignupScreen extends JFrame {

    private AuthManager authManager;
    private JFrame parent;

    private JTextField nameField, emailField, phoneField, uniField;
    private JPasswordField passField, confirmPassField;
    private JComboBox<String> bloodGroupBox;
    private JLabel statusLabel;

    public SignupScreen(AuthManager authManager, JFrame parent) {
        this.authManager = authManager;
        this.parent      = parent;
        buildUI();
    }

    private void buildUI() {
        setTitle("Create Account — BloodLink");
        setSize(520, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0,UITheme.BG_DARK,0,getHeight(),new Color(0x001a0a));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        root.setLayout(new GridBagLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 0, 5, 0);

        JLabel titleLbl = UITheme.makeLabel("📝  Create Account", UITheme.FONT_TITLE, UITheme.RED_ACCENT);
        JLabel subLbl   = UITheme.makeLabel("Join the BloodLink university network", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        subLbl.setHorizontalAlignment(SwingConstants.CENTER);

        nameField        = UITheme.makeField(20);
        emailField       = UITheme.makeField(20);
        phoneField       = UITheme.makeField(20);
        uniField         = UITheme.makeField(20);
        passField        = UITheme.makePassField(20);
        confirmPassField = UITheme.makePassField(20);

        String[] groups = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};
        bloodGroupBox = UITheme.makeCombo(groups);

        statusLabel = UITheme.makeLabel("", UITheme.FONT_SMALL, UITheme.RED_LIGHT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton signupBtn = UITheme.makeButton("Create Account", UITheme.GREEN_SUCCESS, UITheme.WHITE);
        JButton backBtn   = UITheme.makeButton("← Back to Login", UITheme.BG_SURFACE, UITheme.WHITE);
        signupBtn.setPreferredSize(new Dimension(380, 44));
        backBtn.setPreferredSize(new Dimension(380, 36));

        int row = 0;
        gc.gridy = row++; gc.insets = new Insets(0,0,4,0); root.add(titleLbl, gc);
        gc.gridy = row++; root.add(subLbl, gc);

        gc.gridy = row++; gc.insets = new Insets(12,0,3,0);
            root.add(UITheme.makeLabel("Full Name", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = row++; gc.insets = new Insets(0,0,5,0); root.add(nameField, gc);

        gc.gridy = row++; gc.insets = new Insets(8,0,3,0);
            root.add(UITheme.makeLabel("University Email", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = row++; gc.insets = new Insets(0,0,5,0); root.add(emailField, gc);

        gc.gridy = row++; gc.insets = new Insets(8,0,3,0);
            root.add(UITheme.makeLabel("Phone Number", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = row++; gc.insets = new Insets(0,0,5,0); root.add(phoneField, gc);

        gc.gridy = row++; gc.insets = new Insets(8,0,3,0);
            root.add(UITheme.makeLabel("University Name", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = row++; gc.insets = new Insets(0,0,5,0); root.add(uniField, gc);

        gc.gridy = row++; gc.insets = new Insets(8,0,3,0);
            root.add(UITheme.makeLabel("Blood Group", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = row++; gc.insets = new Insets(0,0,5,0); root.add(bloodGroupBox, gc);

        gc.gridy = row++; gc.insets = new Insets(8,0,3,0);
            root.add(UITheme.makeLabel("Password", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = row++; gc.insets = new Insets(0,0,5,0); root.add(passField, gc);

        gc.gridy = row++; gc.insets = new Insets(8,0,3,0);
            root.add(UITheme.makeLabel("Confirm Password", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.gridy = row++; gc.insets = new Insets(0,0,5,0); root.add(confirmPassField, gc);

        gc.gridy = row++; root.add(statusLabel, gc);
        gc.gridy = row++; gc.insets = new Insets(12,0,6,0); root.add(signupBtn, gc);
        gc.gridy = row++;  gc.insets = new Insets(4,0,0,0);  root.add(backBtn, gc);

        signupBtn.addActionListener(e -> doSignup());
        backBtn.addActionListener(e -> { dispose(); parent.setVisible(true); });

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG_DARK);
        setContentPane(scroll);
    }

    private void doSignup() {
        String name   = nameField.getText().trim();
        String email  = emailField.getText().trim();
        String phone  = phoneField.getText().trim();
        String uni    = uniField.getText().trim();
        String pass   = new String(passField.getPassword());
        String conf   = new String(confirmPassField.getPassword());
        String blood  = (String) bloodGroupBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()
                || uni.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("⚠ Please fill in all fields."); return;
        }
        if (!email.contains("@")) {
            statusLabel.setText("⚠ Enter a valid email address."); return;
        }
        if (pass.length() < 6) {
            statusLabel.setText("⚠ Password must be at least 6 characters."); return;
        }
        if (!pass.equals(conf)) {
            statusLabel.setText("⚠ Passwords do not match."); return;
        }
        if (FileStorageManager.emailExists(email)) {
            statusLabel.setText("⚠ This email is already registered. Please sign in.");
            return;
        }

        boolean ok = authManager.register(name, email, pass, phone, blood, uni);
        if (ok) {
            dispose();
            new UserDashboard(authManager).setVisible(true);
        } else {
            statusLabel.setText("✗ Registration failed. Email may already exist.");
        }
    }
}
