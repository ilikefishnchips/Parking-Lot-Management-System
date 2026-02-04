package UI;

import controller.StudentController;
import model.Student;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class StudentPanel extends JPanel {

    private Student currentStudent;
    private StudentController controller;
    
    // UI Components
    private JTextField txtTitle, txtSupervisor, txtFilePath;
    private JTextArea txtAbstract;
    private JComboBox<String> cbType;
    private JButton btnUpload, btnSubmit;

    public StudentPanel(Student student) {
        this.currentStudent = student;
        this.controller = new StudentController();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblHeader = new JLabel("Student Seminar Registration: " + student.getName());
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
        txtTitle = new JTextField(20);
        formPanel.add(txtTitle, gbc);

        // 2. Abstract
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Abstract:"), gbc);
        gbc.gridx = 1;
        txtAbstract = new JTextArea(5, 20);
        txtAbstract.setLineWrap(true);
        JScrollPane scrollAbstract = new JScrollPane(txtAbstract);
        formPanel.add(scrollAbstract, gbc);

        // 3. Supervisor
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Supervisor Name:"), gbc);
        gbc.gridx = 1;
        txtSupervisor = new JTextField(20);
        formPanel.add(txtSupervisor, gbc);

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
            String sup = txtSupervisor.getText();
            String type = (String) cbType.getSelectedItem();
            String path = txtFilePath.getText();

            if (title.isEmpty() || abs.isEmpty() || sup.isEmpty() || path.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call Controller
            boolean success = controller.submitResearch(currentStudent, title, abs, sup, type, path);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Submission Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                btnSubmit.setEnabled(false); // Prevent double submission
            } else {
                JOptionPane.showMessageDialog(this, "Error saving data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}