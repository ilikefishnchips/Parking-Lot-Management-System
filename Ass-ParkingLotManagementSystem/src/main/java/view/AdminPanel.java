package main.java.view;

import javax.swing.*;

import main.java.controller.FineService;
import main.java.model.Fine;
import main.java.model.FixedFineScheme;
import main.java.model.HourlyFineScheme;
import main.java.model.ProgressiveFineScheme;

import java.awt.*;

public class AdminPanel extends JPanel {

    private JTabbedPane tabbedPane;

    public AdminPanel() {

        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("Current Vehicles", createVehiclePanel());
        tabbedPane.addTab("Occupancy Report", createOccupancyPanel());
        tabbedPane.addTab("Revenue Report", createRevenuePanel());
        tabbedPane.addTab("Fine Management", createFinePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Vehicles currently parked will be shown here."));
        return panel;
    }

    private JPanel createOccupancyPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Occupancy report will be shown here."));
        return panel;
    }

    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Revenue report will be shown here."));
        return panel;
    }

    private JPanel createFinePanel() {

    JPanel panel = new JPanel(new BorderLayout(10, 10));

    // 🔹 Top panel for scheme selection
    JPanel topPanel = new JPanel();

    JLabel lblScheme = new JLabel("Select Fine Scheme:");
    String[] schemes = {"Fixed", "Progressive", "Hourly"};
    JComboBox<String> cmbScheme = new JComboBox<>(schemes);

    JButton btnApply = new JButton("Apply Scheme");

    topPanel.add(lblScheme);
    topPanel.add(cmbScheme);
    topPanel.add(btnApply);

    panel.add(topPanel, BorderLayout.NORTH);

    // 🔹 Center area to display unpaid fines
    JTextArea txtFines = new JTextArea();
    txtFines.setEditable(false);
    panel.add(new JScrollPane(txtFines), BorderLayout.CENTER);

    // 🔹 Button action
    btnApply.addActionListener(e -> {

        String selected = (String) cmbScheme.getSelectedItem();

        FineService fineService = FineService.getInstance();

        switch (selected) {
            case "Fixed":
                fineService.setFineScheme(new FixedFineScheme());
                break;
            case "Progressive":
                fineService.setFineScheme(new ProgressiveFineScheme());
                break;
            case "Hourly":
                fineService.setFineScheme(new HourlyFineScheme());
                break;
        }

        JOptionPane.showMessageDialog(panel,
                "Fine scheme changed to " + selected + " (Applied to future fines only)");
    });

    // 🔹 Load unpaid fines display
    JButton btnRefresh = new JButton("Refresh Unpaid Fines");
    topPanel.add(btnRefresh);

    btnRefresh.addActionListener(e -> {

        FineService fineService = FineService.getInstance();

        txtFines.setText("=== UNPAID FINES ===\n\n");

        for (Fine fine : fineService.getAllFines()) {
            if (!fine.isPaid()) {
                txtFines.append(
                        "Plate: " + fine.getLicensePlate() +
                        "\nAmount: RM" + fine.getAmount() +
                        "\nReason: " + fine.getReason() +
                        "\n----------------------\n"
                    );
                }
            }
        
            if (txtFines.getText().equals("=== UNPAID FINES ===\n\n")) {
            txtFines.append("No unpaid fines found.");
        }
        
        });

        return panel;
    }

}
