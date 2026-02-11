package main.java.model;

import java.time.LocalDateTime;

public class Fine {

    private String fineId;
    private String licensePlate;
    private double amount;
    private String reason;
    private LocalDateTime issueDate;
    private boolean paid;

    public Fine(String fineId,
                String licensePlate,
                double amount,
                String reason,
                LocalDateTime issueDate) {

        this.fineId = fineId;
        this.licensePlate = licensePlate;
        this.amount = amount;
        this.reason = reason;
        this.issueDate = issueDate;
        this.paid = false;
    }

    public String getFineId() {
        return fineId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void markAsPaid() {
        this.paid = true;
    }
}
