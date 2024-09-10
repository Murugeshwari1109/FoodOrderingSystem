package dao;
import database.DatabaseUtil;

import java.sql.Connection;
//import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesStatsDAO {

    // Method to get restaurant-wise sales statistics
    public List<Map<String, Object>> getRestaurantWiseSalesStats(LocalDate startDate, LocalDate endDate) throws SQLException {
        String query = "SELECT r.restaurant_name, COUNT(o.order_id) AS total_orders, SUM(o.total_amount) AS total_sales " +
                       "FROM Orders o " +
                       "JOIN Restaurants r ON o.restaurant_id = r.restaurant_id ";

        // Add date filter condition if dates are provided
        if (startDate != null && endDate != null) {
            query += "WHERE o.order_date BETWEEN ? AND ? ";
        }
        
        query += "GROUP BY r.restaurant_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (startDate != null && endDate != null) {
                stmt.setDate(1, java.sql.Date.valueOf(startDate));
                stmt.setDate(2, java.sql.Date.valueOf(endDate));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("restaurant_name", rs.getString("restaurant_name"));
                    row.put("total_orders", rs.getInt("total_orders"));
                    row.put("total_sales", rs.getBigDecimal("total_sales"));
                    results.add(row);
                }
                return results;
            }
        }
    }

    // Method to get user-wise spend statistics
    public List<Map<String, Object>> getUserWiseSpendStats(LocalDate startDate, LocalDate endDate) throws SQLException {
        String query = "SELECT u.first_name, u.last_name, COUNT(o.order_id) AS total_orders, SUM(o.total_amount) AS total_spent " +
                       "FROM Orders o " +
                       "JOIN Users u ON o.user_id = u.user_id ";

        // Add date filter condition if dates are provided
        if (startDate != null && endDate != null) {
            query += "WHERE o.order_date BETWEEN ? AND ? ";
        }
        
        query += "GROUP BY u.user_id, u.first_name, u.last_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (startDate != null && endDate != null) {
                stmt.setDate(1, java.sql.Date.valueOf(startDate));
                stmt.setDate(2, java.sql.Date.valueOf(endDate));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("user_name", rs.getString("first_name") + " " + rs.getString("last_name"));
                    row.put("total_orders", rs.getInt("total_orders"));
                    row.put("total_spent", rs.getBigDecimal("total_spent"));
                    results.add(row);
                }
                return results;
            }
        }
    }

    // Method to get location-wise sales statistics
    public List<Map<String, Object>> getLocationWiseSalesStats(LocalDate startDate, LocalDate endDate) throws SQLException {
        String query = "SELECT a.city, COUNT(o.order_id) AS total_orders, SUM(o.total_amount) AS total_sales " +
                       "FROM Orders o " +
                       "JOIN Addresses a ON o.delivery_address_id = a.address_id ";

        // Add date filter condition if dates are provided
        if (startDate != null && endDate != null) {
            query += "WHERE o.order_date BETWEEN ? AND ? ";
        }
        
        query += "GROUP BY a.city";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (startDate != null && endDate != null) {
                stmt.setDate(1, java.sql.Date.valueOf(startDate));
                stmt.setDate(2, java.sql.Date.valueOf(endDate));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("city", rs.getString("city"));
                    row.put("total_orders", rs.getInt("total_orders"));
                    row.put("total_sales", rs.getBigDecimal("total_sales"));
                    results.add(row);
                }
                return results;
            }
        }
    }
}
