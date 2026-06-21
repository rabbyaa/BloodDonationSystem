package blooddonationsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private AuthManager authManager;
    private JTable recordsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilter, statusFilter;
    private JLabel statsLabel;
    private List<BloodRecord> currentRecords;

    public AdminDashboard(AuthManager authManager) {
        this.authManager = authManager;
        buildUI();
        doSearch();
    }

    private void buildUI() {
        setTitle("BloodLink — Admin Panel");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0,UITheme.BG_DARK,0,getHeight(),new Color(0x0D0D1A));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };

        // ── Top bar ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0,new Color(0x1B1B3A),getWidth(),0,UITheme.RED_DARK);
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        topBar.setPreferredSize(new Dimension(0, 64));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JLabel logo     = UITheme.makeLabel("🛡  BloodLink Admin", new Font("Segoe UI", Font.BOLD, 20), UITheme.WHITE);
        JLabel adminTag = UITheme.makeLabel("Logged in as: " + authManager.getCurrentUser().getFullName(),
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);

        JButton logoutBtn = UITheme.makeButton("Logout", UITheme.RED_DARK, UITheme.WHITE);
        logoutBtn.setPreferredSize(new Dimension(100, 32));
        logoutBtn.addActionListener(e -> {
            authManager.logout();
            dispose();
            new SplashScreen(authManager).setVisible(true);
        });

        JPanel leftTop = new JPanel(new GridLayout(2,1));
        leftTop.setOpaque(false);
        leftTop.add(logo);
        leftTop.add(adminTag);

        topBar.add(leftTop, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);

        // ── Stats bar ────────────────────────────────────────────────────────
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        statsBar.setOpaque(false);
        statsBar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        statsLabel = UITheme.makeLabel("Loading...", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        statsBar.add(statsLabel);

        // ── Search & filter bar ──────────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchBar.setOpaque(false);
        searchBar.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        searchField = UITheme.makeField(22);
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Search by name, blood group, location…");

        typeFilter   = UITheme.makeCombo(new String[]{"ALL", "Donation", "Request"});
        statusFilter = UITheme.makeCombo(new String[]{"ALL", "Pending", "Completed", "Rejected"});
        typeFilter.setPreferredSize(new Dimension(120, 36));
        statusFilter.setPreferredSize(new Dimension(120, 36));

        JButton searchBtn    = UITheme.makeButton("🔍 Search", UITheme.RED_PRIMARY, UITheme.WHITE);
        JButton reanalyzeBtn = UITheme.makeButton("🤖 Re-Analyze All", UITheme.BG_SURFACE, UITheme.TEXT_LIGHT);
        searchBtn.setPreferredSize(new Dimension(110, 36));
        reanalyzeBtn.setPreferredSize(new Dimension(150, 36));

        searchBar.add(UITheme.makeLabel("Search:", UITheme.FONT_SUB, UITheme.TEXT_LIGHT));
        searchBar.add(searchField);
        searchBar.add(UITheme.makeLabel("Type:", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        searchBar.add(typeFilter);
        searchBar.add(UITheme.makeLabel("Status:", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        searchBar.add(statusFilter);
        searchBar.add(searchBtn);
        searchBar.add(reanalyzeBtn);

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());
        reanalyzeBtn.addActionListener(e -> reanalyzeAll());
        typeFilter.addActionListener(e -> doSearch());
        statusFilter.addActionListener(e -> doSearch());

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(searchBar, BorderLayout.NORTH);
        topSection.add(statsBar, BorderLayout.SOUTH);

        // ── Records table ────────────────────────────────────────────────────
        String[] cols = {"#","Type","User","Email","Blood","Hospital","Location",
                         "Status","AI Score","Risk","Date","Flagged"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        recordsTable = new JTable(tableModel);
        styleTable(recordsTable);

        // Column widths
        int[] widths = {30,80,120,160,60,130,110,90,70,90,90,70};
        for (int i=0; i<widths.length; i++) {
            recordsTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane tableScroll = new JScrollPane(recordsTable);
        tableScroll.getViewport().setBackground(UITheme.BG_CARD);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        // ── Right-click context menu ─────────────────────────────────────────
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(UITheme.BG_CARD);

        JMenuItem verifyItem  = new JMenuItem("✔ Mark as COMPLETED");
        JMenuItem rejectItem  = new JMenuItem("✘ Mark as REJECTED");
        JMenuItem flagItem    = new JMenuItem("🚩 Flag as Suspicious");
        JMenuItem unflagItem  = new JMenuItem("✅ Remove Flag");
        JMenuItem detailItem  = new JMenuItem("📋 View Full Details");

        styleMenuItem(verifyItem, UITheme.GREEN_SUCCESS);
        styleMenuItem(rejectItem, UITheme.RED_DANGER);
        styleMenuItem(flagItem,   UITheme.ORANGE_WARN);
        styleMenuItem(unflagItem, UITheme.BLUE_INFO);
        styleMenuItem(detailItem, UITheme.TEXT_LIGHT);

        popup.add(detailItem);
        popup.addSeparator();
        popup.add(verifyItem);
        popup.add(rejectItem);
        popup.addSeparator();
        popup.add(flagItem);
        popup.add(unflagItem);

        verifyItem.addActionListener(e -> changeStatus(BloodRecord.Status.COMPLETED));
        rejectItem.addActionListener(e -> changeStatus(BloodRecord.Status.REJECTED));
        flagItem.addActionListener(e   -> setFlag(true));
        unflagItem.addActionListener(e -> setFlag(false));
        detailItem.addActionListener(e -> showDetails());

        recordsTable.setComponentPopupMenu(popup);
        recordsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = recordsTable.rowAtPoint(e.getPoint());
                if (row >= 0) recordsTable.setRowSelectionInterval(row, row);
            }
        });

        // ── Bottom action bar ────────────────────────────────────────────────
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));

        JLabel hint = UITheme.makeLabel("Right-click a record to verify, reject, or flag it",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JButton refreshBtn = UITheme.makeButton("↻ Refresh", UITheme.BG_SURFACE, UITheme.TEXT_LIGHT);
        refreshBtn.setPreferredSize(new Dimension(100, 32));
        refreshBtn.addActionListener(e -> doSearch());

        bottomBar.add(hint);
        bottomBar.add(refreshBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        center.add(tableScroll, BorderLayout.CENTER);

        root.add(topBar, BorderLayout.NORTH);
        root.add(topSection, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSection, center);
        split.setDividerLocation(100);
        split.setResizeWeight(0.0);
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(4);

        root.add(topBar, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        root.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void styleMenuItem(JMenuItem item, Color fg) {
        item.setBackground(UITheme.BG_CARD);
        item.setForeground(fg);
        item.setFont(UITheme.FONT_BODY);
    }

    private void styleTable(JTable table) {
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.WHITE);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(new Color(0x1E3A5F));
        table.setSelectionBackground(UITheme.RED_DARK);
        table.setSelectionForeground(UITheme.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.getTableHeader().setBackground(new Color(0x8E0000));
        table.getTableHeader().setForeground(UITheme.WHITE);
        table.getTableHeader().setFont(UITheme.FONT_SUB);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                boolean flagged = currentRecords != null && row < currentRecords.size()
                        && currentRecords.get(row).isFlaggedByAdmin();
                setBackground(sel ? UITheme.RED_DARK : (flagged ? new Color(0x3A1A00) : UITheme.BG_CARD));
                setForeground(UITheme.WHITE);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                if (val != null) {
                    String v = val.toString();
                    if (col == 7) { // Status
                        if (v.equals("Completed")) setForeground(UITheme.GREEN_SUCCESS);
                        else if (v.equals("Rejected")) setForeground(UITheme.RED_DANGER);
                        else setForeground(UITheme.ORANGE_WARN);
                    } else if (col == 9) { // Risk
                        if (v.contains("HIGH")) setForeground(UITheme.RED_DANGER);
                        else if (v.contains("MED")) setForeground(UITheme.ORANGE_WARN);
                        else setForeground(UITheme.GREEN_SUCCESS);
                    } else if (col == 11) { // Flagged
                        setForeground(v.equals("YES") ? UITheme.RED_ACCENT : UITheme.TEXT_MUTED);
                    }
                }
                return this;
            }
        });
    }

    private void doSearch() {
        String query  = searchField != null ? searchField.getText().trim() : "";
        String type   = typeFilter   != null ? (String) typeFilter.getSelectedItem()   : "ALL";
        String status = statusFilter != null ? (String) statusFilter.getSelectedItem() : "ALL";

        currentRecords = FileStorageManager.searchRecords(query, type, status);
        tableModel.setRowCount(0);

        long total     = FileStorageManager.loadRecords().size();
        long highRisk  = FileStorageManager.loadRecords().stream()
                .filter(r -> r.getAiSuspicionScore() >= 70).count();
        long flagged   = FileStorageManager.loadRecords().stream()
                .filter(BloodRecord::isFlaggedByAdmin).count();
        statsLabel.setText("Total: " + total + " records  |  High Risk: " + highRisk
                + "  |  Admin Flagged: " + flagged + "  |  Showing: " + currentRecords.size());

        int i = 1;
        for (BloodRecord r : currentRecords) {
            tableModel.addRow(new Object[]{
                i++,
                r.getTypeLabel(),
                r.getUserName(),
                r.getUserEmail(),
                r.getBloodGroup(),
                r.getHospital(),
                r.getLocation(),
                r.getStatusLabel(),
                r.getAiSuspicionScore(),
                r.getRiskLabel(),
                r.getTimestamp().substring(0, 10),
                r.isFlaggedByAdmin() ? "YES" : "-"
            });
        }
    }

    private BloodRecord getSelectedRecord() {
        int row = recordsTable.getSelectedRow();
        if (row < 0 || currentRecords == null || row >= currentRecords.size()) return null;
        return currentRecords.get(row);
    }

    private void changeStatus(BloodRecord.Status newStatus) {
        BloodRecord rec = getSelectedRecord();
        if (rec == null) { JOptionPane.showMessageDialog(this, "Select a record first."); return; }
        rec.setStatus(newStatus);
        FileStorageManager.updateRecord(rec);
        doSearch();
        JOptionPane.showMessageDialog(this,
            "✔ Status updated to: " + newStatus.name(),
            "Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setFlag(boolean flag) {
        BloodRecord rec = getSelectedRecord();
        if (rec == null) { JOptionPane.showMessageDialog(this, "Select a record first."); return; }
        rec.setFlaggedByAdmin(flag);
        FileStorageManager.updateRecord(rec);
        doSearch();
    }

    private void showDetails() {
        BloodRecord rec = getSelectedRecord();
        if (rec == null) { JOptionPane.showMessageDialog(this, "Select a record first."); return; }

        Color riskColor = Color.decode(AISuspicionAnalyzer.getRiskColor(rec.getAiSuspicionScore()));
        String html = "<html><body style='font-family:Segoe UI;width:380px;'>"
            + "<h2 style='color:#C62828;'>" + rec.getTypeLabel() + " Details</h2>"
            + "<table cellpadding='4'>"
            + row("Record ID",    rec.getRecordId())
            + row("Submitted By", rec.getUserName())
            + row("Email",        rec.getUserEmail())
            + row("Phone",        rec.getUserPhone())
            + row("Blood Group",  rec.getBloodGroup())
            + row("Hospital",     rec.getHospital())
            + row("Location",     rec.getLocation())
            + row("Status",       rec.getStatusLabel())
            + row("Date",         rec.getTimestamp())
            + row("Message",      "<i>" + (rec.getMessage().isEmpty() ? "—" : rec.getMessage()) + "</i>")
            + "</table>"
            + "<hr>"
            + "<h3 style='color:" + String.format("#%02X%02X%02X", riskColor.getRed(),
                    riskColor.getGreen(), riskColor.getBlue()) + ";'>"
            + "🤖 AI Analysis: " + rec.getRiskLabel() + " (" + rec.getAiSuspicionScore() + "/100)</h3>"
            + "<p>" + rec.getAiReason() + "</p>"
            + (rec.isFlaggedByAdmin() ? "<p style='color:#FF1744;'>⚑ FLAGGED BY ADMIN</p>" : "")
            + "</body></html>";

        JLabel label = new JLabel(html);
        JOptionPane.showMessageDialog(this, label, "Record Details", JOptionPane.PLAIN_MESSAGE);
    }

    private String row(String key, String val) {
        return "<tr><td><b>" + key + ":</b></td><td>" + val + "</td></tr>";
    }

    private void reanalyzeAll() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Re-run AI analysis on ALL records?\nThis may take a moment.",
            "Confirm Re-Analysis", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                List<BloodRecord> all = FileStorageManager.loadRecords();
                for (BloodRecord r : all) {
                    AISuspicionAnalyzer.analyze(r, all);
                }
                return null;
            }
            @Override protected void done() {
                doSearch();
                JOptionPane.showMessageDialog(AdminDashboard.this,
                    "✔ AI re-analysis complete for all records.",
                    "Done", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }
}
