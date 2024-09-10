package dao;

import model.Address;
import database.DatabaseUtil;

import java.sql.*;

public class AddressDAO {

    // Method to add a new address
    public int addAddress(Address address) throws SQLException {
        String insertQuery = "INSERT INTO Addresses (address_line1, address_line2, city, state, country, pincode, user_id, restaurant_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set parameters for the new address
            insertStmt.setString(1, address.getAddressLine1());
            insertStmt.setString(2, address.getAddressLine2());
            insertStmt.setString(3, address.getCity());
            insertStmt.setString(4, address.getState());
            insertStmt.setString(5, address.getCountry());
            insertStmt.setString(6, address.getPincode());
            insertStmt.setInt(7, address.getUserId());
            insertStmt.setInt(8, address.getRestaurantId()); // Set restaurant_id if provided

            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating address failed, no rows affected.");
            }

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated address ID
                } else {
                    throw new SQLException("Creating address failed, no ID obtained.");
                }
            }
        }
    }
  
    public Address getAddressByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM Addresses WHERE user_id = ? ";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
        
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Address(
                        rs.getInt("address_id"),
                        rs.getString("address_line1"),
                        rs.getString("address_line2"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("country"),
                        rs.getString("pincode"),
                        rs.getInt("user_id"),
                        rs.getInt("restaurant_id") // Retrieve restaurant ID if available
                    );
                } else {
                    return null;
                }
            }
        }
    }
    
    public Address getAddressByRestaurantId(int restaurantId) throws SQLException {
        String query = "SELECT * FROM Addresses WHERE restaurant_id = ? ";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, restaurantId);
        
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Address(
                        rs.getInt("address_id"),
                        rs.getString("address_line1"),
                        rs.getString("address_line2"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("country"),
                        rs.getString("pincode"),
                        rs.getInt("user_id"),
                        rs.getInt("restaurant_id") 
                    );
                } else {
                    return null;
                }
            }
        }
    }
    
    public Address getAddressByUserIdAndAddressId(int userId, int addressId) throws SQLException {
        String query = "SELECT * FROM Addresses WHERE user_id = ? AND address_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, addressId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Address(
                        rs.getInt("address_id"),
                        rs.getString("address_line1"),
                        rs.getString("address_line2"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("country"),
                        rs.getString("pincode"),
                        rs.getInt("user_id"),
                        rs.getInt("restaurant_id")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    public void updateAddress(Address address) throws SQLException {
        String query = "UPDATE Addresses SET address_line1 = ?, address_line2 = ?, city = ?, state = ?, country = ?, pincode = ? WHERE address_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, address.getAddressLine1());
            stmt.setString(2, address.getAddressLine2());
            stmt.setString(3, address.getCity());
            stmt.setString(4, address.getState());
            stmt.setString(5, address.getCountry());
            stmt.setString(6, address.getPincode());
            stmt.setInt(7, address.getAddressId());

            stmt.executeUpdate();
        }
    }

    public void deleteAddress(int addressId) throws SQLException {
        String sql = "DELETE FROM Addresses WHERE address_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql))  {
            stmt.setInt(1, addressId);
            stmt.executeUpdate();
        }
    }
}
