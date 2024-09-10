package dao;

import model.OrderItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseUtil;

public class OrderItemDAO {

    public void createOrderItem(OrderItem orderItem) throws SQLException {
        String sql = "INSERT INTO OrderItems (order_id, menu_item_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItem.getOrderId());
            stmt.setInt(2, orderItem.getMenuItemId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setDouble(4, orderItem.getPrice());
            stmt.setDouble(5, orderItem.getTotalPrice());

            stmt.executeUpdate();
        }
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException {
        String orderItemsQuery = "SELECT oi.menu_item_id, oi.quantity, oi.price, f.food_name " +
                                 "FROM OrderItems oi " +
                                 "JOIN MenuItems mi ON oi.menu_item_id = mi.menu_item_id " +
                                 "JOIN Foods f ON mi.food_id = f.food_id " +
                                 "WHERE oi.order_id = ?";
        
        List<OrderItem> orderItems = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement statement = conn.prepareStatement(orderItemsQuery)) {
            statement.setInt(1, orderId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setMenuItemId(rs.getInt("menu_item_id"));
                    orderItem.setQuantity(rs.getInt("quantity"));
                    orderItem.setPrice(rs.getDouble("price"));
                    orderItem.setFoodName(rs.getString("food_name")); // Set food name
                    orderItems.add(orderItem);
                }
            }
        }
        return orderItems;
    }

//    private OrderItem mapRowToOrderItem(ResultSet rs) throws SQLException {
//        return new OrderItem(
//            rs.getInt("order_item_id"), 
//            rs.getInt("order_id"),
//            rs.getInt("menu_item_id"),
//            rs.getInt("quantity"),
//            rs.getDouble("price"),
//            rs.getDouble("total_price")
//        );
//    }
}
