package model;
import java.sql.Timestamp;
import java.util.List;

public class UserOrder {
    
    private int orderId;
    private int userId;
    private int restaurantId;
    private Timestamp orderDate;
    private double totalAmount;
    private int deliveryAddressId;
    private String paymentStatus;
    private String deliveryStatus;
    private int paymentMethodId;
    private String restaurantName;
    private String restaurantStatus;
    private Address restaurantAddress;
    private User user;
    private List<OrderItem> orderItems;

    // Constructor with all fields except orderId and orderDate (for creation)
    public UserOrder(int userId, int restaurantId, double totalAmount, int deliveryAddressId) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.deliveryAddressId = deliveryAddressId;
        this.paymentStatus = "Pending";  
        this.deliveryStatus = "Pending"; 
        this.orderDate = new Timestamp(System.currentTimeMillis()); 
    }

    // Constructor for setting all fields
    public UserOrder(int orderId, int userId, int restaurantId, Timestamp orderDate, double totalAmount, int deliveryAddressId, String paymentStatus, String deliveryStatus, int paymentMethodId) {
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.deliveryAddressId = deliveryAddressId;
        this.paymentStatus = paymentStatus;
        this.deliveryStatus = deliveryStatus;
        this.paymentMethodId = paymentMethodId;
    }
    
    // Constructor with restaurant details
    public UserOrder(int orderId, int userId, int restaurantId, String restaurantStatus, String restaurantName, Address restaurantAddress, Timestamp orderDate, double totalAmount, int deliveryAddressId, String paymentStatus, String deliveryStatus, int paymentMethodId) {
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantStatus = restaurantStatus;
        this.restaurantAddress = restaurantAddress;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.deliveryAddressId = deliveryAddressId;
        this.paymentStatus = paymentStatus;
        this.deliveryStatus = deliveryStatus;
        this.paymentMethodId = paymentMethodId;
    }

    public UserOrder() {
        // Default constructor
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(int deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantStatus() {
        return restaurantStatus;
    }
    

    public void setRestaurantStatus(String restaurantStatus) {
        this.restaurantStatus = restaurantStatus;
    }

    public Address getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(Address restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
