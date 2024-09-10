package dao;

import model.CartItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseUtil;

public class CartItemDAO {
    private Connection getConnection() throws SQLException {
        return DatabaseUtil.getConnection();
    }

    public CartItem getCartItem(int cartId, int menuItemId) throws SQLException {
        String query = "SELECT * FROM CartItems WHERE cart_id = ? AND menu_item_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cartId);
            statement.setInt(2, menuItemId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new CartItem(
                    resultSet.getInt("cart_item_id"),
                    resultSet.getInt("cart_id"),
                    resultSet.getInt("menu_item_id"),
                    resultSet.getInt("quantity")
                );
            }
        }
        return null;
    }

    public void addCartItem(CartItem cartItem) throws SQLException {
        String query = "INSERT INTO CartItems (cart_id, menu_item_id, quantity) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cartItem.getCartId());
            statement.setInt(2, cartItem.getMenuItemId());
            statement.setInt(3, cartItem.getQuantity());
            statement.executeUpdate();
        }
    }

    public void updateCartItem(CartItem cartItem) throws SQLException {
        String query = "UPDATE CartItems SET quantity = ? WHERE cart_item_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cartItem.getQuantity());
            statement.setInt(2, cartItem.getCartItemId());
            statement.executeUpdate();
        }
    }

    public void deleteCartItem(int cartItemId) throws SQLException {
        String query = "DELETE FROM CartItems WHERE cart_item_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cartItemId);
            statement.executeUpdate();
        }
    }

    public List<CartItem> getCartItemsByCartId(int cartId) throws SQLException {
        String query = "SELECT * FROM CartItems WHERE cart_id = ?";
        List<CartItem> cartItems = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cartId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cartItems.add(new CartItem(
                    resultSet.getInt("cart_item_id"),
                    resultSet.getInt("cart_id"),
                    resultSet.getInt("menu_item_id"),
                    resultSet.getInt("quantity")
                ));
            }
        }
        return cartItems;
    }
}
