package src.Coordinator.ui;

import src.Coordinator.controller.SessionController;
import src.Coordinator.controller.ReportController;
import src.Coordinator.model.Session;
import src.common.model.Report;
import src.common.model.Award;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CoordinatorPanel extends JPanel {
    private SessionController sessionController;
    private ReportController reportController;
    
    private JTabbedPane tabbedPane;
    
    // User lists
    private List<String> studentList;
    private List<String> evaluatorList;
    
    // Session Management Tab
    private JList<Session> sessionList;
    private DefaultListModel<Session> sessionListModel;
    private JTextField txtDate, txtTime, txtVenue, txtCapacity;
    private JComboBox<String> cbType;
    private JList<String> studentSelector, evaluatorSelector;
    
    // Reports Tab
    private JTextArea reportArea;
    private JButton btnGenerateSchedule, btnGenerateEvaluation, btnExportCSV;
    
    // Awards Tab
    private JTextArea awardArea;
    private JButton btnComputeAwards;
    
    // Date formatter for validation
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    public CoordinatorPanel() {
        this.sessionController = new SessionController();
        this.reportController = new ReportController();
        initializeUserLists();
        
        initUI();
        loadSessions();
    }
    
    private void initializeUserLists() {
        studentList = Arrays.asList("STU001 - John Doe", "STU002 - Jane Smith", "STU003 - Bob Johnson");
        evaluatorList = Arrays.asList("EVAL001 - Dr. Smith", "EVAL002 - Prof. Chen");
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        // Tab 1: Session Management
        tabbedPane.addTab("Session Management", createSessionPanel());
        
        // Tab 2: Reports
        tabbedPane.addTab("Reports", createReportPanel());
        
        // Tab 3: Awards
        tabbedPane.addTab("Awards", createAwardPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createSessionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Session List
        sessionListModel = new DefaultListModel<>();
        sessionList = new JList<>(sessionListModel);
        sessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionList.setCellRenderer(new SessionCellRenderer());
        JScrollPane listScroll = new JScrollPane(sessionList);
        listScroll.setPreferredSize(new Dimension(300, 400));
        panel.add(listScroll, BorderLayout.WEST);
        
        // Session Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Date (Date Picker style)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtDate = new JTextField(15);
        formPanel.add(txtDate, gbc);
        gbc.gridx = 2;
        JButton btnToday = new JButton("Today");
        btnToday.addActionListener(e -> txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
        formPanel.add(btnToday, gbc);
        
        // Time
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1;
        txtTime = new JTextField(15);
        formPanel.add(txtTime, gbc);
        gbc.gridx = 2;
        JButton btnNow = new JButton("Now");
        btnNow.addActionListener(e -> txtTime.setText(new SimpleDateFormat("HH:mm").format(new Date())));
        formPanel.add(btnNow, gbc);
        
        // Venue
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Venue:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtVenue = new JTextField(15);
        formPanel.add(txtVenue, gbc);
        
        // Type Dropdown
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        String[] types = {"Oral Presentation", "Poster Presentation"};
        cbType = new JComboBox<>(types);
        formPanel.add(cbType, gbc);
        
        // Capacity
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtCapacity = new JTextField(15);
        formPanel.add(txtCapacity, gbc);
        
        // Student Selector (Multiple selection)
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Select Students:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        studentSelector = new JList<>(studentList.toArray(new String[0]));
        studentSelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane studentScroll = new JScrollPane(studentSelector);
        studentScroll.setPreferredSize(new Dimension(200, 100));
        formPanel.add(studentScroll, gbc);
        
        // Evaluator Selector (Multiple selection)
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Select Evaluators:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        evaluatorSelector = new JList<>(evaluatorList.toArray(new String[0]));
        evaluatorSelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane evaluatorScroll = new JScrollPane(evaluatorSelector);
        evaluatorScroll.setPreferredSize(new Dimension(200, 100));
        formPanel.add(evaluatorScroll, gbc);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton btnCreate = new JButton("Create Session");
        JButton btnUpdate = new JButton("Update Session");
        JButton btnDelete = new JButton("Delete Session");
        
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Session selection listener
        sessionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && sessionList.getSelectedValue() != null) {
                Session selected = sessionList.getSelectedValue();
                loadSessionData(selected);
            }
        });
        
        // Button listeners
        btnCreate.addActionListener(e -> createSession());
        btnUpdate.addActionListener(e -> updateSession());
        btnDelete.addActionListener(e -> deleteSession());
        
        return panel;
    }
    
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Report buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        btnGenerateSchedule = new JButton("Generate Schedule Report");
        btnGenerateEvaluation = new JButton("Generate Evaluation Report");
        btnExportCSV = new JButton("Export to CSV");
        
        buttonPanel.add(btnGenerateSchedule);
        buttonPanel.add(btnGenerateEvaluation);
        buttonPanel.add(btnExportCSV);
        
        // Report display area
        reportArea = new JTextArea(20, 60);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane reportScroll = new JScrollPane(reportArea);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(reportScroll, BorderLayout.CENTER);
        
        // Add listeners
        btnGenerateSchedule.addActionListener(e -> generateScheduleReport());
        btnGenerateEvaluation.addActionListener(e -> generateEvaluationReport());
        btnExportCSV.addActionListener(e -> exportReportToCSV());
        
        return panel;
    }
    
    private JPanel createAwardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Award button
        JPanel buttonPanel = new JPanel();
        btnComputeAwards = new JButton("Compute Awards");
        buttonPanel.add(btnComputeAwards);
        
        // Award display area
        awardArea = new JTextArea(20, 60);
        awardArea.setEditable(false);
        awardArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane awardScroll = new JScrollPane(awardArea);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(awardScroll, BorderLayout.CENTER);
        
        // Add listener
        btnComputeAwards.addActionListener(e -> computeAwards());
        
        return panel;
    }
    
    private class SessionCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Session) {
                Session session = (Session) value;
                setText(String.format("%s: %s %s (%s)", 
                    session.getSessionId(),
                    session.getDate(),
                    session.getTime(),
                    session.getSessionType()));
            }
            return this;
        }
    }
    
    private void loadSessions() {
        sessionListModel.clear();
        List<Session> sessions = sessionController.getAllSessions();
        for (Session session : sessions) {
            sessionListModel.addElement(session);
        }
    }
    
    private void loadSessionData(Session session) {
        txtDate.setText(session.getDate());
        txtTime.setText(session.getTime());
        txtVenue.setText(session.getVenue());
        txtCapacity.setText(String.valueOf(session.getMaxCapacity()));
        
        // Set type
        for (int i = 0; i < cbType.getItemCount(); i++) {
            if (cbType.getItemAt(i).equals(session.getSessionType())) {
                cbType.setSelectedIndex(i);
                break;
            }
        }
        
        // Select students
        List<String> selectedStudents = new ArrayList<>();
        for (String studentId : session.getStudentIds()) {
            for (String studentItem : studentList) {
                if (studentItem.startsWith(studentId)) {
                    selectedStudents.add(studentItem);
                    break;
                }
            }
        }
        studentSelector.setSelectedIndices(getIndices(selectedStudents, studentList));
        
        // Select evaluators
        List<String> selectedEvaluators = new ArrayList<>();
        for (String evaluatorId : session.getEvaluatorIds()) {
            for (String evaluatorItem : evaluatorList) {
                if (evaluatorItem.startsWith(evaluatorId)) {
                    selectedEvaluators.add(evaluatorItem);
                    break;
                }
            }
        }
        evaluatorSelector.setSelectedIndices(getIndices(selectedEvaluators, evaluatorList));
    }
    
    private int[] getIndices(List<String> selectedItems, List<String> allItems) {
        List<Integer> indices = new ArrayList<>();
        for (String selected : selectedItems) {
            int index = allItems.indexOf(selected);
            if (index >= 0) {
                indices.add(index);
            }
        }
        return indices.stream().mapToInt(i -> i).toArray();
    }
    
    private boolean checkTimeConflict(String date, String time) {
        try {
            Date newDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date + " " + time);
            
            for (int i = 0; i < sessionListModel.size(); i++) {
                Session existing = sessionListModel.get(i);
                if (sessionList.getSelectedValue() != null && 
                    existing.getSessionId().equals(sessionList.getSelectedValue().getSessionId())) {
                    continue; // Skip the session being edited
                }
                
                Date existingDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .parse(existing.getDate() + " " + existing.getTime());
                
                // Check if same date and within 2 hours (assuming sessions are 2 hours)
                if (existing.getDate().equals(date)) {
                    long diffHours = Math.abs(newDateTime.getTime() - existingDateTime.getTime()) / (1000 * 60 * 60);
                    if (diffHours < 2) {
                        JOptionPane.showMessageDialog(this,
                            "Time conflict with session " + existing.getSessionId() + 
                            " at " + existing.getTime(),
                            "Time Conflict", JOptionPane.WARNING_MESSAGE);
                        return true;
                    }
                }
            }
            return false;
        } catch (ParseException e) {
            return false;
        }
    }
    
    private void createSession() {
        try {
            // Validate fields
            if (txtDate.getText().isEmpty() || txtTime.getText().isEmpty() || 
                txtVenue.getText().isEmpty() || txtCapacity.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields.");
                return;
            }
            
            // Check time conflict
            if (checkTimeConflict(txtDate.getText(), txtTime.getText())) {
                return;
            }
            
            String sessionId = "SESS" + (sessionListModel.size() + 1);
            String date = txtDate.getText();
            String time = txtTime.getText();
            String venue = txtVenue.getText();
            String type = (String) cbType.getSelectedItem();
            int capacity = Integer.parseInt(txtCapacity.getText());
            
            Session session = new Session(sessionId, date, time, venue, type, capacity);
            
            // Add selected students
            for (String studentItem : studentSelector.getSelectedValuesList()) {
                String studentId = studentItem.split(" - ")[0];
                session.addStudent(studentId);
            }
            
            // Add selected evaluators
            for (String evaluatorItem : evaluatorSelector.getSelectedValuesList()) {
                String evaluatorId = evaluatorItem.split(" - ")[0];
                session.addEvaluator(evaluatorId);
            }
            
            if (sessionController.createSession(session)) {
                JOptionPane.showMessageDialog(this, "Session created successfully!");
                loadSessions();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating session.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid capacity number.");
        }
    }
    
    private void updateSession() {
        Session selected = sessionList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a session to update.");
            return;
        }
        
        try {
            // Check time conflict
            if (checkTimeConflict(txtDate.getText(), txtTime.getText())) {
                return;
            }
            
            selected.setDate(txtDate.getText());
            selected.setTime(txtTime.getText());
            selected.setVenue(txtVenue.getText());
            selected.setSessionType((String) cbType.getSelectedItem());
            
            // Update students
            selected.getStudentIds().clear();
            for (String studentItem : studentSelector.getSelectedValuesList()) {
                String studentId = studentItem.split(" - ")[0];
                selected.addStudent(studentId);
            }
            
            // Update evaluators
            selected.getEvaluatorIds().clear();
            for (String evaluatorItem : evaluatorSelector.getSelectedValuesList()) {
                String evaluatorId = evaluatorItem.split(" - ")[0];
                selected.addEvaluator(evaluatorId);
            }
            
            if (sessionController.updateSession(selected)) {
                JOptionPane.showMessageDialog(this, "Session updated successfully!");
                loadSessions();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating session.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void deleteSession() {
        Session selected = sessionList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a session to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete session: " + selected.getSessionId() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (sessionController.deleteSession(selected.getSessionId())) {
                JOptionPane.showMessageDialog(this, "Session deleted successfully!");
                loadSessions();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting session.");
            }
        }
    }
    
    private void generateScheduleReport() {
        Report report = reportController.generateScheduleReport();
        reportArea.setText(report.getFormattedContent());
        JOptionPane.showMessageDialog(this, "Schedule report generated!");
    }
    
    private void generateEvaluationReport() {
        Report report = reportController.generateEvaluationReport();
        reportArea.setText(report.getFormattedContent());
        JOptionPane.showMessageDialog(this, "Evaluation report generated!");
    }
    
    private void exportReportToCSV() {
        String currentReport = reportArea.getText();
        if (currentReport.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Generate a report first.");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("report.csv"));
        int option = fileChooser.showSaveDialog(this);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.write(currentReport.replace("\t", ","));
                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage());
            }
        }
    }
    
    private void computeAwards() {
        List<Award> awards = reportController.computeAwards();
        StringBuilder sb = new StringBuilder();
        sb.append("=== AWARD WINNERS ===\n\n");
        
        for (Award award : awards) {
            sb.append("Award: ").append(award.getAwardType()).append("\n");
            sb.append("Winner: ").append(award.getStudentName()).append("\n");
            sb.append("Student ID: ").append(award.getStudentId()).append("\n");
            sb.append("Submission: ").append(award.getSubmissionTitle()).append("\n");
            sb.append("Score: ").append(String.format("%.2f", award.getScore())).append("\n");
            sb.append("------------------------\n");
        }
        
        awardArea.setText(sb.toString());
        JOptionPane.showMessageDialog(this, "Awards computed successfully!");
    }
    
    private void clearForm() {
        txtDate.setText("");
        txtTime.setText("");
        txtVenue.setText("");
        txtCapacity.setText("");
        studentSelector.clearSelection();
        evaluatorSelector.clearSelection();
    }
}