package main.java.view;

import java.awt.*;
import java.util.List;
import javax.swing.*;

import main.java.data.DatabaseManager;
import main.java.model.*;

public class ConfigPanel extends JPanel {
    private ParkingLot parkingLot;
    private DatabaseManager db;
    private DefaultListModel<String> floorListModel;
    private JList<String> floorList;
    private JTextField txtRow, txtSpotNumber, txtHourlyRate;
    private JComboBox<String> cmbSpotType;

    public ConfigPanel() {
        parkingLot = ParkingLot.getInstance();
        db = DatabaseManager.getInstance();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left side: Floor list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Configured Floors:"), BorderLayout.NORTH);
        
        floorListModel = new DefaultListModel<>();
        floorList = new JList<>(floorListModel);
        JScrollPane scrollPane = new JScrollPane(floorList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Floor management buttons
        JPanel floorButtonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        JButton btnAddFloor = new JButton("Add Floor");
        JButton btnDeleteFloor = new JButton("Delete Floor");
        JButton btnRefreshFloors = new JButton("Refresh");
        JButton btnResetToDefault = new JButton("Reset to Default");
        
        btnAddFloor.addActionListener(e -> addFloor());
        btnDeleteFloor.addActionListener(e -> deleteFloor());
        btnRefreshFloors.addActionListener(e -> refreshFloorList());
        btnResetToDefault.addActionListener(e -> resetToDefault());
        
        floorButtonPanel.add(btnAddFloor);
        floorButtonPanel.add(btnDeleteFloor);
        floorButtonPanel.add(btnRefreshFloors);
        floorButtonPanel.add(btnResetToDefault);
        leftPanel.add(floorButtonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        
        // Right side: Spot management
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        // Spot creation form
        JPanel spotForm = new JPanel(new GridLayout(0, 2, 5, 5));
        spotForm.setBorder(BorderFactory.createTitledBorder("Add New Spot to Floor"));
        
        spotForm.add(new JLabel("Floor:"));
        JLabel lblSelectedFloor = new JLabel("Select a floor from left");
        spotForm.add(lblSelectedFloor);
        
        spotForm.add(new JLabel("Row (e.g., E):"));
        txtRow = new JTextField();
        spotForm.add(txtRow);
        
        spotForm.add(new JLabel("Spot Number:"));
        txtSpotNumber = new JTextField();
        spotForm.add(txtSpotNumber);
        
        spotForm.add(new JLabel("Spot Type:"));
        cmbSpotType = new JComboBox<>(new String[]{"COMPACT", "REGULAR", "HANDICAPPED", "RESERVED"});
        spotForm.add(cmbSpotType);
        
        spotForm.add(new JLabel("Hourly Rate (RM):"));
        txtHourlyRate = new JTextField();
        spotForm.add(txtHourlyRate);
        
        JButton btnAddSpot = new JButton("Add Spot");
        spotForm.add(new JLabel());
        spotForm.add(btnAddSpot);
        
        rightPanel.add(spotForm, BorderLayout.NORTH);
        
        // Update selected floor when selection changes
        floorList.addListSelectionListener(e -> {
            String selected = floorList.getSelectedValue();
            lblSelectedFloor.setText(selected != null ? selected.replace("Floor ", "") : "Select a floor");
        });
        
        btnAddSpot.addActionListener(e -> addSpot());
        
        // Instructions
        JTextArea instructions = new JTextArea(
            "Instructions:\n" +
            "1. Select a floor from the left list\n" +
            "2. Fill in the spot details\n" +
            "3. Click 'Add Spot' to create a new parking spot\n" +
            "4. Click 'Refresh' to update the floor list"
        );
        instructions.setEditable(false);
        instructions.setBackground(getBackground());
        rightPanel.add(instructions, BorderLayout.CENTER);
        
        add(rightPanel, BorderLayout.CENTER);
        
        // Initial load
        refreshFloorList();
    }
    
    private void refreshFloorList() {
        floorListModel.clear();
        List<Integer> floors = db.getAllFloors();
        for (Integer floorNum : floors) {
            floorListModel.addElement("Floor " + floorNum);
        }
    }
    
    private void addFloor() {
        List<Integer> floors = db.getAllFloors();
        int newFloorNum = floors.isEmpty() ? 1 : floors.get(floors.size() - 1) + 1;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Add Floor " + newFloorNum + "?",
            "Add Floor",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            db.saveFloor(newFloorNum);
            // Add floor to in-memory ParkingLot
            Floor newFloor = new Floor(newFloorNum);
            parkingLot.getFloors().add(newFloor);
            refreshFloorList();
            JOptionPane.showMessageDialog(this, "Floor " + newFloorNum + " added successfully!");
        }
    }
    
    private void deleteFloor() {
        String selected = floorList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a floor to delete.");
            return;
        }
        
        int floorNum = Integer.parseInt(selected.replace("Floor ", ""));
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete Floor " + floorNum + "?\nThis will also delete all parking spots on this floor.",
            "Delete Floor",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            db.deleteFloor(floorNum);
            // Remove floor from in-memory ParkingLot
            parkingLot.getFloors().removeIf(f -> f.getFloorNumber() == floorNum);
            // Remove all spots of this floor from allSpots map
            parkingLot.getAllSpots().entrySet().removeIf(e -> e.getValue().getFloorNumber() == floorNum);
            refreshFloorList();
            JOptionPane.showMessageDialog(this, "Floor " + floorNum + " deleted successfully!");
        }
    }
    
    private void addSpot() {
        String selectedFloor = floorList.getSelectedValue();
        if (selectedFloor == null) {
            JOptionPane.showMessageDialog(this, "Select a floor first.");
            return;
        }
        
        int floorNum = Integer.parseInt(selectedFloor.replace("Floor ", ""));
        String row = txtRow.getText().trim();
        String spotNumStr = txtSpotNumber.getText().trim();
        String typeStr = (String) cmbSpotType.getSelectedItem();
        String rateStr = txtHourlyRate.getText().trim();
        
        if (row.isEmpty() || spotNumStr.isEmpty() || rateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }
        
        try {
            int spotNum = Integer.parseInt(spotNumStr);
            double rate = Double.parseDouble(rateStr);
            ParkingSpotType type = ParkingSpotType.valueOf(typeStr);
            
            ParkingSpot spot = new ParkingSpot(floorNum, row, spotNum, type);
            spot.setHourlyRate(rate);
            db.saveParkingSpot(spot);
            
            // Update ParkingLot singleton
            parkingLot.getAllSpots().put(spot.getSpotId(), spot);
            for (Floor floor : parkingLot.getFloors()) {
                if (floor.getFloorNumber() == floorNum) {
                    floor.addSpot(spot);
                    break;
                }
            }
            
            JOptionPane.showMessageDialog(this, "Spot " + spot.getSpotId() + " added!");
            
            // Clear fields
            txtRow.setText("");
            txtSpotNumber.setText("");
            txtHourlyRate.setText("");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format.");
        }
    }

    private void resetToDefault() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "This will delete all current parking spots and reset to default (42 spots across 5 floors).\nAre you sure?",
            "Reset to Default",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            db.resetParkingSpotsToDefault();
            parkingLot.reloadFromDatabase();
            refreshFloorList();
            JOptionPane.showMessageDialog(this, "Parking spots have been reset to default!");
        }
    }
}
