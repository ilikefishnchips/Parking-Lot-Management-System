package src.Coordinator.ui;

import Coordinator.controller.SessionController;
import Coordinator.controller.ReportController;
import Coordinator.model.Session;
import common.model.Report;
import common.model.Award;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CoordinatorPanel extends JPanel {
    private SessionController sessionController;
    private ReportController reportController;
    
    private JTabbedPane tabbedPane;
    
    // Session Management Tab
    private JList<Session> sessionList;
    private DefaultListModel<Session> sessionListModel;
    private JTextField txtDate, txtTime, txtVenue, txtType, txtCapacity;
    private JTextArea txtStudents, txtEvaluators;
    
    // Reports Tab
    private JTextArea reportArea;
    private JButton btnGenerateSchedule, btnGenerateEvaluation, btnExportCSV;
    
    // Awards Tab
    private JTextArea awardArea;
    private JButton btnComputeAwards;
    
    public CoordinatorPanel() {
        this.sessionController = new SessionController();
        this.reportController = new ReportController();
        
        initUI();
        loadSessions();
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
        JScrollPane listScroll = new JScrollPane(sessionList);
        panel.add(listScroll, BorderLayout.CENTER);
        
        // Session Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Date
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtDate = new JTextField(15);
        formPanel.add(txtDate, gbc);
        
        // Time
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1;
        txtTime = new JTextField(15);
        formPanel.add(txtTime, gbc);
        
        // Venue
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Venue:"), gbc);
        gbc.gridx = 1;
        txtVenue = new JTextField(15);
        formPanel.add(txtVenue, gbc);
        
        // Type
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Type (Oral/Poster):"), gbc);
        gbc.gridx = 1;
        txtType = new JTextField(15);
        formPanel.add(txtType, gbc);
        
        // Capacity
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        txtCapacity = new JTextField(15);
        formPanel.add(txtCapacity, gbc);
        
        // Students
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Student IDs (comma separated):"), gbc);
        gbc.gridx = 1;
        txtStudents = new JTextArea(3, 15);
        JScrollPane studentScroll = new JScrollPane(txtStudents);
        formPanel.add(studentScroll, gbc);
        
        // Evaluators
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Evaluator IDs (comma separated):"), gbc);
        gbc.gridx = 1;
        txtEvaluators = new JTextArea(3, 15);
        JScrollPane evaluatorScroll = new JScrollPane(txtEvaluators);
        formPanel.add(evaluatorScroll, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnCreate = new JButton("Create Session");
        JButton btnUpdate = new JButton("Update Session");
        JButton btnDelete = new JButton("Delete Session");
        
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.SOUTH);
        
        // Add listeners
        sessionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && sessionList.getSelectedValue() != null) {
                Session selected = sessionList.getSelectedValue();
                txtDate.setText(selected.getDate());
                txtTime.setText(selected.getTime());
                txtVenue.setText(selected.getVenue());
                txtType.setText(selected.getSessionType());
                txtCapacity.setText(String.valueOf(selected.getMaxCapacity()));
                txtStudents.setText(String.join(",", selected.getStudentIds()));
                txtEvaluators.setText(String.join(",", selected.getEvaluatorIds()));
            }
        });
        
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
    
    private void loadSessions() {
        sessionListModel.clear();
        List<Session> sessions = sessionController.getAllSessions();
        for (Session session : sessions) {
            sessionListModel.addElement(session);
        }
    }
    
    private void createSession() {
        try {
            String sessionId = "SESS" + System.currentTimeMillis() % 10000;
            String date = txtDate.getText();
            String time = txtTime.getText();
            String venue = txtVenue.getText();
            String type = txtType.getText();
            int capacity = Integer.parseInt(txtCapacity.getText());
            
            Session session = new Session(sessionId, date, time, venue, type, capacity);
            
            // Add students
            String[] studentIds = txtStudents.getText().split(",");
            for (String id : studentIds) {
                id = id.trim();
                if (!id.isEmpty()) {
                    session.addStudent(id);
                }
            }
            
            // Add evaluators
            String[] evaluatorIds = txtEvaluators.getText().split(",");
            for (String id : evaluatorIds) {
                id = id.trim();
                if (!id.isEmpty()) {
                    session.addEvaluator(id);
                }
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
            selected.setDate(txtDate.getText());
            selected.setTime(txtTime.getText());
            selected.setVenue(txtVenue.getText());
            selected.setSessionType(txtType.getText());
            
            // Update students
            selected.getStudentIds().clear();
            String[] studentIds = txtStudents.getText().split(",");
            for (String id : studentIds) {
                id = id.trim();
                if (!id.isEmpty()) {
                    selected.addStudent(id);
                }
            }
            
            // Update evaluators
            selected.getEvaluatorIds().clear();
            String[] evaluatorIds = txtEvaluators.getText().split(",");
            for (String id : evaluatorIds) {
                id = id.trim();
                if (!id.isEmpty()) {
                    selected.addEvaluator(id);
                }
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
        txtType.setText("");
        txtCapacity.setText("");
        txtStudents.setText("");
        txtEvaluators.setText("");
    }
}