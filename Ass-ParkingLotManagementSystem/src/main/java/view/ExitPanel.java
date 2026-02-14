package main.java.view;

import main.java.controller.ExitService;
import main.java.controller.FineService;
import main.java.model.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExitPanel extends JPanel {
    private JTextField txtLicensePlate;
    private JRadioButton rbCash, rbCard, rbEWallet;
    private ButtonGroup paymentGroup;
    private JTextArea txtReceipt;
    private JButton btnCalculate, btnPay;
    private JCheckBox chkPayFines;

    private ExitService exitService;
    private FineService fineService;

    // Temporary storage for calculated bill
    private Vehicle currentVehicle;
    private ParkingSpot currentSpot;
    private long hours;
    private double hourlyRate;
    private double parkingFee;
    private double totalFines;
    private double totalAmount;

    public ExitPanel() {
        exitService = new ExitService();
        fineService = FineService.getInstance();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TOP: Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        inputPanel.add(new JLabel("License Plate:"));
        txtLicensePlate = new JTextField();
        inputPanel.add(txtLicensePlate);

        inputPanel.add(new JLabel("Payment Method:"));
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbCash = new JRadioButton("Cash");
        rbCard = new JRadioButton("Card");
        rbEWallet = new JRadioButton("E-Wallet");
        paymentGroup = new ButtonGroup();
        paymentGroup.add(rbCash);
        paymentGroup.add(rbCard);
        paymentGroup.add(rbEWallet);
        rbCash.setSelected(true);
        paymentPanel.add(rbCash);
        paymentPanel.add(rbCard);
        paymentPanel.add(rbEWallet);
        inputPanel.add(paymentPanel);

        // Button row
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnCalculate = new JButton("1. Calculate Bill");
        btnPay = new JButton("2. Process Payment");
        btnPay.setEnabled(false); // disabled until bill is calculated
        buttonPanel.add(btnCalculate);
        buttonPanel.add(btnPay);
        inputPanel.add(new JLabel("")); // spacer
        inputPanel.add(buttonPanel);

        inputPanel.add(new JLabel(""));
        chkPayFines = new JCheckBox("Pay fines now", true);
        inputPanel.add(chkPayFines);

        add(inputPanel, BorderLayout.NORTH);

        // CENTER: Receipt / Bill Display
        txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setBorder(BorderFactory.createTitledBorder("Bill / Receipt"));
        add(new JScrollPane(txtReceipt), BorderLayout.CENTER);

        // Action Listeners
        btnCalculate.addActionListener(e -> calculateBill());
        btnPay.addActionListener(e -> processPayment());
    }

    private void calculateBill() {
        String plate = txtLicensePlate.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter license plate.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Get vehicle and spot
            ParkingLot parkingLot = ParkingLot.getInstance();
            currentVehicle = parkingLot.getParkedVehicle(plate);
            if (currentVehicle == null) {
                throw new IllegalArgumentException("Vehicle not found: " + plate);
            }
            String spotId = currentVehicle.getParkingSpotId();
            currentSpot = parkingLot.getParkingSpotById(spotId);
            if (currentSpot == null) {
                throw new IllegalStateException("Spot not found.");
            }

            // 2. Calculate hours and fees
            currentVehicle.setExitTime(LocalDateTime.now()); // temporary for calculation
            hours = currentVehicle.calculateParkingHours();
            hourlyRate = currentVehicle.getEffectiveHourlyRate(currentSpot);

            parkingFee = hours * hourlyRate;

            // 3. Check fines
            fineService.checkForNewFines(currentVehicle, currentSpot);
            totalFines = fineService.getTotalUnpaid(currentVehicle.getLicensePlate());

            // 4. Total due
            totalAmount = parkingFee + totalFines;

            // 5. Display bill
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            StringBuilder sb = new StringBuilder();
            sb.append("============= PARKING BILL =============\n");
            sb.append("License Plate : ").append(currentVehicle.getLicensePlate()).append("\n");
            sb.append("Spot ID       : ").append(currentSpot.getSpotId()).append("\n");
            sb.append("Entry Time    : ").append(currentVehicle.getEntryTime().format(dtf)).append("\n");
            sb.append("Exit Time     : ").append(currentVehicle.getExitTime().format(dtf)).append("\n");
            sb.append("Duration      : ").append(hours).append(" hour(s)\n");
            sb.append("Hourly Rate   : RM ").append(String.format("%.2f", hourlyRate)).append("\n");
            sb.append("Parking Fee   : RM ").append(String.format("%.2f", parkingFee)).append("\n");
            sb.append("Fines         : RM ").append(String.format("%.2f", totalFines)).append("\n");
            sb.append("-----------------------------------------\n");
            sb.append("TOTAL DUE     : RM ").append(String.format("%.2f", totalAmount)).append("\n");
            sb.append("=========================================\n");
            sb.append("Select payment method and click 'Process Payment'.");

            txtReceipt.setText(sb.toString());
            btnPay.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processPayment() {
        if (currentVehicle == null || currentSpot == null) {
            JOptionPane.showMessageDialog(this, "Please calculate bill first.", "No Bill", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Determine payment strategy
        PaymentStrategy payment;
        if (rbCash.isSelected()) payment = new CashPayment();
        else if (rbCard.isSelected()) payment = new CardPayment();
        else payment = new EWalletPayment();

        boolean payFines = chkPayFines.isSelected();

        try {
            // Process exit with payFines flag
            double paid = exitService.processExit(currentVehicle.getLicensePlate(), payment, payFines);

            // Calculate amounts for display
            double parkingFeePaid = parkingFee;
            double finesPaid = payFines ? totalFines : 0.0;
            double totalPaid = parkingFeePaid + finesPaid;

            // Generate receipt (using Receipt class)
            Receipt receipt = new Receipt(
                    currentVehicle.getLicensePlate(),
                    currentSpot.getSpotId(),
                    currentVehicle.getEntryTime(),
                    currentVehicle.getExitTime(),
                    hours,
                    hourlyRate,
                    parkingFeePaid,
                    finesPaid,
                    totalPaid,
                    payment.getPaymentType()
            );

            txtReceipt.setText(">>> EXIT PROCESSED <<<\n\n");
            if (!payFines && totalFines > 0) {
                txtReceipt.append("NOTE: Fines were NOT paid. They remain outstanding for next visit.\n\n");
            }
            txtReceipt.append(receiptToString(receipt));

            // Reset UI for next exit
            txtLicensePlate.setText("");
            btnPay.setEnabled(false);
            chkPayFines.setSelected(true);
            currentVehicle = null;
            currentSpot = null;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Payment Error: " + ex.getMessage(), "Exit Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String receiptToString(Receipt receipt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "============= PARKING RECEIPT =============\n" +
               "License Plate : " + receipt.getLicensePlate() + "\n" +
               "Parking Spot   : " + receipt.getSpotId() + "\n" +
               "Entry Time     : " + receipt.getEntryTime().format(dtf) + "\n" +
               "Exit Time      : " + receipt.getExitTime().format(dtf) + "\n" +
               "Duration       : " + receipt.getHours() + " hour(s)\n" +
               "Hourly Rate    : RM " + String.format("%.2f", receipt.getHourlyRate()) + "\n" +
               "Parking Fee    : RM " + String.format("%.2f", receipt.getParkingFee()) + "\n" +
               "Fines          : RM " + String.format("%.2f", receipt.getFines()) + "\n" +
               "-------------------------------------------\n" +
               "TOTAL PAID     : RM " + String.format("%.2f", receipt.getTotalAmount()) + "\n" +
               "Payment Method : " + receipt.getPaymentType() + "\n" +
               "Payment Time   : " + receipt.getPaymentTime().format(dtf) + "\n" +
               "===========================================\n";
    }
}