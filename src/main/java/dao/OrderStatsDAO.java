package dao;

import database.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStatsDAO {

    // Method to get food-wise order count for a specific user with optional month filter
	public List<Map<String, Object>> getFoodWiseOrderCountByUser(String userId, String month) throws SQLException {
	    // Query to get food order count per food item for a specific user
	    String query = "SELECT f.food_name, COUNT(oi.menu_item_id) AS order_count " +
	                   "FROM OrderItems oi " +
	                   "JOIN MenuItems mi ON oi.menu_item_id = mi.menu_item_id " +
	                   "JOIN Foods f ON mi.food_id = f.food_id " +
	                   "JOIN Orders o ON oi.order_id = o.order_id " +
	                   "WHERE o.user_id = ? " +
	                   (month != null ? "AND DATE_FORMAT(o.order_date, '%Y-%m') = ? " : "") +
	                   "GROUP BY f.food_name";

	    String totalOrderQuery = "SELECT COUNT(*) AS total_orders FROM Orders WHERE user_id = ?" +
	                             (month != null ? " AND DATE_FORMAT(order_date, '%Y-%m') = ?" : "");

	    try (Connection conn = DatabaseUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query);
	         PreparedStatement totalOrderStmt = conn.prepareStatement(totalOrderQuery)) {

	        // Set parameters for both queries
	        stmt.setInt(1, Integer.parseInt(userId));
	        totalOrderStmt.setInt(1, Integer.parseInt(userId));
	        if (month != null) {
	            stmt.setString(2, month);
	            totalOrderStmt.setString(2, month);
	        }

	        // Execute total order query
	        int totalOrders;
	        try (ResultSet totalRs = totalOrderStmt.executeQuery()) {
	            totalRs.next();
	            totalOrders = totalRs.getInt("total_orders");
	        }

	        // Execute the main query
	        try (ResultSet rs = stmt.executeQuery()) {
	            List<Map<String, Object>> results = new ArrayList<>();
	            while (rs.next()) {
	                Map<String, Object> row = new HashMap<>();
	                String foodName = rs.getString("food_name");
	                int orderCount = rs.getInt("order_count");

	                // Calculate the percentage of this food item relative to the total orders
	                double percentage = (double) orderCount / totalOrders * 100;

	                row.put("foodName", foodName);
	                row.put("orderCount", orderCount);
	                row.put("percentage", String.format("%.2f", percentage) + "%");
	                row.put("totalOrders", totalOrders);
	                results.add(row);
	            }
	            return results;
	        }
	    }
	}


    // Method to get food-wise order count for a specific restaurant with optional month filter
    public List<Map<String, Object>> getFoodWiseOrderCountByRestaurant(String restaurantId, String month) throws SQLException {
        String query = "SELECT f.food_name, COUNT(oi.menu_item_id) AS order_count " +
                       "FROM OrderItems oi " +
                       "JOIN MenuItems mi ON oi.menu_item_id = mi.menu_item_id " +
                       "JOIN Foods f ON mi.food_id = f.food_id " +
                       "JOIN Orders o ON oi.order_id = o.order_id " +
                       "WHERE o.restaurant_id = ? " +
                       (month != null ? "AND DATE_FORMAT(o.order_date, '%Y-%m') = ? " : "") +
                       "GROUP BY f.food_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, Integer.parseInt(restaurantId));
            if (month != null) {
                stmt.setString(2, month);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("foodName", rs.getString("food_name"));
                    row.put("orderCount", rs.getInt("order_count"));
                    results.add(row);
                }
                return results;
            }
        }
    }

    // Method to get food-wise order count for a specific month
    public List<Map<String, Object>> getFoodWiseOrderCountByMonth(String month) throws SQLException {
        String query = "SELECT f.food_name, COUNT(oi.menu_item_id) AS order_count " +
                       "FROM OrderItems oi " +
                       "JOIN MenuItems mi ON oi.menu_item_id = mi.menu_item_id " +
                       "JOIN Foods f ON mi.food_id = f.food_id " +
                       "JOIN Orders o ON oi.order_id = o.order_id " +
                       "WHERE DATE_FORMAT(o.order_date, '%Y-%m') = ? " +
                       "GROUP BY f.food_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, month);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("foodName", rs.getString("food_name"));
                    row.put("orderCount", rs.getInt("order_count"));
                    results.add(row);
                }
                return results;
            }
        }
    }

    // Method to get food-wise order count for a specific date range, with optional filtering by restaurantId and userId
    public List<Map<String, Object>> getFoodWiseOrderCountByDateRange(LocalDate startDate, LocalDate endDate, String restaurantId, String userId) throws SQLException {
        String query = "SELECT f.food_name, COUNT(oi.menu_item_id) AS order_count " +
                       "FROM OrderItems oi " +
                       "JOIN MenuItems mi ON oi.menu_item_id = mi.menu_item_id " +
                       "JOIN Foods f ON mi.food_id = f.food_id " +
                       "JOIN Orders o ON oi.order_id = o.order_id " +
                       "WHERE o.order_date BETWEEN ? AND ? " +
                       (restaurantId != null ? "AND o.restaurant_id = ? " : "") +
                       (userId != null ? "AND o.user_id = ? " : "") +
                       "GROUP BY f.food_id, f.food_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            int paramIndex = 3;

            if (restaurantId != null) {
                stmt.setInt(paramIndex++, Integer.parseInt(restaurantId));
            }
            if (userId != null) {
                stmt.setInt(paramIndex++, Integer.parseInt(userId));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("foodName", rs.getString("food_name"));
                    row.put("orderCount", rs.getInt("order_count"));
                    results.add(row);
                }
                return results;
            }
        }
    }

    // Method to get location-wise food split, with optional filtering by date range
   
    public List<Map<String, Object>> getLocationWiseFoodSplit(LocalDate startDate, LocalDate endDate) throws SQLException {
        // Base query
        String query = "SELECT a.city, f.food_name, COUNT(*) AS count " +
                       "FROM Orders o " +
                       "JOIN OrderItems oi ON o.order_id = oi.order_id " +
                       "JOIN MenuItems mi ON oi.menu_item_id = mi.menu_item_id " +
                       "JOIN Foods f ON mi.food_id = f.food_id " +
                       "JOIN Addresses a ON o.delivery_address_id = a.address_id " +
                       "WHERE 1=1 ";

        // Append date range filter if provided
        if (startDate != null && endDate != null) {
            query += "AND o.order_date BETWEEN ? AND ? ";
        }

        // Group by city and food name
        query += "GROUP BY a.city, f.food_name " +
                 "ORDER BY a.city, f.food_name";

        //System.out.println("Executing query: " + query);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            if (startDate != null && endDate != null) {
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(startDate));
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(endDate));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                // Debugging output
                System.out.println("Query executed successfully.");

                // Process result set
                Map<String, Map<String, Integer>> locationFoodCounts = new HashMap<>();

                while (rs.next()) {
                    String city = rs.getString("city");
                    String foodName = rs.getString("food_name");
                    int count = rs.getInt("count");

                    locationFoodCounts.putIfAbsent(city, new HashMap<>());
                    Map<String, Integer> foodCounts = locationFoodCounts.get(city);
                    foodCounts.put(foodName, count);
                }

                // Prepare results for JSON response
                List<Map<String, Object>> results = new ArrayList<>();
                for (Map.Entry<String, Map<String, Integer>> entry : locationFoodCounts.entrySet()) {
                    String city = entry.getKey();
                    Map<String, Integer> foodCounts = entry.getValue();

                    Map<String, Object> cityData = new HashMap<>();
                    cityData.put("city", city);
                    cityData.put("foodCounts", foodCounts);
                    results.add(cityData);
                }
                
                System.out.println("Results: " + results);

                return results;
            }
        }
    }

}
