package main.java.data;

import main.java.model.Fine;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    
    // MySQL configuration – adjust if you changed credentials
    private static final String URL = "jdbc:mysql://localhost:3306/parkinglot_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";      // default XAMPP user
    private static final String PASSWORD = "";      // default XAMPP password (empty)

    private DatabaseManager() {
        try {
            // Explicitly load driver (optional for modern JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // ---------------------- FINE OPERATIONS ----------------------
    public void saveFine(Fine fine) {
        String sql = "INSERT INTO fine (fine_id, license_plate, amount, reason, issue_date, paid) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fine.getFineId());
            pstmt.setString(2, fine.getLicensePlate());
            pstmt.setDouble(3, fine.getAmount());
            pstmt.setString(4, fine.getReason());
            pstmt.setTimestamp(5, Timestamp.valueOf(fine.getIssueDate()));
            pstmt.setBoolean(6, fine.isPaid());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFinePaid(String fineId) {
        String sql = "UPDATE fine SET paid = TRUE WHERE fine_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fineId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Fine> loadAllFines() {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fine";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Fine fine = new Fine(
                        rs.getString("fine_id"),
                        rs.getString("license_plate"),
                        rs.getDouble("amount"),
                        rs.getString("reason"),
                        rs.getTimestamp("issue_date").toLocalDateTime()
                );
                if (rs.getBoolean("paid")) {
                    fine.markAsPaid();
                }
                fines.add(fine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }

    // ---------------------- REVENUE OPERATIONS ----------------------
    public void addRevenue(double amount, String description) {
        String sql = "INSERT INTO revenue (amount, timestamp, description) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(3, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM revenue";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTodayRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM revenue WHERE DATE(timestamp) = CURDATE()";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // ---------------------- (Optional) PARKING LOT CONFIGURATION ----------------------
    // You will add methods for floors & spots later when implementing admin config.
}