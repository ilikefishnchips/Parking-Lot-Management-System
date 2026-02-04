package src.Coordinator.ui;


import src.Coordinator.controller.SessionController;
import src.Coordinator.model.Session;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CoordinatorPanel extends JPanel {
    private SessionController sessionController;
    
    // UI Components
    private JList<Session> sessionList;
    private DefaultListModel<Session> sessionListModel;
    private JTextField txtDate, txtTime, txtVenue, txtType, txtCapacity;
    private JTextArea txtStudents, txtEvaluators;
    private JButton btnCreate, btnUpdate, btnDelete;
    private JTextArea reportArea;
    
    public CoordinatorPanel() {
        this.sessionController = new SessionController();
        initUI();
        loadSessions();
        setupListeners();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Main split pane
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(400);
        
        // Left panel: Session Management
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Session Management"));
        
        // Session List
        sessionListModel = new DefaultListModel<>();
        sessionList = new JList<>(sessionListModel);
        sessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(sessionList);
        leftPanel.add(listScroll, BorderLayout.CENTER);
        
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
        
        // Students (comma separated IDs)
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Student IDs:"), gbc);
        gbc.gridx = 1;
        txtStudents = new JTextArea(3, 15);
        txtStudents.setLineWrap(true);
        JScrollPane studentScroll = new JScrollPane(txtStudents);
        formPanel.add(studentScroll, gbc);
        
        // Evaluators (comma separated IDs)
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Evaluator IDs:"), gbc);
        gbc.gridx = 1;
        txtEvaluators = new JTextArea(3, 15);
        txtEvaluators.setLineWrap(true);
        JScrollPane evaluatorScroll = new JScrollPane(txtEvaluators);
        formPanel.add(evaluatorScroll, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnCreate = new JButton("Create");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        leftPanel.add(formPanel, BorderLayout.SOUTH);
        
        // Right panel: Reports
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Reports"));
        
        // Report display area
        reportArea = new JTextArea(20, 40);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane reportScroll = new JScrollPane(reportArea);
        
        rightPanel.add(reportScroll, BorderLayout.CENTER);
        
        mainSplit.setLeftComponent(leftPanel);
        mainSplit.setRightComponent(rightPanel);
        
        add(mainSplit, BorderLayout.CENTER);
    }
    
    private void loadSessions() {
        sessionListModel.clear();
        List<Session> sessions = sessionController.getAllSessions();
        for (Session session : sessions) {
            sessionListModel.addElement(session);
        }
    }
    
    private void setupListeners() {
        // Session selection
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
        
        // Create session
        btnCreate.addActionListener(e -> createSession());
        
        // Update session
        btnUpdate.addActionListener(e -> updateSession());
        
        // Delete session
        btnDelete.addActionListener(e -> deleteSession());
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
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for capacity.");
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