package Member2_assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class EntryPanel extends JPanel {
    
    // UI Components
    private JTextField txtPlate;
    private JComboBox<String> cmbVehicleType;
    private JComboBox<ParkingSpotWrapper> cmbSpots; // Wrapper to display spots nicely
    private JTextArea txtReceipt;
    private JButton btnPark;

    // Logic Controller
    private EntryService entryService;

    public EntryPanel() {
        this.entryService = new EntryService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP PANEL: Inputs ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        inputPanel.add(new JLabel("License Plate:"));
        txtPlate = new JTextField();
        inputPanel.add(txtPlate);

        inputPanel.add(new JLabel("Vehicle Type:"));
        String[] types = {"Motorcycle", "Car", "SUV", "Handicapped"};
        cmbVehicleType = new JComboBox<>(types);
        inputPanel.add(cmbVehicleType);

        inputPanel.add(new JLabel("Select Spot:"));
        cmbSpots = new JComboBox<>();
        loadMockSpots(); // Helper to fill dropdown
        inputPanel.add(cmbSpots);

        btnPark = new JButton("Generate Ticket");
        inputPanel.add(new JLabel("")); // Spacer
        inputPanel.add(btnPark);
        
        add(inputPanel, BorderLayout.NORTH);

        // --- CENTER PANEL: Output ---
        txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setBorder(BorderFactory.createTitledBorder("Ticket Output"));
        add(new JScrollPane(txtReceipt), BorderLayout.CENTER);

        // --- ACTION LISTENER (The Brains) ---
        btnPark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleParkButton();
            }
        });
    }

    private void handleParkButton() {
        try {
            // 1. Get Input
            String plate = txtPlate.getText().trim();
            String type = (String) cmbVehicleType.getSelectedItem();
            
            // Get the actual ParkingSpot object from the dropdown wrapper
            ParkingSpotWrapper selectedWrapper = (ParkingSpotWrapper) cmbSpots.getSelectedItem();
            ParkingSpot spot = selectedWrapper.spot;

            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a license plate.");
                return;
            }

            // 2. Call Logic (Your EntryService)
            Ticket ticket = entryService.processEntry(plate, type, spot);

            // 3. Update UI on Success
            txtReceipt.setText(">>> TICKET GENERATED <<<\n\n");
            txtReceipt.append(ticket.toString());
            
            // Refresh the spot text to show it's now occupied
            cmbSpots.repaint(); 

        } catch (Exception ex) {
            // 4. Handle Errors (e.g., Wrong Spot Type)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Entry Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MOCK DATA HELPER ---
    // In the real project, Member 1 will give you a List<ParkingSpot>
    private void loadMockSpots() {
        List<ParkingSpot> mockSpots = new ArrayList<>();
        mockSpots.add(new ParkingSpot("L1-C1", ParkingSpotType.COMPACT));
        mockSpots.add(new ParkingSpot("L1-R1", ParkingSpotType.REGULAR));
        mockSpots.add(new ParkingSpot("L1-H1", ParkingSpotType.HANDICAPPED));

        for (ParkingSpot s : mockSpots) {
            cmbSpots.addItem(new ParkingSpotWrapper(s));
        }
    }

    // Helper class to make the Dropdown look nice
    private class ParkingSpotWrapper {
        ParkingSpot spot;
        public ParkingSpotWrapper(ParkingSpot spot) { this.spot = spot; }
        
        @Override
        public String toString() {
            String status = spot.isOccupied() ? "[OCCUPIED]" : "[Free]";
            return spot.getSpotId() + " (" + spot.getType() + ") " + status;
        }
    }
}