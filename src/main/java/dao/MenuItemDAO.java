package dao;

import model.MenuItem;
import database.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    public int addMenuItem(MenuItem menuItem) throws SQLException {
        String sql = "INSERT INTO MenuItems (restaurant_id, food_id, price, available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, menuItem.getRestaurantId());
            stmt.setInt(2, menuItem.getFoodId()); // Include food_id here
            stmt.setDouble(3, menuItem.getPrice());
            stmt.setBoolean(4, menuItem.isAvailable());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<MenuItem> getAllMenuItems() throws SQLException {
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT MenuItems.*, Foods.food_name FROM MenuItems JOIN Foods ON MenuItems.food_id = Foods.food_id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MenuItem menuItem = new MenuItem(
                        rs.getInt("menu_item_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("food_id"), 
                        rs.getString("food_name"),
                        rs.getDouble("price"),
                        rs.getBoolean("available")
                );
                menuItems.add(menuItem);
            }
        }
        return menuItems;
    }

    public List<MenuItem> getMenuItemsByRestaurantId(int restaurantId) throws SQLException {
        List<MenuItem> menuItems = new ArrayList<>();
        String query = "SELECT MenuItems.*, Foods.food_name FROM MenuItems JOIN Foods ON MenuItems.food_id = Foods.food_id WHERE restaurant_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, restaurantId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MenuItem menuItem = new MenuItem(
                        rs.getInt("menu_item_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("food_id"), // This field is needed for internal processing
                        rs.getString("food_name"),
                        rs.getDouble("price"),
                        rs.getBoolean("available")
                );
                menuItems.add(menuItem);
            }
        }
        return menuItems;
    }

    public MenuItem getMenuItemById(int menuItemId) throws SQLException {
        String sql = "SELECT MenuItems.*, Foods.food_name FROM MenuItems JOIN Foods ON MenuItems.food_id = Foods.food_id WHERE menu_item_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, menuItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MenuItem(
                            rs.getInt("menu_item_id"),
                            rs.getInt("restaurant_id"),
                            rs.getInt("food_id"), 
                            rs.getString("food_name"),
                            rs.getDouble("price"),
                            rs.getBoolean("available")
                    );
                }
            }
        }
        return null;
    }

    public void updateMenuItem(MenuItem menuItem) throws SQLException {
        String sql = "UPDATE MenuItems SET restaurant_id = ?, food_id = ?, price = ?, available = ? WHERE menu_item_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, menuItem.getRestaurantId());
            stmt.setInt(2, menuItem.getFoodId()); // Include food_id here
            stmt.setDouble(3, menuItem.getPrice());
            stmt.setBoolean(4, menuItem.isAvailable());
            stmt.setInt(5, menuItem.getMenuItemId());
            stmt.executeUpdate();
        }
    }

    public void deleteMenuItem(int menuItemId) throws SQLException {
        String sql = "DELETE FROM MenuItems WHERE menu_item_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, menuItemId);
            stmt.executeUpdate();
        }
    }
}
