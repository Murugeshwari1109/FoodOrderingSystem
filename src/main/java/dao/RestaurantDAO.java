package dao;

import model.Restaurant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseUtil;

public class RestaurantDAO {
   

	public int addRestaurant(Restaurant restaurant) throws SQLException {
	    String sql = "INSERT INTO Restaurants (restaurant_name, contact_number, opening_time, closing_time) VALUES (?, ?, ?, ?)";
	    try (Connection conn = DatabaseUtil.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        stmt.setString(1, restaurant.getRestaurantName());
	        stmt.setString(2, restaurant.getContactNumber());
	        stmt.setString(3, restaurant.getOpeningTime());
	        stmt.setString(4, restaurant.getClosingTime());
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

    public List<Restaurant> getAllRestaurants() throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM Restaurants where restaurant_status = 'active'";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Restaurant restaurant = new Restaurant(
                        rs.getInt("restaurant_id"),
                        rs.getString("restaurant_name"),
                        rs.getString("contact_number"),
                        rs.getString("opening_time"),
                        rs.getString("closing_time"),
                        rs.getString("restaurant_status")
                );
                restaurants.add(restaurant);
            }
        }
        return restaurants;
    }

    public Restaurant getRestaurantById(int restaurantId) throws SQLException {
        String sql = "SELECT * FROM Restaurants WHERE restaurant_id = ? and restaurant_status = 'active'";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Restaurant(
                            rs.getInt("restaurant_id"),
                            rs.getString("restaurant_name"),
                            rs.getString("contact_number"),
                            rs.getString("opening_time"),
                            rs.getString("closing_time"),
                            rs.getString("restaurant_status")
                    );
                }
            }
        }
        return null;
    }
    
    public List<Restaurant> searchRestaurantByQuery(String query) {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM Restaurants WHERE restaurant_name LIKE ?";

        try (Connection conn = DatabaseUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%"); // Search for usernames starting with the query

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                	Restaurant restaurant = new Restaurant(
                			 rs.getInt("restaurant_id"),
                             rs.getString("restaurant_name"),
                             rs.getString("contact_number"),
                             rs.getString("opening_time"),
                             rs.getString("closing_time"),
                             rs.getString("restaurant_status")
                        );
                        restaurants.add(restaurant);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

        return restaurants;
    }
    
    public String getRestaurantByNameId(int restaurantId) throws SQLException {
        String sql = "SELECT restaurant_name FROM Restaurants WHERE restaurant_id = ? AND restaurant_status = 'active'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, restaurantId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("restaurant_name");
                } else {
                    return null;
                }
            }
        }
    }

    public void updateRestaurant(Restaurant restaurant) throws SQLException {
        String sql = "UPDATE Restaurants SET restaurant_name = ?, contact_number = ?, opening_time = ?, closing_time = ? WHERE restaurant_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurant.getRestaurantName());
            stmt.setString(2, restaurant.getContactNumber());
            stmt.setString(3, restaurant.getOpeningTime());
            stmt.setString(4, restaurant.getClosingTime());
            stmt.setInt(5, restaurant.getRestaurantId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteRestaurant(int restaurantId) throws SQLException {
        String sql = "DELETE FROM Restaurants WHERE restaurant_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, restaurantId);
            stmt.executeUpdate();
        }
    }
}
