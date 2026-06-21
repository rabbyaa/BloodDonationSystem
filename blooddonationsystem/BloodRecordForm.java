package blooddonationsystem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BloodRecordForm extends JFrame {

    private AuthManager authManager;
    private BloodRecord.Type type;
    private UserDashboard parent;

    private JTextField hospitalField, locationField;
    private JComboBox<String> bloodGroupBox, statusBox;
    private JTextArea messageArea;
    private JLabel statusLabel, aiResultLabel;
    private JButton submitBtn;

    public BloodRecordForm(AuthManager authManager, BloodRecord.Type type, UserDashboard parent) {
        this.authManager = authManager;
        this.type        = type;
        this.parent      = parent;
        buildUI();
    }

    private void buildUI() {
        boolean isDonation = (type == BloodRecord.Type.DONATION);
        String typeStr = isDonation ? "Blood Donation" : "Blood Request";
        Color accent   = isDonation ? UITheme.RED_PRIMARY : UITheme.BLUE_INFO;

        setTitle(typeStr + " — BloodLink");
        setSize(540, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0,0,UITheme.BG_DARK,0,getHeight(),
                        isDonation ? new Color(0x1a0000) : new Color(0x001020));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(24, 50, 24, 50));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);

        String icon = isDonation ? "❤️" : "🩸";
        JLabel titleLbl = UITheme.makeLabel(icon + "  " + typeStr, UITheme.FONT_TITLE, accent);
        JLabel subLbl   = UITheme.makeLabel(
            isDonation ? "Fill in your donor details below" : "Describe your blood requirement",
            UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
        subLbl.setHorizontalAlignment(SwingConstants.CENTER);

        String[] groups = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};
        bloodGroupBox = UITheme.makeCombo(groups);
        // Pre-select user's blood group
        String userBG = authManager.getCurrentUser().getBloodGroup();
        for (int i=0; i<groups.length; i++) {
            if (groups[i].equals(userBG)) { bloodGroupBox.setSelectedIndex(i); break; }
        }

        hospitalField = UITheme.makeField(20);
        hospitalField.setToolTipText("Full hospital name (required for verification)");

        locationField = UITheme.makeField(20);
        locationField.setToolTipText("City / Area");

        String[] statuses = {"PENDING", "COMPLETED"};
        statusBox = UITheme.makeCombo(statuses);

        messageArea = new JTextArea(4, 20);
        messageArea.setBackground(UITheme.FIELD_BG);
        messageArea.setForeground(UITheme.WHITE);
        messageArea.setCaretColor(UITheme.WHITE);
        messageArea.setFont(UITheme.FONT_BODY);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.FIELD_BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JScrollPane msgScroll = new JScrollPane(messageArea);
        msgScroll.setBorder(null);

        statusLabel   = UITheme.makeLabel("", UITheme.FONT_SMALL, UITheme.RED_LIGHT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // AI result panel
        aiResultLabel = UITheme.makeLabel("", UITheme.FONT_SMALL, UITheme.GREEN_SUCCESS);
        aiResultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        submitBtn = UITheme.makeButton("Submit & Analyze", accent, UITheme.WHITE);
        submitBtn.setPreferredSize(new Dimension(380, 46));

        JButton cancelBtn = UITheme.makeButton("Cancel", UITheme.BG_SURFACE, UITheme.TEXT_MUTED);
        cancelBtn.setPreferredSize(new Dimension(380, 36));
        cancelBtn.addActionListener(e -> dispose());

        int row = 0;
        gc.gridy = row++; gc.insets = new Insets(0,0,4,0); root.add(titleLbl, gc);
        gc.gridy = row++; root.add(subLbl, gc);

        gc.insets = new Insets(12,0,3,0);
        gc.gridy = row++; root.add(UITheme.makeLabel("Blood Group *", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.insets = new Insets(0,0,6,0);
        gc.gridy = row++; root.add(bloodGroupBox, gc);

        gc.insets = new Insets(8,0,3,0);
        gc.gridy = row++; root.add(UITheme.makeLabel(
            isDonation ? "Your Hospital / Clinic" : "Required At Hospital *",
            UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.insets = new Insets(0,0,6,0);
        gc.gridy = row++; root.add(hospitalField, gc);

        gc.insets = new Insets(8,0,3,0);
        gc.gridy = row++; root.add(UITheme.makeLabel("City / Location *", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.insets = new Insets(0,0,6,0);
        gc.gridy = row++; root.add(locationField, gc);

        gc.insets = new Insets(8,0,3,0);
        gc.gridy = row++; root.add(UITheme.makeLabel("Additional Message", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.insets = new Insets(0,0,6,0);
        gc.gridy = row++; root.add(msgScroll, gc);

        gc.insets = new Insets(8,0,3,0);
        gc.gridy = row++; root.add(UITheme.makeLabel("Record Status", UITheme.FONT_SUB, UITheme.TEXT_LIGHT), gc);
        gc.insets = new Insets(0,0,6,0);
        gc.gridy = row++; root.add(statusBox, gc);

        gc.gridy = row++; root.add(statusLabel, gc);
        gc.gridy = row++; root.add(aiResultLabel, gc);
        gc.insets = new Insets(14,0,6,0);
        gc.gridy = row++; root.add(submitBtn, gc);
        gc.insets = new Insets(4,0,0,0);
        gc.gridy = row++;  root.add(cancelBtn, gc);

        submitBtn.addActionListener(e -> doSubmit());

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG_DARK);
        setContentPane(scroll);
    }

    private void doSubmit() {
        String hospital = hospitalField.getText().trim();
        String location = locationField.getText().trim();
        String message  = messageArea.getText().trim();
        String bg       = (String) bloodGroupBox.getSelectedItem();
        String statusStr= (String) statusBox.getSelectedItem();

        if (hospital.isEmpty() || location.isEmpty()) {
            statusLabel.setText("⚠ Hospital and location are required."); return;
        }

        submitBtn.setEnabled(false);
        submitBtn.setText("Analyzing...");
        statusLabel.setText("");
        aiResultLabel.setText("🤖 Running AI verification...");

        SwingWorker<BloodRecord, Void> worker = new SwingWorker<>() {
            @Override protected BloodRecord doInBackground() {
                User u = authManager.getCurrentUser();
                String id = FileStorageManager.generateRecordId();
                BloodRecord rec = new BloodRecord(id, u.getUserId(), u.getFullName(),
                    u.getPhone(), u.getEmail(), bg, location, hospital, message, type);

                // Apply status from dropdown
                BloodRecord.Status chosenStatus = statusStr.equals("COMPLETED")
                    ? BloodRecord.Status.COMPLETED : BloodRecord.Status.PENDING;
                rec.setStatus(chosenStatus);

                // Run AI before saving
                List<BloodRecord> all = FileStorageManager.loadRecords();
                AISuspicionAnalyzer.analyze(rec, all);

                FileStorageManager.addRecord(rec);
                return rec;
            }

            @Override protected void done() {
                try {
                    BloodRecord rec = get();
                    int score = rec.getAiSuspicionScore();
                    String risk  = rec.getRiskLabel();
                    String reason= rec.getAiReason();
                    Color  col   = Color.decode(AISuspicionAnalyzer.getRiskColor(score));

                    aiResultLabel.setForeground(col);
                    aiResultLabel.setText("<html><center>🤖 AI Risk: <b>" + risk
                        + "</b> (Score: " + score + "/100)<br>"
                        + "<small>" + reason + "</small></center></html>");

                    submitBtn.setEnabled(true);
                    submitBtn.setText("Submit & Analyze");
                    statusLabel.setForeground(UITheme.GREEN_SUCCESS);
                    statusLabel.setText("✓ Record submitted successfully!");

                    parent.refreshAfterForm();

                    // Warn if high risk
                    if (score >= 70) {
                        JOptionPane.showMessageDialog(BloodRecordForm.this,
                            "⚠ Your record was flagged as HIGH RISK by the AI.\n"
                            + "Reason: " + reason + "\n\n"
                            + "An admin will review it. Please ensure all details are accurate.",
                            "AI Flagged — High Risk", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
