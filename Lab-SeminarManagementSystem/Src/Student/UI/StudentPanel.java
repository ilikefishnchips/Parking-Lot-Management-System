package src.Student.ui;

import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import src.Coordinator.model.Session;
import src.Student.controller.StudentController;
import src.Student.model.Student;

public class StudentPanel extends JPanel {
    private Student currentStudent;
    private StudentController controller;
    private List<Session> studentSessions;
    
    // UI Components
    private JTextField txtTitle, txtFilePath;
    private JTextArea txtAbstract;
    private JComboBox<String> cbType, cbSupervisor, cbSession, cbEvaluator;
    private JButton btnUpload, btnSubmit;
    private JLabel lblSessionInfo;
    
    // Supervisor options
    private String[] supervisors = {"Dr. Smith", "Prof. Chen", "Dr. Lee", "Prof. Kumar", "Dr. Johnson"};
    
    public StudentPanel(Student student) {
        this.currentStudent = student;
        this.controller = new StudentController();
        this.studentSessions = controller.getStudentSessions(student.getStudentId());
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblHeader = new JLabel("Student Seminar Registration: " + student.getName() + " (" + student.getStudentId() + ")");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblHeader, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 1. Session Selection (if multiple sessions)
        if (!studentSessions.isEmpty()) {
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Select Session:"), gbc);
            gbc.gridx = 1;
            cbSession = new JComboBox<>();
            for (Session session : studentSessions) {
                cbSession.addItem(session.getSessionId() + ": " + 
                    session.getSessionType() + " (" + session.getStartDateTime() + ")");
            }
            cbSession.addActionListener(e -> updateEvaluatorList());
            formPanel.add(cbSession, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.gridwidth = 2;
            lblSessionInfo = new JLabel("");
            lblSessionInfo.setFont(new Font("Arial", Font.PLAIN, 11));
            lblSessionInfo.setForeground(Color.DARK_GRAY);
            formPanel.add(lblSessionInfo, gbc);
            
            gbc.gridwidth = 1;
        } else {
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.gridwidth = 2;
            JLabel lblNoSession = new JLabel("No sessions assigned yet. Please contact coordinator.");
            lblNoSession.setForeground(Color.RED);
            lblNoSession.setHorizontalAlignment(SwingConstants.CENTER);
            formPanel.add(lblNoSession, gbc);
            gbc.gridwidth = 1;
        }

        // 2. Research Title
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Research Title:"), gbc);
        gbc.gridx = 1;
        txtTitle = new JTextField(25);
        formPanel.add(txtTitle, gbc);

        // 3. Abstract
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Abstract:"), gbc);
        gbc.gridx = 1;
        txtAbstract = new JTextArea(5, 25);
        txtAbstract.setLineWrap(true);
        JScrollPane scrollAbstract = new JScrollPane(txtAbstract);
        formPanel.add(scrollAbstract, gbc);

        // 4. Supervisor
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Supervisor:"), gbc);
        gbc.gridx = 1;
        cbSupervisor = new JComboBox<>(supervisors);
        formPanel.add(cbSupervisor, gbc);

        // 5. Presentation Type
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Presentation Type:"), gbc);
        gbc.gridx = 1;
        String[] types = {"Oral Presentation", "Poster Presentation"};
        cbType = new JComboBox<>(types);
        formPanel.add(cbType, gbc);

        // 6. Evaluator Selection (Choose from multiple)
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Choose Evaluator:"), gbc);
        gbc.gridx = 1;
        cbEvaluator = new JComboBox<>();
        formPanel.add(cbEvaluator, gbc);
        
        // Load evaluators if session exists
        if (!studentSessions.isEmpty()) {
            updateEvaluatorList();
        }

        // 7. File Upload
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Presentation File:"), gbc);
        gbc.gridx = 1;
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        txtFilePath = new JTextField();
        txtFilePath.setEditable(false);
        btnUpload = new JButton("Browse...");
        filePanel.add(txtFilePath, BorderLayout.CENTER);
        filePanel.add(btnUpload, BorderLayout.EAST);
        formPanel.add(filePanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Submit Button
        btnSubmit = new JButton("Submit Registration");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(70, 130, 180));
        btnSubmit.setForeground(Color.WHITE);
        add(btnSubmit, BorderLayout.SOUTH);

        // Action Listeners
        setupListeners();
    }

    private void updateEvaluatorList() {
        if (cbSession == null || studentSessions.isEmpty()) return;
        
        cbEvaluator.removeAllItems();
        
        int selectedIndex = cbSession.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < studentSessions.size()) {
            Session selectedSession = studentSessions.get(selectedIndex);
            
            // Update session info
            lblSessionInfo.setText("Session: " + selectedSession.getVenue() + 
                                 ", " + selectedSession.getStartDateTime() + " to " + 
                                 selectedSession.getEndDateTime());
            
            // Load evaluators from session
            List<String> evaluatorIds = selectedSession.getEvaluatorIds();
            
            if (evaluatorIds.isEmpty()) {
                cbEvaluator.addItem("No evaluators assigned");
                cbEvaluator.setEnabled(false);
            } else {
                cbEvaluator.setEnabled(true);
                for (String evaluatorId : evaluatorIds) {
                    String evaluatorName = getEvaluatorName(evaluatorId);
                    cbEvaluator.addItem(evaluatorName + " (" + evaluatorId + ")");
                }
            }
        }
    }
    
