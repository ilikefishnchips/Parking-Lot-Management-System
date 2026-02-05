package src.Student.ui;

import java.awt.*;
import java.io.File;
import javax.swing.*;
import src.Student.controller.StudentController;
import src.Student.model.Student;

public class StudentPanel extends JPanel {
    private final Student currentStudent;
    private final StudentController controller;
    
    // UI Components
    private JTextField txtTitle, txtFilePath;
    private JTextArea txtAbstract;
    private JComboBox<String> cbSupervisor;
    private JComboBox<String> cbType;
    private JButton btnUpload, btnSubmit;
    
    // Labels for assigned session and evaluator (READ-ONLY)
    private JLabel lblAssignedSession, lblAssignedEvaluator;
    private JLabel lblSubmissionStatus;
    
    // Supervisor options
    private final String[] supervisors = {"Dr. Smith", "Prof. Chen", "Dr. Lee", "Prof. Kumar", "Dr. Johnson"};
    
    public StudentPanel(Student student) {
        this.currentStudent = student;
        this.controller = new StudentController();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblHeader = new JLabel("Student Seminar Registration: " + student.getName() + " (" + student.getStudentId() + ")");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblHeader, BorderLayout.NORTH);

        // Main content panel with scroll
        JPanel contentPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Status Panel (top)
        JPanel statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Your Assignment Status"));
        GridBagConstraints gbcStatus = new GridBagConstraints();
        gbcStatus.insets = new Insets(5, 5, 5, 5);
        gbcStatus.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Assigned Session (READ-ONLY, set by Coordinator)
        gbcStatus.gridx = 0; gbcStatus.gridy = row;
        statusPanel.add(new JLabel("Assigned Session:"), gbcStatus);
        gbcStatus.gridx = 1;
        lblAssignedSession = new JLabel("Not assigned yet");
        lblAssignedSession.setForeground(Color.RED);
        statusPanel.add(lblAssignedSession, gbcStatus);
        row++;
        
        // Assigned Evaluator (READ-ONLY, set by Coordinator/System)
        gbcStatus.gridx = 0; gbcStatus.gridy = row;
        statusPanel.add(new JLabel("Assigned Evaluator:"), gbcStatus);
        gbcStatus.gridx = 1;
        lblAssignedEvaluator = new JLabel("Not assigned yet");
        lblAssignedEvaluator.setForeground(Color.RED);
        statusPanel.add(lblAssignedEvaluator, gbcStatus);
        row++;
        
        // Submission Status
        gbcStatus.gridx = 0; gbcStatus.gridy = row;
        statusPanel.add(new JLabel("Submission Status:"), gbcStatus);
        gbcStatus.gridx = 1;
        lblSubmissionStatus = new JLabel("Not submitted");
        lblSubmissionStatus.setForeground(Color.ORANGE);
        statusPanel.add(lblSubmissionStatus, gbcStatus);
        
        contentPanel.add(statusPanel, BorderLayout.NORTH);
        
        // Check if student already submitted
        if (controller.hasStudentSubmitted(student.getStudentId())) {
            lblSubmissionStatus.setText("Submitted ✓");
            lblSubmissionStatus.setForeground(Color.GREEN);
        }
        
        // Load assignment information
        loadAssignmentInfo();

        // Form Panel for registration
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Registration Form"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        row = 0;
        
        // 1. Research Title
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Research Title*:"), gbc);
        gbc.gridx = 1;
        txtTitle = new JTextField(25);
        formPanel.add(txtTitle, gbc);
        row++;

        // 2. Abstract
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Abstract*:"), gbc);
        gbc.gridx = 1;
        txtAbstract = new JTextArea(5, 25);
        txtAbstract.setLineWrap(true);
        JScrollPane scrollAbstract = new JScrollPane(txtAbstract);
        formPanel.add(scrollAbstract, gbc);
        row++;

        // 3. Supervisor
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Supervisor*:"), gbc);
        gbc.gridx = 1;
        cbSupervisor = new JComboBox<>(supervisors);
        formPanel.add(cbSupervisor, gbc);
        row++;

        // 4. Presentation Type (PREFERENCE, not assignment)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Preferred Presentation Type*:"), gbc);
        gbc.gridx = 1;
        String[] types = {"Oral Presentation", "Poster Presentation"};
        cbType = new JComboBox<>(types);
        formPanel.add(cbType, gbc);
        row++;

        // 5. File Upload
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Presentation File*:"), gbc);
        gbc.gridx = 1;
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        txtFilePath = new JTextField();
        txtFilePath.setEditable(false);
        btnUpload = new JButton("Browse...");
        filePanel.add(txtFilePath, BorderLayout.CENTER);
        filePanel.add(btnUpload, BorderLayout.EAST);
        formPanel.add(filePanel, gbc);
        row++;

        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Information Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Important Information"));
        
