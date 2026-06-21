package blooddonationsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SplashScreen extends JFrame {

    private AuthManager authManager;
    private int alpha = 0;
    private Timer fadeTimer;

    public SplashScreen(AuthManager authManager) {
        this.authManager = authManager;
        FileStorageManager.initialize();
        buildUI();
        startFade();
    }

    private void buildUI() {
        setTitle("BloodLink - Emergency Blood Donation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setUndecorated(false);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, UITheme.BG_DARK,
                    getWidth(), getHeight(), new Color(0x3D0000)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ── Left panel: branding ─────────────────────────────────────────────
        JPanel left = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, UITheme.RED_DARK,
                    0, getHeight(), new Color(0x8E0000)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(-60, -60, 220, 220);
                g2.fillOval(getWidth() - 100, getHeight() - 100, 200, 200);
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(340, 600));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.CENTER;

        // Blood drop icon (drawn)
        JLabel icon = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shape
                int cx = 50, cy = 30, r = 40;
                g2.setColor(new Color(255, 255, 255, 200));
                int[] xp = {cx, cx - r, cx + r};
                int[] yp = {cy + r + 30, cy, cy};
                g2.fillOval(cx - r, cy, r * 2, r * 2);
                Polygon drop = new Polygon(xp, yp, 3);
                g2.fillPolygon(drop);
                // Cross
                g2.setColor(UITheme.RED_DARK);
                g2.fillRect(cx - 6, cy + 18, 12, 30);
                g2.fillRect(cx - 18, cy + 28, 36, 12);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(100, 110); }
        };

        JLabel appName  = UITheme.makeLabel("BloodLink", new Font("Segoe UI", Font.BOLD, 34), UITheme.WHITE);
        JLabel tagline  = UITheme.makeLabel("Verified. Fast. Life-Saving.", UITheme.FONT_BODY, new Color(255,200,200));
        JLabel sub      = UITheme.makeLabel("University Blood Network", UITheme.FONT_SMALL, new Color(255,200,200));

        // Stats row
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        stats.setOpaque(false);
        stats.add(makeStat("❤", "Donate"));
        stats.add(makeStat("🩸", "Request"));
        stats.add(makeStat("✓", "Verified"));

        gc.gridy = 0; left.add(icon, gc);
        gc.gridy = 1; left.add(appName, gc);
        gc.gridy = 2; left.add(tagline, gc);
        gc.gridy = 3; left.add(sub, gc);
        gc.gridy = 4; gc.insets = new Insets(24, 8, 8, 8); left.add(stats, gc);

        // ── Right panel: auth buttons ────────────────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0; rc.fill = GridBagConstraints.HORIZONTAL;
        rc.insets = new Insets(10, 0, 10, 0);

        JLabel welcome = UITheme.makeLabel("Welcome to BloodLink", UITheme.FONT_HEADING, UITheme.WHITE);
        JLabel desc = UITheme.makeLabel("<html><center>A verified emergency blood donation<br>platform for university communities.</center></html>",
                UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        desc.setHorizontalAlignment(SwingConstants.CENTER);

        JButton loginBtn  = UITheme.makeButton("🔑  Sign In", UITheme.RED_PRIMARY, UITheme.WHITE);
        JButton signupBtn = UITheme.makeButton("📝  Create Account", UITheme.BG_SURFACE, UITheme.WHITE);
        JButton adminBtn  = UITheme.makeButton("🛡  Admin Login", new Color(0x1B1B3A), UITheme.TEXT_MUTED);

        loginBtn.setPreferredSize(new Dimension(280, 48));
        signupBtn.setPreferredSize(new Dimension(280, 48));
        adminBtn.setPreferredSize(new Dimension(280, 40));

        JLabel orLabel = UITheme.makeLabel("─────  or  ─────", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        orLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel footer = UITheme.makeLabel("🔒 All entries are AI-verified for authenticity",
                UITheme.FONT_SMALL, new Color(0x43A047));
        footer.setHorizontalAlignment(SwingConstants.CENTER);

        rc.gridy = 0; right.add(welcome, rc);
        rc.gridy = 1; right.add(desc, rc);
        rc.gridy = 2; rc.insets = new Insets(30, 0, 10, 0); right.add(loginBtn, rc);
        rc.gridy = 3; rc.insets = new Insets(10, 0, 10, 0); right.add(signupBtn, rc);
        rc.gridy = 4; right.add(orLabel, rc);
        rc.gridy = 5; right.add(adminBtn, rc);
        rc.gridy = 6; rc.insets = new Insets(30, 0, 0, 0); right.add(footer, rc);

        // Actions
        loginBtn.addActionListener(e -> openLogin(false));
        signupBtn.addActionListener(e -> openSignup());
        adminBtn.addActionListener(e -> openLogin(true));

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel makeStat(String icon, String label) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel i = UITheme.makeLabel(icon, new Font("Segoe UI", Font.PLAIN, 22), UITheme.WHITE);
        JLabel l = UITheme.makeLabel(label, UITheme.FONT_SMALL, new Color(255,200,200));
        i.setHorizontalAlignment(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(i); p.add(l);
        return p;
    }

    private void startFade() {
        fadeTimer = new Timer(20, e -> {
            alpha = Math.min(alpha + 5, 255);
            if (alpha >= 255) ((Timer)e.getSource()).stop();
            repaint();
        });
        fadeTimer.start();
    }

    private void openLogin(boolean adminMode) {
        new LoginScreen(authManager, adminMode, this).setVisible(true);
        setVisible(false);
    }

    private void openSignup() {
        new SignupScreen(authManager, this).setVisible(true);
        setVisible(false);
    }
}
