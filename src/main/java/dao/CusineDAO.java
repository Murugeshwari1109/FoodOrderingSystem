package dao;

import model.Cusine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseUtil;

public class CusineDAO {

    // Add a new cusine
    public int addCusine(Cusine cusine) throws SQLException {
        String sql = "INSERT INTO Cusines (cusine_name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cusine.getCusineName());
            stmt.executeUpdate();

            // Retrieve generated keys
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Cusine> getAllCusines() throws SQLException {
        List<Cusine> cusines = new ArrayList<>();
        String sql = "SELECT * FROM Cusines";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cusine cusine = new Cusine(
                    rs.getInt("cusine_id"),
                    rs.getString("cusine_name")
                );
                cusines.add(cusine);
            }
        }
        return cusines;
    }

    // Retrieve a cusine by ID
    public Cusine getCusineById(int cusineId) throws SQLException {
        String sql = "SELECT * FROM Cusines WHERE cusine_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cusineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cusine(
                        rs.getInt("cusine_id"),
                        rs.getString("cusine_name")
                    );
                }
            }
        }
        return null;
    }

    // Update a cusine
    public void updateCusine(Cusine cusine) throws SQLException {
        String sql = "UPDATE Cusines SET cusine_name = ? WHERE cusine_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cusine.getCusineName());
            stmt.setInt(2, cusine.getCusineId());
            stmt.executeUpdate();
        }
    }
}
