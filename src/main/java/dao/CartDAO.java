package dao;

import model.Cart;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseUtil;

public class CartDAO {
    private Connection getConnection() throws SQLException {
        return DatabaseUtil.getConnection();
    }

    public Cart getCartByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM Cart WHERE user_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Cart(
                    resultSet.getInt("cart_id"),
                    resultSet.getInt("user_id")
                );
            }
        }
        return null;
    }

    public int addCart(Cart cart) throws SQLException {
        String query = "INSERT INTO Cart (user_id) VALUES (?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, cart.getUserId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        throw new SQLException("Failed to create cart.");
    }
    
    public List<Cart> getAllCarts() throws SQLException {
        List<Cart> carts = new ArrayList<>();
        String sql = "SELECT * FROM Cart"; // Adjust the query based on your actual table name
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int cartId = rs.getInt("cart_id");
                int userId = rs.getInt("user_id");
                carts.add(new Cart(cartId, userId));
            }
        }
        return carts;
    }

}
