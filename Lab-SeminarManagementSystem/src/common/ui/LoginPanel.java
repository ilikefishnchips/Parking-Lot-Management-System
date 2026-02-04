package src.common.ui;

import src.Student.model.Student;
import src.Student.ui.StudentPanel;
import src.Evaluator.model.Evaluator;
import src.Evaluator.ui.EvaluatorPanel;
import src.Coordinator.ui.CoordinatorPanel;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LoginPanel extends JPanel {
    private JFrame parentFrame;
    private Map<String, UserData> userDatabase;
    
    // User data class
    private class UserData {
        String id;
        String name;
        String password;
        String role;
        String additionalInfo;
        
        UserData(String id, String name, String password, String role, String additionalInfo) {
            this.id = id;
            this.name = name;
            this.password = password;
            this.role = role;
            this.additionalInfo = additionalInfo;
        }
    }
    
    public LoginPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUserDatabase();
        initUI();
    }
    
    private void initializeUserDatabase() {
        userDatabase = new HashMap<>();
        
        // Students
        userDatabase.put("student1", new UserData("STU001", "John Doe", "student123", "Student", "Computer Science"));
        userDatabase.put("student2", new UserData("STU002", "Jane Smith", "student456", "Student", "Data Science"));
        userDatabase.put("student3", new UserData("STU003", "Bob Johnson", "student789", "Student", "AI Research"));
        
        // Evaluators
        userDatabase.put("evaluator1", new UserData("EVAL001", "Dr. Smith", "eval123", "Evaluator", "Senior Lecturer"));
        userDatabase.put("evaluator2", new UserData("EVAL002", "Prof. Chen", "eval456", "Evaluator", "Department Head"));
        
        // Coordinator
        userDatabase.put("admin", new UserData("COORD001", "Dr. Lee", "admin123", "Coordinator", "Seminar Coordinator"));
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
        
        // Login Form
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
        JComboBox<String> usernameCombo = new JComboBox<>(new String[]{"student1", "student2", "student3", "evaluator1", "evaluator2", "admin"});
        formPanel.add(usernameCombo, fgbc);
        
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
        
        // Auto-fill password when username selected
        usernameCombo.addActionListener(e -> {
            String username = (String) usernameCombo.getSelectedItem();
            if (userDatabase.containsKey(username)) {
                passwordField.setText(userDatabase.get(username).password);
            }
        });
        
        // Login action
        loginButton.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            String username = (String) usernameCombo.getSelectedItem();
            String password = new String(passwordField.getPassword());
            
            UserData user = userDatabase.get(username);
            if (user != null && user.password.equals(password) && user.role.equals(role)) {
                switchToPanel(user);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Login failed. Please check credentials.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Add form to main panel
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(formPanel, gbc);
    }
    
    private void switchToPanel(UserData user) {
        parentFrame.getContentPane().removeAll();
        
        switch (user.role) {
            case "Student":
                Student student = new Student(user.id, user.name, user.password, user.name);
                parentFrame.add(new StudentPanel(student));
                parentFrame.setTitle("Student: " + user.name);
                break;
                
            case "Evaluator":
                Evaluator evaluator = new Evaluator(user.id, user.name, user.password, 
                                                    user.name, user.additionalInfo);
                parentFrame.add(new EvaluatorPanel(evaluator));
                parentFrame.setTitle("Evaluator: " + user.name);
                break;
                
            case "Coordinator":
                parentFrame.add(new CoordinatorPanel());
                parentFrame.setTitle("Coordinator Dashboard");
                break;
        }
        
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}