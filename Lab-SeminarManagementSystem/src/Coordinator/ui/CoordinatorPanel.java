package src.Coordinator.ui;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import src.Coordinator.controller.BoardController;
import src.Coordinator.controller.EnhancedAnalyticsController;
import src.Coordinator.controller.ReportController;
import src.Coordinator.controller.SessionController;
import src.Coordinator.model.Session;
import src.Student.controller.StudentDataController;
import src.common.model.Award;
import src.common.model.Board;
import src.common.model.Report;
import src.common.model.Submission;

public class CoordinatorPanel extends JPanel {
    private final SessionController sessionController;
    private final ReportController reportController;
    private final BoardController boardController;
    private final EnhancedAnalyticsController analyticsController;
    private final StudentDataController studentDataController;
    
    // Board Management UI Components
    private JTable boardTable;
    private DefaultTableModel boardTableModel;
    private JTextField txtBoardId, txtBoardLocation, txtBoardWidth, txtBoardHeight;
    private JTextArea txtBoardRequirements;
    private JComboBox<String> cbBoardSession;
    
    // Analytics UI Components
    private JTextArea analyticsArea;
    private JButton btnGenerateStats, btnGenerateSummary, btnGenerateCharts;
    private JTabbedPane tabbedPane;
    
    // User lists
    private java.util.List<String> studentList;
    private java.util.List<String> evaluatorList;
    
    // Session Management Tab (UPDATED VARIABLES)
    private JList<Session> sessionList;
    private DefaultListModel<Session> sessionListModel;
    private JTextField txtStartDateTime, txtEndDateTime, txtVenue, txtCapacity;
    private JComboBox<String> cbType;
    private JList<String> studentSelector, evaluatorSelector;
    private JLabel lblConflictWarning;
    
    // Reports Tab
    private JTextArea reportArea;
    private JButton btnGenerateSchedule, btnGenerateEvaluation, btnExportCSV;
    
    // Awards Tab
    private JTextArea awardArea;
    private JButton btnComputeAwards;
    
    public CoordinatorPanel() {
        this.sessionController = new SessionController();
        this.reportController = new ReportController();
        this.boardController = new BoardController();
        this.analyticsController = new EnhancedAnalyticsController();
        this.studentDataController = new StudentDataController();
        initializeUserLists();
        
        initUI();
        loadSessions();
    }
    
