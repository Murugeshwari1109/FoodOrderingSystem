package model;

public class MenuItem {
    private int menuItemId;
    private int restaurantId;
    private transient int foodId; // This field will not be serialized
    private String foodName;
    private double price;
    private boolean available;

    public MenuItem(int menuItemId, int restaurantId, int foodId, String foodName, double price, boolean available) {
        this.menuItemId = menuItemId;
        this.restaurantId = restaurantId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.price = price;
        this.available = available;
    }

    // Getters and setters
    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
