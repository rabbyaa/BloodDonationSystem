package blooddonationsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JFrame {

    private AuthManager authManager;
    private JTable recordsTable;
    private DefaultTableModel tableModel;
    private JLabel greetLabel, statsLabel;

    public UserDashboard(AuthManager authManager) {
        this.authManager = authManager;
        buildUI();
        loadUserRecords();
    }

    private void buildUI() {
        User u = authManager.getCurrentUser();
        setTitle("BloodLink — " + u.getFullName());
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0,UITheme.BG_DARK,0,getHeight(),new Color(0x0D1B2A));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };

        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.RED_DARK);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        topBar.setPreferredSize(new Dimension(0, 70));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel logo = UITheme.makeLabel("🩸 BloodLink", new Font("Segoe UI", Font.BOLD, 22), UITheme.WHITE);
        greetLabel  = UITheme.makeLabel("Hi, " + u.getFullName() + " | " + u.getBloodGroup(), UITheme.FONT_BODY, new Color(255,200,200));

        JButton logoutBtn = UITheme.makeButton("Logout", new Color(0x8E0000), UITheme.WHITE);
        logoutBtn.setPreferredSize(new Dimension(100, 34));
        logoutBtn.addActionListener(e -> {
            authManager.logout();
            dispose();
            new SplashScreen(authManager).setVisible(true);
        });

        topBar.add(logo, BorderLayout.WEST);
        topBar.add(greetLabel, BorderLayout.CENTER);
        topBar.add(logoutBtn, BorderLayout.EAST);

        // ── Action cards row ─────────────────────────────────────────────────
        JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 18));
        cardsRow.setOpaque(false);
        cardsRow.add(makeActionCard("❤️", "Donate Blood",
            "Register yourself as a blood donor",
            UITheme.RED_PRIMARY, () -> openRecordForm(BloodRecord.Type.DONATION)));
        cardsRow.add(makeActionCard("🩸", "Request Blood",
            "Post an emergency blood request",
            UITheme.BG_SURFACE, () -> openRecordForm(BloodRecord.Type.REQUEST)));
        cardsRow.add(makeActionCard("👤", "My Profile",
            u.getBloodGroup() + " | " + u.getUniversity(),
            new Color(0x1B3A2D), () -> showProfile()));

        // ── Records table ────────────────────────────────────────────────────
        String[] cols = {"#", "Type", "Blood Group", "Hospital", "Location", "Status", "AI Risk", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        recordsTable = new JTable(tableModel);
        styleTable(recordsTable);

        // Status change on right-click
        JPopupMenu popup = new JPopupMenu();
        JMenu statusMenu = new JMenu("Change Status");
        for (BloodRecord.Status s : BloodRecord.Status.values()) {
            JMenuItem item = new JMenuItem(s.name());
            item.addActionListener(e -> changeStatus(s));
            statusMenu.add(item);
        }
        popup.add(statusMenu);
        recordsTable.setComponentPopupMenu(popup);

        JScrollPane tableScroll = new JScrollPane(recordsTable);
        tableScroll.getViewport().setBackground(UITheme.BG_CARD);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        statsLabel = UITheme.makeLabel("Your Donations & Requests", UITheme.FONT_HEADING, UITheme.WHITE);
        JLabel hint = UITheme.makeLabel("Right-click a row to update status", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        tableHeader.add(statsLabel, BorderLayout.WEST);
        tableHeader.add(hint, BorderLayout.EAST);

        JButton refreshBtn = UITheme.makeButton("↻ Refresh", UITheme.BG_SURFACE, UITheme.TEXT_LIGHT);
        refreshBtn.setPreferredSize(new Dimension(100, 30));
        refreshBtn.addActionListener(e -> loadUserRecords());
        tableHeader.add(refreshBtn, BorderLayout.EAST);

        tablePanel.add(tableHeader, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        root.add(topBar, BorderLayout.NORTH);
        root.add(cardsRow, BorderLayout.CENTER);
        // Wrap cardsRow + tablePanel in a vertical split
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cardsRow, tablePanel);
        split.setDividerLocation(210);
        split.setResizeWeight(0.0);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(4);

        root.add(split, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel makeActionCard(String icon, String title, String sub, Color accent, Runnable action) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,UITheme.BG_CARD,0,getHeight(),accent.darker().darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,18,18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(260, 140));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(2, 0, 2, 0);

        JLabel iconLbl = UITheme.makeLabel(icon, new Font("Segoe UI Emoji", Font.PLAIN, 32), UITheme.WHITE);
        JLabel titleLbl= UITheme.makeLabel(title, UITheme.FONT_SUB, UITheme.WHITE);
        JLabel subLbl  = UITheme.makeLabel("<html>" + sub + "</html>", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);

        gc.gridy = 0; card.add(iconLbl, gc);
        gc.gridy = 1; card.add(titleLbl, gc);
        gc.gridy = 2; card.add(subLbl, gc);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
        });
        return card;
    }

    private void styleTable(JTable table) {
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.WHITE);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(34);
        table.setGridColor(new Color(0x1565C0, false));
        table.setSelectionBackground(UITheme.RED_DARK);
        table.setSelectionForeground(UITheme.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(UITheme.RED_DARK);
        table.getTableHeader().setForeground(UITheme.WHITE);
        table.getTableHeader().setFont(UITheme.FONT_SUB);

        // Color status and risk cells
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(sel ? UITheme.RED_DARK : UITheme.BG_CARD);
                setForeground(UITheme.WHITE);
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (val != null) {
                    String v = val.toString();
                    if (col == 5) { // Status
                        if (v.equals("Completed")) setForeground(UITheme.GREEN_SUCCESS);
                        else if (v.equals("Rejected")) setForeground(UITheme.RED_DANGER);
                        else setForeground(UITheme.ORANGE_WARN);
                    } else if (col == 6) { // AI Risk
                        if (v.contains("HIGH")) setForeground(UITheme.RED_DANGER);
                        else if (v.contains("MED")) setForeground(UITheme.ORANGE_WARN);
                        else setForeground(UITheme.GREEN_SUCCESS);
                    }
                }
                return this;
            }
        });
    }

    private void loadUserRecords() {
        tableModel.setRowCount(0);
        List<BloodRecord> records = FileStorageManager.getRecordsByUser(
                authManager.getCurrentUser().getUserId());
        int i = 1;
        for (BloodRecord r : records) {
            tableModel.addRow(new Object[]{
                i++,
                r.getTypeLabel(),
                r.getBloodGroup(),
                r.getHospital(),
                r.getLocation(),
                r.getStatusLabel(),
                r.getRiskLabel() + " (" + r.getAiSuspicionScore() + ")",
                r.getTimestamp().substring(0, 10)
            });
        }
        int donations = (int) records.stream().filter(r -> r.getType() == BloodRecord.Type.DONATION).count();
        int requests  = (int) records.stream().filter(r -> r.getType() == BloodRecord.Type.REQUEST).count();
        statsLabel.setText("Your Records — " + donations + " donations, " + requests + " requests");
    }

    private void openRecordForm(BloodRecord.Type type) {
        new BloodRecordForm(authManager, type, this).setVisible(true);
    }

    private void changeStatus(BloodRecord.Status newStatus) {
        int row = recordsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a record first."); return; }
        List<BloodRecord> records = FileStorageManager.getRecordsByUser(
                authManager.getCurrentUser().getUserId());
        if (row >= records.size()) return;
        BloodRecord rec = records.get(row);
        rec.setStatus(newStatus);
        FileStorageManager.updateRecord(rec);
        loadUserRecords();
        JOptionPane.showMessageDialog(this,
            "Status updated to: " + newStatus.name(),
            "Status Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showProfile() {
        User u = authManager.getCurrentUser();
        String info = "<html><b>Name:</b> " + u.getFullName()
            + "<br><b>Email:</b> " + u.getEmail()
            + "<br><b>Phone:</b> " + u.getPhone()
            + "<br><b>Blood Group:</b> " + u.getBloodGroup()
            + "<br><b>University:</b> " + u.getUniversity()
            + "<br><b>Role:</b> " + u.getRole() + "</html>";
        JOptionPane.showMessageDialog(this, info, "My Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshAfterForm() {
        loadUserRecords();
    }
}