        JLabel info1 = new JLabel("• You can only submit ONCE. Make sure all information is correct.");
        JLabel info2 = new JLabel("• Coordinator will assign you to a session based on availability.");
        JLabel info3 = new JLabel("• You will be notified when assigned to a session.");
        JLabel info4 = new JLabel("• You cannot choose your evaluator - it will be assigned automatically.");
        
        info1.setForeground(Color.DARK_GRAY);
        info2.setForeground(Color.DARK_GRAY);
        info3.setForeground(Color.DARK_GRAY);
        info4.setForeground(Color.DARK_GRAY);
        
        infoPanel.add(info1);
        infoPanel.add(info2);
        infoPanel.add(info3);
        infoPanel.add(info4);
        
        contentPanel.add(infoPanel, BorderLayout.SOUTH);

        // Submit Button
        btnSubmit = new JButton("Submit Registration");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(70, 130, 180));
        btnSubmit.setForeground(Color.WHITE);
        
        // Disable submit if already submitted
        if (controller.hasStudentSubmitted(student.getStudentId())) {
            btnSubmit.setEnabled(false);
            btnSubmit.setText("Already Submitted");
            btnSubmit.setBackground(Color.GRAY);
            disableFormFields();
        }
        
        add(btnSubmit, BorderLayout.SOUTH);

        // Action Listeners
        setupListeners();
    }

    private void loadAssignmentInfo() {
        // Get assignment information from controller
        var assignment = controller.getStudentAssignment(currentStudent.getStudentId());
        
        if (assignment.containsKey("sessionId") && !assignment.get("sessionId").isEmpty()) {
            lblAssignedSession.setText(assignment.get("sessionId"));
            lblAssignedSession.setForeground(new Color(0, 128, 0)); // Green
        }
        
        if (assignment.containsKey("evaluatorId") && !assignment.get("evaluatorId").isEmpty()) {
            lblAssignedEvaluator.setText(getEvaluatorName(assignment.get("evaluatorId")));
            lblAssignedEvaluator.setForeground(new Color(0, 128, 0)); // Green
        }
        
        if (assignment.containsKey("status")) {
            lblSubmissionStatus.setText(assignment.get("status"));
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
            fileChooser.setDialogTitle("Select Presentation File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            // Set file filters
            javax.swing.filechooser.FileFilter pdfFilter = new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf") 
                           || f.getName().toLowerCase().endsWith(".ppt") 
                           || f.getName().toLowerCase().endsWith(".pptx") 
                           || f.getName().toLowerCase().endsWith(".jpg") 
                           || f.getName().toLowerCase().endsWith(".png");
                }
                
                @Override
                public String getDescription() {
                    return "Presentation Files (PDF, PPT, JPG, PNG)";
                }
            };
            
            fileChooser.setFileFilter(pdfFilter);
            
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                txtFilePath.setText(file.getAbsolutePath());
                
                // Check file size (optional)
                long fileSize = file.length();
                if (fileSize > 50 * 1024 * 1024) { // 50MB limit
                    JOptionPane.showMessageDialog(this, 
                        "File is too large (>50MB). Please compress or use a smaller file.",
                        "File Too Large", JOptionPane.WARNING_MESSAGE);
                    txtFilePath.setText("");
                }
            }
        });

        // Handle Submit
        btnSubmit.addActionListener(e -> {
            String title = txtTitle.getText().trim();
            String abs = txtAbstract.getText().trim();
            String sup = (String) cbSupervisor.getSelectedItem();
            String type = (String) cbType.getSelectedItem();
            String path = txtFilePath.getText().trim();

            // Validation
            StringBuilder errors = new StringBuilder();
            
            if (title.isEmpty()) {
                errors.append("• Research title is required\n");
            }
            
            if (abs.isEmpty()) {
                errors.append("• Abstract is required\n");
            }
            
            if (path.isEmpty()) {
                errors.append("• Presentation file is required\n");
            }
            
            if (errors.length() > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please fix the following errors:\n\n" + errors.toString(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Confirm submission
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to submit?\n\n" +
                "Title: " + title + "\n" +
                "Type: " + type + "\n" +
                "Supervisor: " + sup + "\n\n" +
                "You cannot edit after submission!",
                "Confirm Submission", JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Call Controller (without session/evaluator - student doesn't choose them!)
            boolean success = controller.submitResearch(currentStudent, title, abs, sup, type, path);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Registration Submitted Successfully!\n\n" +
                    "Your registration has been received.\n" +
                    "The coordinator will assign you to a session.\n" +
                    "You will be notified when assigned.\n\n" +
                    "Thank you for your submission!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Update UI
                lblSubmissionStatus.setText("Submitted ✓");
                lblSubmissionStatus.setForeground(Color.GREEN);
                btnSubmit.setEnabled(false);
                btnSubmit.setText("Already Submitted");
                btnSubmit.setBackground(Color.GRAY);
                disableFormFields();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error saving data. Please try again or contact support.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void disableFormFields() {
        txtTitle.setEnabled(false);
        txtAbstract.setEnabled(false);
        cbSupervisor.setEnabled(false);
        cbType.setEnabled(false);
        btnUpload.setEnabled(false);
    }
}