package dao;

import model.User;
import database.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    /*public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users ORDER BY user_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String contactNumber = rs.getString("contact_number");
                int userRoleId = rs.getInt("user_role_id");

                User user = new User(userId, firstName, lastName, email, contactNumber, userRoleId);
                users.add(user);
            }
        }
        return users;
    }*/
	
	public List<User> searchUsersByQuery(String query) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE first_name LIKE ?";

        try (Connection conn = DatabaseUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, query + "%"); // Search for usernames starting with the query

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                	User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("contact_number"),
                            rs.getInt("user_role_id")
                        );
                        users.add(user);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }

        return users;
    }
	
	public List<User> getUsers(int pageNumber, int pageSize) throws SQLException {
        List<User> users = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT * FROM Users ORDER BY user_id LIMIT ? OFFSET ?";
        
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("contact_number"),
                    rs.getInt("user_role_id")
                );
                users.add(user);
            }
        }
        return users;
    }

    // Method to get the total number of users
    public int getTotalUserCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM Users";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Extract user details from ResultSet
                    return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("contact_number"),
                        resultSet.getInt("user_role_id")
               
                    );
                } else {
                    // User not found
                    return null;
                }
            }
        }
    }
    
    public Map<String, Object> getUserDetailsById(int userId) throws SQLException {
        String query = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Create a map to store user details
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("user_id", resultSet.getInt("user_id"));
                    userMap.put("first_name", resultSet.getString("first_name"));
                    userMap.put("last_name", resultSet.getString("last_name"));
                    userMap.put("email", resultSet.getString("email"));
                    userMap.put("contact_number", resultSet.getString("contact_number"));
                    
                    return userMap; // Return the map
                } else {
                    // User not found
                    return null;
                }
            }
        }
    }

    public int addUser(User user) throws SQLException {
        String query = "INSERT INTO Users (first_name, last_name, email, contact_number, user_role_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getContactNumber());
            pstmt.setInt(5, user.getUserRoleId());
            //pstmt.setInt(6, user.getAddressId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1; // Indicates failure
    }

    public void updateUser(User user) throws SQLException {
        String query = "UPDATE Users SET first_name = ?, last_name = ?, email = ?, contact_number = ?, user_role_id = ? WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getContactNumber());
            pstmt.setInt(5, user.getUserRoleId());
            //pstmt.setInt(6, user.getAddressId());
            pstmt.setInt(6, user.getUserId());

            pstmt.executeUpdate();
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
