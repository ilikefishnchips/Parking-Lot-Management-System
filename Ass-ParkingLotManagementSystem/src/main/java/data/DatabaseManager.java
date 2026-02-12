package main.java.data;

import main.java.model.Fine;
import main.java.model.ParkingSpot;
import main.java.model.ParkingSpotType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    
    // MySQL configuration
    private static final String URL = "jdbc:mysql://localhost:3306/parkinglot_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseManager() {
        try {
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

    // ---------------------- FLOOR OPERATIONS ----------------------
    public void saveFloor(int floorNumber) {
        String sql = "INSERT IGNORE INTO floor (floor_number) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, floorNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteFloor(int floorNumber) {
        String sql = "DELETE FROM floor WHERE floor_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, floorNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Integer> getAllFloors() {
        List<Integer> floors = new ArrayList<>();
        String sql = "SELECT floor_number FROM floor ORDER BY floor_number";
        try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) floors.add(rs.getInt("floor_number"));
        } catch (SQLException e) { e.printStackTrace(); }
        return floors;
    }

    // ---------------------- SPOT OPERATIONS ----------------------
    public void saveParkingSpot(ParkingSpot spot) {
        String sql = "INSERT INTO parking_spot (spot_id, floor_number, row_label, spot_number, type, hourly_rate, is_occupied, current_vehicle_plate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE hourly_rate = VALUES(hourly_rate), type = VALUES(type)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, spot.getSpotId());
            pstmt.setInt(2, spot.getFloorNumber());
            pstmt.setString(3, spot.getRow());
            pstmt.setInt(4, spot.getSpotNumber());
            pstmt.setString(5, spot.getType().name());
            pstmt.setDouble(6, spot.getHourlyRate());
            pstmt.setBoolean(7, spot.isOccupied());
            pstmt.setString(8, spot.getCurrentVehicle() != null ? spot.getCurrentVehicle().getLicensePlate() : null);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateSpotOccupancy(String spotId, boolean occupied, String vehiclePlate) {
        String sql = "UPDATE parking_spot SET is_occupied = ?, current_vehicle_plate = ? WHERE spot_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, occupied);
            pstmt.setString(2, occupied ? vehiclePlate : null);
            pstmt.setString(3, spotId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteParkingSpot(String spotId) {
        String sql = "DELETE FROM parking_spot WHERE spot_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, spotId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<ParkingSpot> loadAllSpots() {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spot";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ParkingSpotType type = ParkingSpotType.valueOf(rs.getString("type"));
                ParkingSpot spot = new ParkingSpot(
                    rs.getInt("floor_number"),
                    rs.getString("row_label"),
                    rs.getInt("spot_number"),
                    type
                );
                spot.setHourlyRate(rs.getDouble("hourly_rate"));
                if (rs.getBoolean("is_occupied")) {
                    spot.setOccupied(true);
                }
                spots.add(spot);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return spots;
    }

    // ---------------------- VEHICLE ENTRY OPERATIONS ----------------------
    public void saveVehicleEntry(String licensePlate, String vehicleType, LocalDateTime entryTime, String spotId) {
        String sql = "INSERT INTO vehicle_entry (license_plate, vehicle_type, entry_time, spot_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, licensePlate);
            pstmt.setString(2, vehicleType);
            pstmt.setTimestamp(3, Timestamp.valueOf(entryTime));
            pstmt.setString(4, spotId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateVehicleExit(String licensePlate, LocalDateTime exitTime) {
        String sql = "UPDATE vehicle_entry SET exit_time = ? WHERE license_plate = ? AND exit_time IS NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(exitTime));
            pstmt.setString(2, licensePlate);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<VehicleEntryRecord> loadActiveVehicleEntries() {
        List<VehicleEntryRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM vehicle_entry WHERE exit_time IS NULL";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                VehicleEntryRecord rec = new VehicleEntryRecord(
                    rs.getString("license_plate"),
                    rs.getString("vehicle_type"),
                    rs.getTimestamp("entry_time").toLocalDateTime(),
                    rs.getString("spot_id")
                );
                records.add(rec);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return records;
    }

    // Helper class for vehicle entry records
    public static class VehicleEntryRecord {
        public final String licensePlate;
        public final String vehicleType;
        public final LocalDateTime entryTime;
        public final String spotId;

        public VehicleEntryRecord(String lp, String vt, LocalDateTime et, String sid) {
            this.licensePlate = lp;
            this.vehicleType = vt;
            this.entryTime = et;
            this.spotId = sid;
        }
    }
}
