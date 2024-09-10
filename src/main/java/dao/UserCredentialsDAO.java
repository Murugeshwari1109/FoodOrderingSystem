package dao;

import model.UserCredentials;
import database.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCredentialsDAO {

    // Method to add user credentials
    public void addUserCredentials(UserCredentials userCredentials) throws SQLException {
        String query = "INSERT INTO UserCredentials (user_id, user_name, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userCredentials.getUserId());
            stmt.setString(2, userCredentials.getUsername());
            stmt.setString(3, userCredentials.getPassword());
            stmt.executeUpdate();
        }
    }

    // Method to update user credentials
    public void updateUserCredentials(UserCredentials userCredentials) throws SQLException {
        String query = "UPDATE UserCredentials SET user_name = ?, password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userCredentials.getUsername());
            stmt.setString(2, userCredentials.getPassword());
            stmt.setInt(3, userCredentials.getUserId());
            stmt.executeUpdate();
        }
    }

    // Method to delete user credentials
    public void deleteUserCredentials(int userId) throws SQLException {
        String query = "DELETE FROM UserCredentials WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    // Method to get user credentials by user ID
    public UserCredentials getUserCredentialsByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM UserCredentials WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserCredentials(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("password")
                    );
                } else {
                    return null;
                }
            }
        }
    }
}
