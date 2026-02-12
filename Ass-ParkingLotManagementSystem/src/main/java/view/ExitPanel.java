package main.java.view;

import main.java.controller.ExitService;
import main.java.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ExitPanel extends JPanel {
    private JTextField txtLicensePlate;
    private JRadioButton rbCash, rbCard, rbEWallet;
    private ButtonGroup paymentGroup;
    private JTextArea txtReceipt;
    private JButton btnExit;

    private ExitService exitService;

    public ExitPanel() {
        exitService = new ExitService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // TOP: Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

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
        rbCash.setSelected(true); // default
        paymentPanel.add(rbCash);
        paymentPanel.add(rbCard);
        paymentPanel.add(rbEWallet);
        inputPanel.add(paymentPanel);

        btnExit = new JButton("Process Exit");
        inputPanel.add(new JLabel("")); // spacer
        inputPanel.add(btnExit);

        add(inputPanel, BorderLayout.NORTH);

        // CENTER: Receipt Display
        txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setBorder(BorderFactory.createTitledBorder("Exit Receipt"));
        add(new JScrollPane(txtReceipt), BorderLayout.CENTER);

        // Button Action
        btnExit.addActionListener((ActionEvent e) -> processExit());
    }

    private void processExit() {
        String plate = txtLicensePlate.getText().trim();
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a license plate.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Determine selected payment strategy
        PaymentStrategy payment;
        if (rbCash.isSelected()) {
            payment = new CashPayment();
        } else if (rbCard.isSelected()) {
            payment = new CardPayment();
        } else {
            payment = new EWalletPayment();
        }

        try {
            double totalPaid = exitService.processExit(plate, payment);

            // Receipt is also printed to console via ExitService,
            // but we display a summary in the GUI.
            txtReceipt.setText("EXIT PROCESSED SUCCESSFULLY\n\n");
            txtReceipt.append("License Plate : " + plate + "\n");
            txtReceipt.append("Payment Method : " + payment.getPaymentType() + "\n");
            txtReceipt.append("Total Paid    : RM " + String.format("%.2f", totalPaid) + "\n");
            txtReceipt.append("\nSee console for detailed receipt.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Exit Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
