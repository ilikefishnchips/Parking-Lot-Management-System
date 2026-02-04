package src.Student.ui;

import src.Student.controller.StudentController;
import src.Student.model.Student;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class StudentPanel extends JPanel {
    private Student currentStudent;
    private StudentController controller;
    
    // UI Components
    private JTextField txtTitle, txtFilePath;
    private JTextArea txtAbstract;
    private JComboBox<String> cbType, cbSupervisor, cbAssignedEvaluator;
    private JButton btnUpload, btnSubmit;
    
    // Supervisor options
    private String[] supervisors = {"Dr. Smith", "Prof. Chen", "Dr. Lee", "Prof. Kumar", "Dr. Johnson"};
    
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

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 1. Research Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Research Title:"), gbc);
        gbc.gridx = 1;
        txtTitle = new JTextField(25);
        formPanel.add(txtTitle, gbc);

        // 2. Abstract
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Abstract:"), gbc);
        gbc.gridx = 1;
        txtAbstract = new JTextArea(5, 25);
        txtAbstract.setLineWrap(true);
        JScrollPane scrollAbstract = new JScrollPane(txtAbstract);
        formPanel.add(scrollAbstract, gbc);

        // 3. Supervisor
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Supervisor:"), gbc);
        gbc.gridx = 1;
        cbSupervisor = new JComboBox<>(supervisors);
        formPanel.add(cbSupervisor, gbc);

        // 4. Presentation Type
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Presentation Type:"), gbc);
        gbc.gridx = 1;
        String[] types = {"Oral Presentation", "Poster Presentation"};
        cbType = new JComboBox<>(types);
        formPanel.add(cbType, gbc);

        // 5. File Upload
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Presentation File:"), gbc);
        gbc.gridx = 1;
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        txtFilePath = new JTextField();
        txtFilePath.setEditable(false);
        btnUpload = new JButton("Browse...");
        filePanel.add(txtFilePath, BorderLayout.CENTER);
        filePanel.add(btnUpload, BorderLayout.EAST);
        formPanel.add(filePanel, gbc);

        // 6. Assigned Evaluator (Read-only, shows coordinator's assignment)
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Assigned Evaluator:"), gbc);
        gbc.gridx = 1;
        cbAssignedEvaluator = new JComboBox<>();
        cbAssignedEvaluator.setEnabled(false);
        loadAssignedEvaluator();
        formPanel.add(cbAssignedEvaluator, gbc);

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

    private void loadAssignedEvaluator() {
        // Load assigned evaluator from assignments file
        File assignmentFile = new File("assignments.txt");
        if (assignmentFile.exists()) {
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(assignmentFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 4 && parts[2].equals(currentStudent.getStudentId())) {
                        String evaluatorId = parts[3];
                        // Map evaluator ID to name
                        String evaluatorName = getEvaluatorName(evaluatorId);
                        cbAssignedEvaluator.addItem(evaluatorName);
                        break;
                    }
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (cbAssignedEvaluator.getItemCount() == 0) {
            cbAssignedEvaluator.addItem("Not assigned yet");
        }
    }
    
    private String getEvaluatorName(String evaluatorId) {
        // Map evaluator IDs to names
        switch (evaluatorId) {
            case "EVAL001": return "Dr. Smith";
            case "EVAL002": return "Prof. Chen";
            default: return evaluatorId;
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
            String title = txtTitle.getText();
            String abs = txtAbstract.getText();
            String sup = (String) cbSupervisor.getSelectedItem();
            String type = (String) cbType.getSelectedItem();
            String path = txtFilePath.getText();

            if (title.isEmpty() || abs.isEmpty() || path.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call Controller
            boolean success = controller.submitResearch(currentStudent, title, abs, sup, type, path);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Submission Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                btnSubmit.setEnabled(false);
                // Disable all fields after submission
                txtTitle.setEnabled(false);
                txtAbstract.setEnabled(false);
                cbSupervisor.setEnabled(false);
                cbType.setEnabled(false);
                btnUpload.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Error saving data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}