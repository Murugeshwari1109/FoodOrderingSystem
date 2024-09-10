package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DatabaseUtil;

public class UserRoleDAO {

    public int getUserRoleByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM UserRoles ur JOIN Users u ON ur.user_role_id = u.user_role_id WHERE u.user_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_role_id");
                }
            }
        }
        return -1;
    }
    
    public boolean updateUserRole(int userId, int newRoleId) throws SQLException {
        String sql = "UPDATE Users SET user_role_id = ? WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, newRoleId);
            stmt.setInt(2, userId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