    private String getEvaluatorName(String evaluatorId) {
        // Map evaluator IDs to names
        switch (evaluatorId) {
            case "EVAL001": return "Dr. Smith";
            case "EVAL002": return "Prof. Chen";
            case "EVAL003": return "Dr. Lee";
            case "EVAL004": return "Prof. Kumar";
            case "EVAL005": return "Dr. Johnson";
            default: return "Evaluator " + evaluatorId;
        }
    }

    private void setupListeners() {
        // Handle File Browse
        btnUpload.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                txtFilePath.setText(file.getAbsolutePath());
            }
        });

        // Handle Submit
        btnSubmit.addActionListener(e -> {
            // Validate session selection
            if (studentSessions.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "You are not assigned to any session. Please contact coordinator.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get selected session
            int sessionIndex = cbSession.getSelectedIndex();
            Session selectedSession = studentSessions.get(sessionIndex);
            
            // Get selected evaluator
            String evaluatorSelection = (String) cbEvaluator.getSelectedItem();
            if (evaluatorSelection == null || evaluatorSelection.contains("No evaluators")) {
                JOptionPane.showMessageDialog(this, 
                    "Please select an evaluator from the list.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Extract evaluator ID from selection
            String evaluatorId = extractEvaluatorId(evaluatorSelection);
            
            // Get other form data
            String title = txtTitle.getText();
            String abs = txtAbstract.getText();
            String sup = (String) cbSupervisor.getSelectedItem();
            String type = (String) cbType.getSelectedItem();
            String path = txtFilePath.getText();

            if (title.isEmpty() || abs.isEmpty() || path.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill all required fields.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call Controller with evaluator choice
            boolean success = controller.submitResearch(currentStudent, selectedSession, 
                title, abs, sup, type, path, evaluatorId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Submission Successful!\nEvaluator: " + evaluatorSelection, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                btnSubmit.setEnabled(false);
                // Disable all fields after submission
                txtTitle.setEnabled(false);
                txtAbstract.setEnabled(false);
                cbSupervisor.setEnabled(false);
                cbType.setEnabled(false);
                cbEvaluator.setEnabled(false);
                btnUpload.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error saving data. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private String extractEvaluatorId(String evaluatorSelection) {
        // Extract ID from format: "Dr. Smith (EVAL001)"
        if (evaluatorSelection.contains("(")) {
            int start = evaluatorSelection.indexOf("(") + 1;
            int end = evaluatorSelection.indexOf(")");
            return evaluatorSelection.substring(start, end);
        }
        return evaluatorSelection;
    }
}