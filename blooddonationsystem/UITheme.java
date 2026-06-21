package blooddonationsystem;

import java.awt.*;

public class UITheme {

    // ── Palette ──────────────────────────────────────────────────────────────
    public static final Color RED_PRIMARY    = new Color(0xC62828);
    public static final Color RED_LIGHT      = new Color(0xEF5350);
    public static final Color RED_DARK       = new Color(0x8E0000);
    public static final Color RED_ACCENT     = new Color(0xFF1744);

    public static final Color BG_DARK        = new Color(0x1A1A2E);
    public static final Color BG_CARD        = new Color(0x16213E);
    public static final Color BG_SURFACE     = new Color(0x0F3460);

    public static final Color WHITE          = new Color(0xFFFFFF);
    public static final Color OFF_WHITE      = new Color(0xF5F5F5);
    public static final Color TEXT_MUTED     = new Color(0xB0BEC5);
    public static final Color TEXT_LIGHT     = new Color(0xECEFF1);

    public static final Color GREEN_SUCCESS  = new Color(0x43A047);
    public static final Color ORANGE_WARN    = new Color(0xFB8C00);
    public static final Color RED_DANGER     = new Color(0xE53935);
    public static final Color BLUE_INFO      = new Color(0x1E88E5);

    public static final Color FIELD_BG       = new Color(0x0A1628);
    public static final Color FIELD_BORDER   = new Color(0x1565C0);
    public static final Color FIELD_FOCUS    = new Color(0xFF1744);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUB     = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 13);

    // ── Helper: styled button ─────────────────────────────────────────────────
    public static javax.swing.JButton makeButton(String text, Color bg, Color fg) {
        javax.swing.JButton btn = new javax.swing.JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }

    // ── Helper: styled text field ─────────────────────────────────────────────
    public static javax.swing.JTextField makeField(int cols) {
        javax.swing.JTextField f = new javax.swing.JTextField(cols);
        f.setBackground(FIELD_BG);
        f.setForeground(WHITE);
        f.setCaretColor(WHITE);
        f.setFont(FONT_BODY);
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(FIELD_BORDER, 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    // ── Helper: styled password field ─────────────────────────────────────────
    public static javax.swing.JPasswordField makePassField(int cols) {
        javax.swing.JPasswordField f = new javax.swing.JPasswordField(cols);
        f.setBackground(FIELD_BG);
        f.setForeground(WHITE);
        f.setCaretColor(WHITE);
        f.setFont(FONT_BODY);
        f.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(FIELD_BORDER, 1),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    // ── Helper: styled combo box ──────────────────────────────────────────────
    public static javax.swing.JComboBox<String> makeCombo(String[] items) {
        javax.swing.JComboBox<String> cb = new javax.swing.JComboBox<>(items);
        cb.setBackground(FIELD_BG);
        cb.setForeground(WHITE);
        cb.setFont(FONT_BODY);
        cb.setBorder(javax.swing.BorderFactory.createLineBorder(FIELD_BORDER, 1));
        return cb;
    }

    // ── Helper: label ─────────────────────────────────────────────────────────
    public static javax.swing.JLabel makeLabel(String text, Font font, Color color) {
        javax.swing.JLabel lbl = new javax.swing.JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    // ── Helper: card panel ────────────────────────────────────────────────────
    public static javax.swing.JPanel makeCard() {
        javax.swing.JPanel p = new javax.swing.JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(0xFF1744, false).brighter());
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 24, 20, 24));
        return p;
    }
}
