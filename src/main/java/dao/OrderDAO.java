package dao;

import model.Address;
import model.User;
import model.UserOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseUtil;

public class OrderDAO {

    public int createOrder(UserOrder order) {
        String sql = "INSERT INTO Orders (user_id, restaurant_id, total_amount, delivery_address_id, payment_status, delivery_status, payment_method_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, order.getUserId());
            stmt.setInt(2, order.getRestaurantId());
            stmt.setDouble(3, order.getTotalAmount());
            stmt.setInt(4, order.getDeliveryAddressId());
            stmt.setString(5, order.getPaymentStatus());
            stmt.setString(6, order.getDeliveryStatus());
            stmt.setInt(7, order.getPaymentMethodId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            // Log exception
            throw new RuntimeException("Database error occurred while creating order", e);
        }
    }

    public boolean updateDeliveryStatus(int orderId) {
        String sql = "UPDATE Orders SET delivery_status = 'Delivered' WHERE order_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            // Log exception
            throw new RuntimeException("Database error occurred while updating delivery status", e);
        }
    }
    
    public double getMenuItemPrice(int menuItemId) {
        String query = "SELECT price FROM MenuItems WHERE menu_item_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, menuItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
            }
        } catch (SQLException e) {
            // Log exception
            throw new RuntimeException("Database error occurred while retrieving menu item price", e);
        }
        return 0.0;
    }

    public int getRestaurantIdFromMenuItem(int menuItemId) {
        String query = "SELECT restaurant_id FROM MenuItems WHERE menu_item_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, menuItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("restaurant_id");
                }
            }
        } catch (SQLException e) {
            // Log exception
            throw new RuntimeException("Database error occurred while retrieving restaurant ID from menu item", e);
        }
        return -1;
    }

    public List<UserOrder> getAllOrders() {
    	String query = "SELECT o.order_id, u.user_id,o.total_amount, o.payment_status, o.delivery_status, o.order_date, " +
                "r.restaurant_name, a.address_line1, a.address_line2, a.city, a.state, a.pincode, " +
                "u.first_name, u.last_name " +
                "FROM Orders o " +
                "JOIN Restaurants r ON o.restaurant_id = r.restaurant_id " +
                "JOIN Addresses a ON o.delivery_address_id = a.address_id " +
                "JOIN Users u ON o.user_id = u.user_id";



        List<UserOrder> orders = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                UserOrder order = new UserOrder();
                order.setOrderId(rs.getInt("order_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setDeliveryStatus(rs.getString("delivery_status"));
                order.setRestaurantName(rs.getString("restaurant_name"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                // Create address from result set
                Address address = new Address();
                address.setAddressLine1(rs.getString("address_line1"));
                address.setAddressLine2(rs.getString("address_line2"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setPincode(rs.getString("pincode"));
                
                order.setRestaurantAddress(address);
                User user = new User();
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setUserId(rs.getInt("user_id"));
                
                order.setUser(user);
                
                orders.add(order);
            }
        } catch (SQLException e) {
            // Log exception
            throw new RuntimeException("Database error occurred while retrieving all orders", e);
        }
        return orders;
    }

    public List<UserOrder> getOrdersByUserId(int userId) {
        String query = "SELECT o.order_id, o.total_amount, o.payment_status, o.delivery_status, " +
                       "o.order_date, r.restaurant_name, a.address_line1, a.address_line2, a.city, a.state, a.pincode, " +
                       "u.first_name, u.last_name " +  
                       "FROM Orders o " +
                       "JOIN Restaurants r ON o.restaurant_id = r.restaurant_id " +
                       "JOIN Addresses a ON o.delivery_address_id = a.address_id " +
                       "JOIN Users u ON o.user_id = u.user_id " +  
                       "WHERE o.user_id = ?";

        List<UserOrder> orders = new ArrayList<>();
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
      
            while (rs.next()) {
                UserOrder order = new UserOrder();
                order.setOrderId(rs.getInt("order_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setDeliveryStatus(rs.getString("delivery_status"));
                order.setRestaurantName(rs.getString("restaurant_name"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                
                // Create address from result set
                Address address = new Address();
                address.setAddressLine1(rs.getString("address_line1"));
                address.setAddressLine2(rs.getString("address_line2"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setPincode(rs.getString("pincode"));
                
                order.setRestaurantAddress(address);
                
                // Create user from result set
                User user = new User();
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                
                order.setUser(user);
                
                orders.add(order);
            }
        } catch (SQLException e) {
            // Log exception
            throw new RuntimeException("Database error occurred while retrieving orders for user", e);
        }
        return orders;
    }

    public UserOrder getOrderById(int orderId) {
        String orderQuery = "SELECT o.order_id, o.user_id, o.total_amount, o.payment_status, o.order_date, o.delivery_status, " +
                            "r.restaurant_name, r.restaurant_status, a.address_line1, a.address_line2, a.city, a.state, a.pincode, " +
                            "u.first_name, u.last_name " +  // Add these fields to the select clause
                            "FROM Orders o " +
                            "JOIN Restaurants r ON o.restaurant_id = r.restaurant_id " +
                            "JOIN Addresses a ON o.delivery_address_id = a.address_id " +
                            "JOIN Users u ON o.user_id = u.user_id " +  // Join with Users table
                            "WHERE o.order_id = ?";
        
        UserOrder order = null;
        
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement orderStatement = connection.prepareStatement(orderQuery)) {
            
            // Fetch order details
            orderStatement.setInt(1, orderId);
            try (ResultSet rs = orderStatement.executeQuery()) {
                if (rs.next()) {
                    order = new UserOrder();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setTotalAmount(rs.getDouble("total_amount"));
                    order.setPaymentStatus(rs.getString("payment_status"));
                    order.setDeliveryStatus(rs.getString("delivery_status"));
                    order.setRestaurantName(rs.getString("restaurant_name"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setUserId(rs.getInt("user_id"));
                    
                    // Create address from result set
                    Address address = new Address();
                    address.setAddressLine1(rs.getString("address_line1"));
                    address.setAddressLine2(rs.getString("address_line2"));
                    address.setCity(rs.getString("city"));
                    address.setState(rs.getString("state"));
                    address.setPincode(rs.getString("pincode"));
                    
//                    // Create user from result set
                    User user = new User();
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    
                    order.setUser(user);
                    order.setRestaurantAddress(address);
                }
            }
        } catch (SQLException e) {
            // Log exception
            throw new RuntimeException("Database error occurred while retrieving order by ID", e);
        }
        return order;
    }

}
