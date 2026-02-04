package src.Evaluator.ui;

import src.Evaluator.controller.EvaluationController;
import src.Evaluator.model.Evaluator;
import src.common.model.Submission;
import src.common.model.Evaluation;
import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;  // Add this import
import java.io.File;      // Add this import
import java.util.List;
import java.util.UUID;

public class EvaluatorPanel extends JPanel {
    private Evaluator currentEvaluator;
    private EvaluationController controller;
    private List<Submission> assignedSubmissions;
    
    // UI Components
    private JComboBox<Submission> submissionCombo;
    private JSlider problemSlider, methodSlider, resultsSlider, presentationSlider;
    private JLabel problemLabel, methodLabel, resultsLabel, presentationLabel, totalLabel;
    private JTextArea commentsArea;
    private JButton btnSubmit;
    private JButton btnOpenFile;  // Added button
    
    public EvaluatorPanel(Evaluator evaluator) {
        this.currentEvaluator = evaluator;
        this.controller = new EvaluationController();
        this.assignedSubmissions = controller.getAssignedSubmissions(evaluator.getId());
        
        initUI();
        setupListeners();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel header = new JLabel("Evaluator Dashboard: " + currentEvaluator.getName());
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        add(header, BorderLayout.NORTH);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Assigned Submissions
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Assigned Submissions:"), gbc);
        gbc.gridx = 1;
        submissionCombo = new JComboBox<>(assignedSubmissions.toArray(new Submission[0]));
        submissionCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Submission) {
                    Submission sub = (Submission) value;
                    setText(sub.getTitle() + " (Student: " + sub.getStudentId() + ")");
                }
                return this;
            }
        });
        mainPanel.add(submissionCombo, gbc);
        
        // ============ ADDED: OPEN FILE BUTTON ============
        gbc.gridx = 2;
        btnOpenFile = new JButton("📂 Open File");
        btnOpenFile.setToolTipText("Open the full presentation file");
        mainPanel.add(btnOpenFile, gbc);
        // ============ END OF ADDED CODE ============
        
        // Reset grid position for next row
        gbc.gridx = 0; gbc.gridy = 1;
        
        // Problem Clarity
        mainPanel.add(new JLabel("Problem Clarity:"), gbc);
        gbc.gridx = 1;
        JPanel problemPanel = new JPanel(new BorderLayout());
        problemSlider = createSlider(1, 5, 3);
        problemLabel = new JLabel("3");
        problemPanel.add(problemSlider, BorderLayout.CENTER);
        problemPanel.add(problemLabel, BorderLayout.EAST);
        mainPanel.add(problemPanel, gbc);
        
        // Methodology
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Methodology:"), gbc);
        gbc.gridx = 1;
        JPanel methodPanel = new JPanel(new BorderLayout());
        methodSlider = createSlider(1, 5, 3);
        methodLabel = new JLabel("3");
        methodPanel.add(methodSlider, BorderLayout.CENTER);
        methodPanel.add(methodLabel, BorderLayout.EAST);
        mainPanel.add(methodPanel, gbc);
        
        // Results
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Results:"), gbc);
        gbc.gridx = 1;
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsSlider = createSlider(1, 5, 3);
        resultsLabel = new JLabel("3");
        resultsPanel.add(resultsSlider, BorderLayout.CENTER);
        resultsPanel.add(resultsLabel, BorderLayout.EAST);
        mainPanel.add(resultsPanel, gbc);
        
        // Presentation
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Presentation:"), gbc);
        gbc.gridx = 1;
        JPanel presentationPanel = new JPanel(new BorderLayout());
        presentationSlider = createSlider(1, 5, 3);
        presentationLabel = new JLabel("3");
        presentationPanel.add(presentationSlider, BorderLayout.CENTER);
        presentationPanel.add(presentationLabel, BorderLayout.EAST);
        mainPanel.add(presentationPanel, gbc);
        
        // Total Score
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Total Score:"), gbc);
        gbc.gridx = 1;
        totalLabel = new JLabel("12.0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(totalLabel, gbc);
        
        // Comments
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Comments:"), gbc);
        gbc.gridx = 1;
        commentsArea = new JTextArea(5, 30);
        commentsArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(commentsArea);
        mainPanel.add(scrollPane, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Submit Button
        btnSubmit = new JButton("Submit Evaluation");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(70, 130, 180));
        btnSubmit.setForeground(Color.WHITE);
        add(btnSubmit, BorderLayout.SOUTH);
    }
    
    private JSlider createSlider(int min, int max, int initial) {
        JSlider slider = new JSlider(min, max, initial);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }
    
    private void setupListeners() {
        // Update labels when sliders change
        problemSlider.addChangeListener(e -> {
            problemLabel.setText(String.valueOf(problemSlider.getValue()));
            updateTotal();
        });
        
        methodSlider.addChangeListener(e -> {
            methodLabel.setText(String.valueOf(methodSlider.getValue()));
            updateTotal();
        });
        
        resultsSlider.addChangeListener(e -> {
            resultsLabel.setText(String.valueOf(resultsSlider.getValue()));
            updateTotal();
        });
        
        presentationSlider.addChangeListener(e -> {
            presentationLabel.setText(String.valueOf(presentationSlider.getValue()));
            updateTotal();
        });
        
        // ============ ADDED: FILE OPENING LISTENER ============
        btnOpenFile.addActionListener(e -> {
            Submission selected = (Submission) submissionCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a submission first.");
                return;
            }
            
            try {
                // Get file path - need to add getPresentationFilePath() to Submission class
                String filePath = selected.getPresentationFilePath();
                if (filePath == null || filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No file path available for this submission.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                File file = new File(filePath);
                if (file.exists()) {
                    // Ask for confirmation
                    int choice = JOptionPane.showConfirmDialog(this,
                        "Open file:\n" + file.getName() + "\n\nLocated at:\n" + file.getParent(),
                        "Open Presentation File",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        Desktop.getDesktop().open(file);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "File not found:\n" + file.getPath(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot open file: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        // ============ END OF ADDED CODE ============
        
        // Submit evaluation
        btnSubmit.addActionListener(e -> submitEvaluation());
    }
    
    private void updateTotal() {
        int total = problemSlider.getValue() + methodSlider.getValue() + 
                   resultsSlider.getValue() + presentationSlider.getValue();
        totalLabel.setText(String.valueOf(total));
    }
    
    private void submitEvaluation() {
        Submission selected = (Submission) submissionCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a submission to evaluate.");
            return;
        }
        
        // Check if already evaluated
        if (controller.hasEvaluated(selected.getStudentId(), currentEvaluator.getId())) {
            JOptionPane.showMessageDialog(this, 
                "You have already evaluated this submission.", 
                "Already Evaluated", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create evaluation
        String evalId = UUID.randomUUID().toString().substring(0, 8);
        Evaluation evaluation = new Evaluation(
            evalId,
            selected.getStudentId(),
            currentEvaluator.getId(),
            problemSlider.getValue(),
            methodSlider.getValue(),
            resultsSlider.getValue(),
            presentationSlider.getValue(),
            commentsArea.getText()
        );
        
        // Save evaluation
        boolean success = controller.saveEvaluation(evaluation);
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Evaluation submitted successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            btnSubmit.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error submitting evaluation.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}