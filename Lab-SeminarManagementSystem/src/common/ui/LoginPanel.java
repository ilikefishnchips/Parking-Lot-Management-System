package src.common.ui;

import src.Student.model.Student;
import src.Evaluator.model.Evaluator;
import src.Coordinator.model.Coordinator;
import src.Student.UI.StudentPanel;
import src.Evaluator.ui.EvaluatorPanel;
import src.Coordinator.ui.CoordinatorPanel;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JFrame parentFrame;
    
    public LoginPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initUI();
    }
    
    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel title = new JLabel("Seminar Management System");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);
        
        // Subtitle
        JLabel subtitle = new JLabel("Faculty of Computing and Informatics");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        add(subtitle, gbc);
        
        // Login Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 5, 5, 5);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Role Selection
        fgbc.gridx = 0; fgbc.gridy = row;
        formPanel.add(new JLabel("Role:"), fgbc);
        fgbc.gridx = 1;
        String[] roles = {"Student", "Evaluator", "Coordinator"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        formPanel.add(roleCombo, fgbc);
        
        // Username
        fgbc.gridx = 0; fgbc.gridy = ++row;
        formPanel.add(new JLabel("Username:"), fgbc);
        fgbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        formPanel.add(usernameField, fgbc);
        
        // Password
        fgbc.gridx = 0; fgbc.gridy = ++row;
        formPanel.add(new JLabel("Password:"), fgbc);
        fgbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        formPanel.add(passwordField, fgbc);
        
        // Login Button
        fgbc.gridx = 0; fgbc.gridy = ++row;
        fgbc.gridwidth = 2;
        fgbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        formPanel.add(loginButton, fgbc);
        
        // Demo credentials
        JLabel demoLabel = new JLabel("Demo: student/student123, evaluator/evaluator123, admin/admin123");
        demoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        demoLabel.setForeground(Color.GRAY);
        fgbc.gridy = ++row;
        formPanel.add(demoLabel, fgbc);
        
        // Add form to main panel
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(formPanel, gbc);
        
        // Action listener for login button
        loginButton.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            // Simple authentication (for demo purposes)
            boolean authenticated = authenticate(role, username, password);
            
            if (authenticated) {
                switchToPanel(role, username);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Login failed. Try:\n" +
                    "Student: student/student123\n" +
                    "Evaluator: evaluator/evaluator123\n" +
                    "Coordinator: admin/admin123",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private boolean authenticate(String role, String username, String password) {
        // Simple hardcoded authentication for demo
        switch (role) {
            case "Student":
                return username.equals("student") && password.equals("student123");
            case "Evaluator":
                return username.equals("evaluator") && password.equals("evaluator123");
            case "Coordinator":
                return username.equals("admin") && password.equals("admin123");
            default:
                return false;
        }
    }
    
    private void switchToPanel(String role, String username) {
        parentFrame.getContentPane().removeAll();
        
        switch (role) {
            case "Student":
                Student student = new Student("STU001", username, "student123", "John Doe");
                parentFrame.add(new StudentPanel(student));
                parentFrame.setTitle("Student Dashboard - " + student.getName());
                break;
                
            case "Evaluator":
                Evaluator evaluator = new Evaluator("EVAL001", username, "evaluator123", 
                                                    "Dr. Smith", "Computing Department");
                parentFrame.add(new EvaluatorPanel(evaluator));
                parentFrame.setTitle("Evaluator Dashboard - " + evaluator.getName());
                break;
                
            case "Coordinator":
                Coordinator coordinator = new Coordinator("COORD001", username, "admin123",
                                                         "Admin", "admin@fci.edu");
                parentFrame.add(new CoordinatorPanel());
                parentFrame.setTitle("Coordinator Dashboard");
                break;
        }
        
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}

