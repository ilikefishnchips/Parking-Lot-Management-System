package main.java.view;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.controller.FineService;
import main.java.model.*;

public class AdminPanel extends JPanel {

    private JTabbedPane tabbedPane;
    private ParkingLot parkingLot;
    private FineService fineService;
    
    // Reference to the inner panel of the Occupancy tab (so we can refresh it)
    private JPanel occupancyPanel;

    public AdminPanel() {
        parkingLot = ParkingLot.getInstance();
        fineService = FineService.getInstance();

        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Current Vehicles", createVehiclePanel());
        tabbedPane.addTab("Occupancy Report", createOccupancyPanel()); // now returns JScrollPane
        tabbedPane.addTab("Revenue Report", createRevenuePanel());
        tabbedPane.addTab("Fine Management", createFinePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────
    // 1. CURRENT VEHICLES TAB
    // ─────────────────────────────────────────────────────────
    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"License Plate", "Vehicle Type", "Spot ID", "Entry Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshVehicleTable(model));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(btnRefresh);
        panel.add(topPanel, BorderLayout.NORTH);

        refreshVehicleTable(model);
        return panel;
    }

    private void refreshVehicleTable(DefaultTableModel model) {
        model.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Floor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isOccupied()) {
                    Vehicle v = spot.getCurrentVehicle();
                    if (v != null) {
                        String type = v.getClass().getSimpleName();
                        String entryTime = v.getEntryTime().format(dtf);
                        model.addRow(new Object[]{
                                v.getLicensePlate(),
                                type,
                                spot.getSpotId(),
                                entryTime
                        });
                    }
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // 2. OCCUPANCY REPORT TAB (FIXED)
    // ─────────────────────────────────────────────────────────
    private JScrollPane createOccupancyPanel() {
        // Create the inner panel (BoxLayout Y_AXIS) and store it in the field
        occupancyPanel = new JPanel();
        occupancyPanel.setLayout(new BoxLayout(occupancyPanel, BoxLayout.Y_AXIS));
        occupancyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Populate the panel with current data
        refreshOccupancyDisplay(occupancyPanel);

        // Wrap in JScrollPane and return
        return new JScrollPane(occupancyPanel);
    }

    private void refreshOccupancyDisplay(JPanel panel) {
        panel.removeAll(); // clear all components

        // Overall occupancy
        double overall = parkingLot.getOverallOccupancy();
        panel.add(createStatPanel("Overall Occupancy", overall));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // By Floor
        JLabel floorLabel = new JLabel("Occupancy by Floor:");
        floorLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(floorLabel);
        Map<Integer, Double> byFloor = parkingLot.getOccupancyByFloor();
        for (Map.Entry<Integer, Double> entry : byFloor.entrySet()) {
            panel.add(createStatPanel("Floor " + entry.getKey(), entry.getValue()));
        }

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // By Spot Type
        JLabel typeLabel = new JLabel("Occupancy by Spot Type:");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(typeLabel);
        Map<ParkingSpotType, Double> byType = parkingLot.getOccupancyBySpotType();
        for (Map.Entry<ParkingSpotType, Double> entry : byType.entrySet()) {
            panel.add(createStatPanel(entry.getKey().toString(), entry.getValue()));
        }

        // Refresh button (inside the panel)
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshOccupancyDisplay(panel));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnRefresh);

        panel.revalidate();
        panel.repaint();
    }

    private JPanel createStatPanel(String labelText, double percentage) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel label = new JLabel(labelText + ": ");
        label.setPreferredSize(new Dimension(150, 25));
        panel.add(label, BorderLayout.WEST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) (percentage * 100));
        bar.setStringPainted(true);
        bar.setString(String.format("%.1f%%", percentage * 100));
        panel.add(bar, BorderLayout.CENTER);

        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // 3. REVENUE REPORT TAB
    // ─────────────────────────────────────────────────────────
    private JScrollPane createRevenuePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        double total = parkingLot.getTotalRevenue();
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.add(new JLabel("Total Revenue: "));
        JLabel totalValue = new JLabel("RM " + String.format("%.2f", total));
        totalValue.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalPanel.add(totalValue);
        panel.add(totalPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel todayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        todayPanel.add(new JLabel("Today's Revenue: "));
        JLabel todayValue = new JLabel("(Detailed tracking not implemented)");
        todayValue.setFont(new Font("SansSerif", Font.PLAIN, 14));
        todayPanel.add(todayValue);
        panel.add(todayPanel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JTextArea note = new JTextArea(
                "Note: Total revenue includes all parking fees and fines collected.\n" +
                "Transaction history can be added in future versions."
        );
        note.setEditable(false);
        note.setBackground(panel.getBackground());
        panel.add(note);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> {
            totalValue.setText("RM " + String.format("%.2f", parkingLot.getTotalRevenue()));
        });
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnRefresh);

        return new JScrollPane(panel);
    }

    // ─────────────────────────────────────────────────────────
    // 4. FINE MANAGEMENT TAB
    // ─────────────────────────────────────────────────────────
    private JPanel createFinePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        JLabel lblScheme = new JLabel("Select Fine Scheme:");
        String[] schemes = {"Fixed", "Progressive", "Hourly"};
        JComboBox<String> cmbScheme = new JComboBox<>(schemes);
        JButton btnApply = new JButton("Apply Scheme");
        topPanel.add(lblScheme);
        topPanel.add(cmbScheme);
        topPanel.add(btnApply);

        panel.add(topPanel, BorderLayout.NORTH);

        JTextArea txtFines = new JTextArea();
        txtFines.setEditable(false);
        panel.add(new JScrollPane(txtFines), BorderLayout.CENTER);

        btnApply.addActionListener(e -> {
            String selected = (String) cmbScheme.getSelectedItem();
            switch (selected) {
                case "Fixed":       fineService.setFineScheme(new FixedFineScheme()); break;
                case "Progressive": fineService.setFineScheme(new ProgressiveFineScheme()); break;
                case "Hourly":      fineService.setFineScheme(new HourlyFineScheme()); break;
            }
            JOptionPane.showMessageDialog(panel,
                    "Fine scheme changed to " + selected + " (Applied to future fines only)");
        });

        JButton btnRefresh = new JButton("Refresh Unpaid Fines");
        topPanel.add(btnRefresh);
        btnRefresh.addActionListener(e -> {
            txtFines.setText("=== UNPAID FINES ===\n\n");
            boolean found = false;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Fine fine : fineService.getAllFines()) {
                if (!fine.isPaid()) {
                    txtFines.append(
                            "Plate: " + fine.getLicensePlate() +
                            "\nAmount: RM" + fine.getAmount() +
                            "\nReason: " + fine.getReason() +
                            "\nIssued: " + fine.getIssueDate().format(dtf) +
                            "\n----------------------\n"
                    );
                    found = true;
                }
            }
            if (!found) {
                txtFines.append("No unpaid fines found.");
            }
        });

        return panel;
    }
}