    private void initializeUserLists() {
        studentList = Arrays.asList("STU001 - John Doe", "STU002 - Jane Smith", "STU003 - Bob Johnson");
        evaluatorList = Arrays.asList("EVAL001 - Dr. Smith", "EVAL002 - Prof. Chen", "EVAL003 - Dr. Lee");
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        // Tab 1: Session Management
        tabbedPane.addTab("Session Management", createSessionPanel());
        
        // Tab 2: Poster Board Management
        tabbedPane.addTab("Poster Boards", createBoardPanel());
        
        // Tab 3: Reports
        tabbedPane.addTab("Reports", createReportPanel());
        
        // Tab 4: Analytics
        tabbedPane.addTab("Analytics", createAnalyticsPanel());
        
        // Tab 5: Awards
        tabbedPane.addTab("Awards", createAwardPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top: Add Board Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Board ID
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Board ID:"), gbc);
        gbc.gridx = 1;
        txtBoardId = new JTextField(15);
        formPanel.add(txtBoardId, gbc);
        
        // Location
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        txtBoardLocation = new JTextField(15);
        formPanel.add(txtBoardLocation, gbc);
        
        // Session Selection
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Session:"), gbc);
        gbc.gridx = 1;
        cbBoardSession = new JComboBox<>();
        loadSessionsIntoCombo();
        formPanel.add(cbBoardSession, gbc);
        
        // Dimensions
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Width (cm):"), gbc);
        gbc.gridx = 1;
        txtBoardWidth = new JTextField(15);
        formPanel.add(txtBoardWidth, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Height (cm):"), gbc);
        gbc.gridx = 1;
        txtBoardHeight = new JTextField(15);
        formPanel.add(txtBoardHeight, gbc);
        
        // Requirements
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("Requirements:"), gbc);
        gbc.gridx = 1;
        txtBoardRequirements = new JTextArea(3, 15);
        JScrollPane reqScroll = new JScrollPane(txtBoardRequirements);
        formPanel.add(reqScroll, gbc);
        
        // Buttons - UPDATED TO INCLUDE POSTER ASSIGNMENT BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAddBoard = new JButton("Add Board");
        JButton btnGenerateLayout = new JButton("Generate Layout Report");
        JButton btnAssignPoster = new JButton("Assign Poster");
        JButton btnUnassignPoster = new JButton("Unassign Poster");
        
        btnAddBoard.addActionListener(e -> addNewBoard());
        btnGenerateLayout.addActionListener(e -> generateBoardLayoutReport());
        btnAssignPoster.addActionListener(e -> assignPosterToBoard());
        btnUnassignPoster.addActionListener(e -> unassignPosterFromBoard());
        
        buttonPanel.add(btnAddBoard);
        buttonPanel.add(btnGenerateLayout);
        buttonPanel.add(btnAssignPoster);
        buttonPanel.add(btnUnassignPoster);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Bottom: Board Table
        String[] columnNames = {"Board ID", "Location", "Status", "Poster ID", "Size", "Requirements"};
        boardTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        boardTable = new JTable(boardTableModel);
        boardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(boardTable);
        panel.add(tableScroll, BorderLayout.CENTER);
        
        // Load existing boards
        loadBoardsIntoTable();
        
        return panel;
    }
    
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top: Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        btnGenerateStats = new JButton("Generate Statistics");
        btnGenerateSummary = new JButton("Generate Executive Summary");
        btnGenerateCharts = new JButton("Generate Charts");
        JButton btnExportAnalytics = new JButton("Export Analytics");
        
        buttonPanel.add(btnGenerateStats);
        buttonPanel.add(btnGenerateSummary);
        buttonPanel.add(btnGenerateCharts);
        buttonPanel.add(btnExportAnalytics);
        
        // Analytics display area
        analyticsArea = new JTextArea(20, 60);
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane analyticsScroll = new JScrollPane(analyticsArea);
        
        // Add listeners
        btnGenerateStats.addActionListener(e -> generateComprehensiveStats());
        btnGenerateSummary.addActionListener(e -> generateExecutiveSummary());
        btnGenerateCharts.addActionListener(e -> generateChartVisualization());
        btnExportAnalytics.addActionListener(e -> exportAnalyticsData());
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(analyticsScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ============ ANALYTICS METHODS ============
    private void generateComprehensiveStats() {
        Map<String, Object> stats = analyticsController.getComprehensiveStatistics();
        
        if (stats.containsKey("message")) {
            analyticsArea.setText((String) stats.get("message"));
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPREHENSIVE ANALYTICS ===\n");
        sb.append("Generated: ").append(new Date()).append("\n\n");
        
        // Basic statistics
        sb.append("BASIC STATISTICS:\n");
        sb.append("Total Submissions: ").append(stats.get("totalSubmissions")).append("\n");
        sb.append("Oral Presentations: ").append(stats.get("oralSubmissions")).append("\n");
        sb.append("Poster Presentations: ").append(stats.get("posterSubmissions")).append("\n");
        sb.append("Total Evaluations: ").append(stats.get("totalEvaluations")).append("\n\n");
        
        // Score analysis
        sb.append("SCORE ANALYSIS:\n");
        sb.append("Overall Average: ").append(stats.get("avgOverall")).append("/5.0\n");
        sb.append("Standard Deviation: ").append(stats.get("stdDevOverall")).append("\n");
        sb.append("Median: ").append(stats.get("medianScore")).append("\n");
        sb.append("Range: ").append(stats.get("scoreRange")).append("\n\n");
        
        // Criteria breakdown
        sb.append("CRITERIA AVERAGES:\n");
        sb.append("Problem Clarity: ").append(stats.get("avgProblem")).append("/5.0\n");
        sb.append("Methodology: ").append(stats.get("avgMethod")).append("/5.0\n");
        sb.append("Results: ").append(stats.get("avgResults")).append("/5.0\n");
        sb.append("Presentation: ").append(stats.get("avgPresentation")).append("/5.0\n\n");
        
        // Evaluator analysis
        Map<?, ?> evaluatorAnalysis = (Map<?, ?>) stats.get("evaluatorAnalysis");
        if (evaluatorAnalysis != null && !evaluatorAnalysis.isEmpty()) {
            sb.append("EVALUATOR CONSISTENCY ANALYSIS:\n");
            for (Map.Entry<?, ?> entry : evaluatorAnalysis.entrySet()) {
                Map<?, ?> evalStats = (Map<?, ?>) entry.getValue();
                sb.append(String.format("Evaluator %s: Avg=%.2f, StdDev=%.2f, Consistency=%s, Evaluations=%d\n",
                    entry.getKey(),
                    evalStats.get("average"),
                    evalStats.get("stdDev"),
                    evalStats.get("consistency"),
                    evalStats.get("numEvaluations")));
            }
            sb.append("\n");
        }
        
        // Supervisor analysis
        Map<?, ?> supervisorAnalysis = (Map<?, ?>) stats.get("supervisorAnalysis");
        if (supervisorAnalysis != null && !supervisorAnalysis.isEmpty()) {
            sb.append("SUPERVISOR PERFORMANCE:\n");
            for (Map.Entry<?, ?> entry : supervisorAnalysis.entrySet()) {
                Map<?, ?> supStats = (Map<?, ?>) entry.getValue();
                sb.append(String.format("%s: %d students, Avg Score=%.2f, Evaluated=%d/%d\n",
                    entry.getKey(),
                    supStats.get("numStudents"),
                    supStats.get("avgScore"),
                    supStats.get("totalEvaluated"),
                    supStats.get("numStudents")));
            }
        }
        
        analyticsArea.setText(sb.toString());
    }
    
    private void generateExecutiveSummary() {
        String summary = analyticsController.generateExecutiveSummary();
        analyticsArea.setText(summary);
    }
    
    private void generateChartVisualization() {
        Map<String, Object> chartData = analyticsController.getChartData();
        
        if (chartData.isEmpty()) {
            analyticsArea.setText("No data available for chart generation.");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== CHART DATA (Visualization Ready) ===\n\n");
        
        // Score Distribution
        Map<?, ?> scoreDist = (Map<?, ?>) chartData.get("scoreDistribution");
        if (scoreDist != null) {
            sb.append("SCORE DISTRIBUTION (Histogram Data):\n");
            sb.append("Score | Count\n");
            sb.append("-------------\n");
            for (Map.Entry<?, ?> entry : scoreDist.entrySet()) {
                sb.append(String.format("%5s | %3d\n", entry.getKey(), entry.getValue()));
            }
            sb.append("\n");
        }
        
        // Criteria Averages
        Map<?, ?> criteriaAvgs = (Map<?, ?>) chartData.get("criteriaAverages");
        if (criteriaAvgs != null) {
            sb.append("CRITERIA AVERAGES (Bar Chart Data):\n");
            sb.append("Criteria          | Average Score\n");
            sb.append("----------------------------------\n");
            for (Map.Entry<?, ?> entry : criteriaAvgs.entrySet()) {
                sb.append(String.format("%-18s | %.2f/5.0\n", entry.getKey(), entry.getValue()));
            }
        }
        
        analyticsArea.setText(sb.toString());
    }
    
    private void exportAnalyticsData() {
        String currentAnalytics = analyticsArea.getText();
        if (currentAnalytics.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Generate analytics first.");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("analytics_report.txt"));
        int option = fileChooser.showSaveDialog(this);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.write(currentAnalytics);
                JOptionPane.showMessageDialog(this, "Analytics exported successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting analytics: " + e.getMessage());
            }
        }
    }
    
    // ============ BOARD MANAGEMENT METHODS ============
    private void loadSessionsIntoCombo() {
        cbBoardSession.removeAllItems();
        java.util.List<Session> sessions = sessionController.getAllSessions();
        for (Session session : sessions) {
            cbBoardSession.addItem(session.getSessionId() + ": " + session.getStartDateTime());
        }
    }
    
    private void loadBoardsIntoTable() {
        boardTableModel.setRowCount(0);
        java.util.List<Board> boards = boardController.getAllBoards();
        
        for (Board board : boards) {
            String size = board.getWidth() + "x" + board.getHeight() + "cm";
            String posterId = board.getPosterId() != null ? board.getPosterId() : "N/A";
            
            boardTableModel.addRow(new Object[]{
                board.getBoardId(),
                board.getLocation(),
                board.getStatus(),
                posterId,  // This will show "N/A" instead of "NONE"
                size,
                board.getSpecialRequirements()
            });
        }
        
        System.out.println("Loaded " + boards.size() + " boards into table");
    }
    
    private void addNewBoard() {
        try {
            String boardId = txtBoardId.getText().trim();
            String location = txtBoardLocation.getText().trim();
            String sessionStr = (String) cbBoardSession.getSelectedItem();
            String sessionId = sessionStr != null ? sessionStr.split(":")[0].trim() : "";
            int width = Integer.parseInt(txtBoardWidth.getText().trim());
            int height = Integer.parseInt(txtBoardHeight.getText().trim());
            String requirements = txtBoardRequirements.getText().trim();
            
            if (boardId.isEmpty() || location.isEmpty() || sessionId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields.");
                return;
            }
            
            Board board = new Board(boardId, location, sessionId, width, height, requirements);
            
            if (boardController.createBoard(board)) {
                JOptionPane.showMessageDialog(this, "Board added successfully!");
                loadBoardsIntoTable();
                clearBoardForm();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding board.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid dimensions.");
        }
    }
    
    private void generateBoardLayoutReport() {
        String sessionStr = (String) cbBoardSession.getSelectedItem();
        if (sessionStr == null || sessionStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a session first.");
            return;
        }
        
        String sessionId = sessionStr.split(":")[0].trim();
        String layoutReport = boardController.generateBoardLayoutReport(sessionId);
        
        // Show in a dialog
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setText(layoutReport);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Poster Board Layout - " + sessionId, JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearBoardForm() {
        txtBoardId.setText("");
        txtBoardLocation.setText("");
        txtBoardWidth.setText("");
        txtBoardHeight.setText("");
        txtBoardRequirements.setText("");
    }

    // POSTER ASSIGNMENT METHODS
    private void assignPosterToBoard() {
        // Get selected board from table
        int selectedRow = boardTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a board from the table.");
            return;
        }
        
        String boardId = (String) boardTableModel.getValueAt(selectedRow, 0);
        String sessionStr = (String) cbBoardSession.getSelectedItem();
        
        if (sessionStr == null || sessionStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a session first.");
            return;
        }
        
        String sessionId = sessionStr.split(":")[0].trim();
        
        // Get available poster submissions for this session
        List<Submission> posterSubmissions = boardController.getPosterSubmissionsForSession(sessionId);
        
        if (posterSubmissions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No poster submissions available for this session.");
            return;
        }
        
        // Create dialog for poster selection
        JDialog assignDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Assign Poster to Board", true);
        assignDialog.setLayout(new BorderLayout());
        assignDialog.setSize(400, 300);
        assignDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel for poster selection
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Poster"));
        
        String[] columnNames = {"Submission ID", "Title", "Student ID"};
        DefaultTableModel posterTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable posterTable = new JTable(posterTableModel);
        posterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        for (Submission submission : posterSubmissions) {
            posterTableModel.addRow(new Object[]{
                submission.getSubmissionId(),
                submission.getTitle(),
                submission.getStudentId()
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(posterTable);
        selectionPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAssign = new JButton("Assign");
        JButton btnCancel = new JButton("Cancel");
        
        btnAssign.addActionListener(e -> {
            int selectedPosterRow = posterTable.getSelectedRow();
            if (selectedPosterRow == -1) {
                JOptionPane.showMessageDialog(assignDialog, "Please select a poster.");
                return;
            }
            
            String submissionId = (String) posterTableModel.getValueAt(selectedPosterRow, 0);
            String title = (String) posterTableModel.getValueAt(selectedPosterRow, 1);
            
            // Confirm assignment
            int confirm = JOptionPane.showConfirmDialog(assignDialog,
                "Assign poster:\n" +
                "Title: " + title + "\n" +
                "To board: " + boardId + "\n\n" +
                "Continue?",
                "Confirm Assignment", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Update board assignment
                if (boardController.assignPosterToBoard(submissionId, boardId) &&
                    boardController.updateSubmissionWithBoard(submissionId, boardId)) {
                    JOptionPane.showMessageDialog(assignDialog, "Poster assigned successfully!");
                    assignDialog.dispose();
                    loadBoardsIntoTable(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(assignDialog, "Error assigning poster.");
                }
            }
        });
        
        btnCancel.addActionListener(e -> assignDialog.dispose());
        
        buttonPanel.add(btnAssign);
        buttonPanel.add(btnCancel);
        
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        assignDialog.add(mainPanel);
        assignDialog.setVisible(true);
    }

    private void unassignPosterFromBoard() {
        int selectedRow = boardTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a board from the table.");
            return;
        }
        
        String boardId = (String) boardTableModel.getValueAt(selectedRow, 0);
        String posterId = (String) boardTableModel.getValueAt(selectedRow, 3);
        
        if (posterId.equals("N/A")) {
            JOptionPane.showMessageDialog(this, "This board doesn't have a poster assigned.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove poster assignment from board " + boardId + "?\n" +
            "Poster ID: " + posterId,
            "Confirm Unassignment", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (boardController.unassignPosterFromBoard(boardId) &&
                boardController.updateSubmissionWithBoard(posterId, "NONE")) {
                JOptionPane.showMessageDialog(this, "Poster unassigned successfully!");
                loadBoardsIntoTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error unassigning poster.");
            }
        }
    }
    
    // ============ SESSION MANAGEMENT METHODS ============
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
        
        // Start Date/Time
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Start Date/Time (YYYY-MM-DD HH:MM):"), gbc);
        gbc.gridx = 1;
        txtStartDateTime = new JTextField(15);
        formPanel.add(txtStartDateTime, gbc);
        gbc.gridx = 2;
        JButton btnNowStart = new JButton("Now");
        btnNowStart.addActionListener(e -> txtStartDateTime.setText(
            new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
        formPanel.add(btnNowStart, gbc);
        
        // End Date/Time
        gbc.gridx = 0; gbc.gridy = ++row;
        formPanel.add(new JLabel("End Date/Time (YYYY-MM-DD HH:MM):"), gbc);
        gbc.gridx = 1;
        txtEndDateTime = new JTextField(15);
        formPanel.add(txtEndDateTime, gbc);
        gbc.gridx = 2;
        JButton btnNowEnd = new JButton("Now + 2h");
        btnNowEnd.addActionListener(e -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 2);
            txtEndDateTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cal.getTime()));
        });
        formPanel.add(btnNowEnd, gbc);
        
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
        formPanel.add(new JLabel("Select Evaluators (Multiple):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        evaluatorSelector = new JList<>(evaluatorList.toArray(new String[0]));
        evaluatorSelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane evaluatorScroll = new JScrollPane(evaluatorSelector);
        evaluatorScroll.setPreferredSize(new Dimension(200, 100));
        formPanel.add(evaluatorScroll, gbc);
        
        // Conflict Check Label
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 3;
        lblConflictWarning = new JLabel("");
        lblConflictWarning.setForeground(Color.RED);
        lblConflictWarning.setVisible(false);
        formPanel.add(lblConflictWarning, gbc);
        
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
        
        // Add listeners for real-time conflict checking
        txtStartDateTime.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                checkEvaluatorConflicts();
            }
        });
        
        txtEndDateTime.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                checkEvaluatorConflicts();
            }
        });
        
        evaluatorSelector.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                checkEvaluatorConflicts();
            }
        });
        
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
    
    private void checkEvaluatorConflicts() {
        lblConflictWarning.setText("");
        lblConflictWarning.setVisible(false);
        
        String startDateTime = txtStartDateTime.getText();
        String endDateTime = txtEndDateTime.getText();
        
        if (startDateTime.isEmpty() || endDateTime.isEmpty()) {
            return;
        }
        
        List<String> selectedEvaluators = evaluatorSelector.getSelectedValuesList();
        StringBuilder conflictMessage = new StringBuilder();
        
        for (String evaluatorItem : selectedEvaluators) {
            String evaluatorId = evaluatorItem.split(" - ")[0];
            
            if (!sessionController.isEvaluatorAvailable(evaluatorId, startDateTime, endDateTime)) {
                // Get conflicting sessions
                List<Session> evaluatorSessions = sessionController.getSessionsByEvaluator(evaluatorId);
                for (Session session : evaluatorSessions) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date newStart = sdf.parse(startDateTime);
                        Date newEnd = sdf.parse(endDateTime);
                        Date existingStart = sdf.parse(session.getStartDateTime());
                        Date existingEnd = sdf.parse(session.getEndDateTime());
                        
                        if (newStart.before(existingEnd) && newEnd.after(existingStart)) {
                            conflictMessage.append(evaluatorItem)
                                .append(" is busy in session ").append(session.getSessionId())
                                .append(" (").append(session.getStartDateTime()).append(" - ")
                                .append(session.getEndDateTime()).append(")\n");
                        }
                    } catch (ParseException e) {
                        // Ignore parse errors
                    }
                }
            }
        }
        
        if (conflictMessage.length() > 0) {
            lblConflictWarning.setText("<html><font color='red'>Conflicts detected:</font><br>" + 
                                      conflictMessage.toString() + "</html>");
            lblConflictWarning.setVisible(true);
        }
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
                setText(String.format("%s: %s to %s (%s)", 
                    session.getSessionId(),
                    session.getStartDateTime(),
                    session.getEndDateTime(),
                    session.getSessionType()));
            }
            return this;
        }
    }
    
    private void loadSessions() {
        sessionListModel.clear();
        java.util.List<Session> sessions = sessionController.getAllSessions();
        for (Session session : sessions) {
            sessionListModel.addElement(session);
        }
    }
    
    private void loadSessionData(Session session) {
        txtStartDateTime.setText(session.getStartDateTime());
        txtEndDateTime.setText(session.getEndDateTime());
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
        java.util.List<String> selectedStudents = new ArrayList<>();
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
        java.util.List<String> selectedEvaluators = new ArrayList<>();
        for (String evaluatorId : session.getEvaluatorIds()) {
            for (String evaluatorItem : evaluatorList) {
                if (evaluatorItem.startsWith(evaluatorId)) {
                    selectedEvaluators.add(evaluatorItem);
                    break;
                }
            }
        }
        evaluatorSelector.setSelectedIndices(getIndices(selectedEvaluators, evaluatorList));
        
        // Check for conflicts
        checkEvaluatorConflicts();
    }
    
    private int[] getIndices(java.util.List<String> selectedItems, java.util.List<String> allItems) {
        java.util.List<Integer> indices = new ArrayList<>();
        for (String selected : selectedItems) {
            int index = allItems.indexOf(selected);
            if (index >= 0) {
                indices.add(index);
            }
        }
        return indices.stream().mapToInt(i -> i).toArray();
    }
    
    private void createSession() {
        try {
            // Validate fields
            if (txtStartDateTime.getText().isEmpty() || txtEndDateTime.getText().isEmpty() || 
                txtVenue.getText().isEmpty() || txtCapacity.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields.");
                return;
            }
            
            // Check evaluator conflicts
            if (lblConflictWarning.isVisible() && lblConflictWarning.getText().contains("Conflicts detected")) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "There are evaluator conflicts. Create session anyway?",
                    "Confirm Session Creation", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            String sessionId = "SESS" + (sessionListModel.size() + 1);
            String startDateTime = txtStartDateTime.getText();
            String endDateTime = txtEndDateTime.getText();
            String venue = txtVenue.getText();
            String type = (String) cbType.getSelectedItem();
            int capacity = Integer.parseInt(txtCapacity.getText());
            
            Session session = new Session(sessionId, startDateTime, endDateTime, venue, type, capacity);
            
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
                // FIX: Update student assignments in students_data.txt
                if (!session.getEvaluatorIds().isEmpty()) {
                    String evaluatorId = session.getEvaluatorIds().get(0);
                    for (String studentId : session.getStudentIds()) {
                        // Update student's assignment in their data file
                        studentDataController.assignStudentToSession(studentId, sessionId, evaluatorId);
                        // Also create assignment record for evaluator panel
                        sessionController.assignEvaluatorToStudent(evaluatorId, studentId, sessionId);
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Session created successfully!");
                loadSessions();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating session. There may be time conflicts.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid capacity number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void updateSession() {
        Session selected = sessionList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a session to update.");
            return;
        }
        
        try {
            // Check evaluator conflicts
            if (lblConflictWarning.isVisible() && lblConflictWarning.getText().contains("Conflicts detected")) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "There are evaluator conflicts. Update session anyway?",
                    "Confirm Session Update", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Clear old assignments first
            for (String studentId : selected.getStudentIds()) {
                studentDataController.removeStudentAssignment(studentId);
            }
            
            selected.setStartDateTime(txtStartDateTime.getText());
            selected.setEndDateTime(txtEndDateTime.getText());
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
                // Update student assignments
                if (!selected.getEvaluatorIds().isEmpty()) {
                    String evaluatorId = selected.getEvaluatorIds().get(0);
                    for (String studentId : selected.getStudentIds()) {
                        studentDataController.assignStudentToSession(studentId, selected.getSessionId(), evaluatorId);
                        sessionController.assignEvaluatorToStudent(evaluatorId, studentId, selected.getSessionId());
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Session updated successfully!");
                loadSessions();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating session. There may be time conflicts.");
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
            // Remove student assignments
            for (String studentId : selected.getStudentIds()) {
                studentDataController.removeStudentAssignment(studentId);
            }
            
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
        java.util.List<Award> awards = reportController.computeAwards();
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
        txtStartDateTime.setText("");
        txtEndDateTime.setText("");
        txtVenue.setText("");
        txtCapacity.setText("");
        studentSelector.clearSelection();
        evaluatorSelector.clearSelection();
        lblConflictWarning.setText("");
        lblConflictWarning.setVisible(false);
    }
}