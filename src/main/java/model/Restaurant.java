package model;

public class Restaurant {
    private int restaurant_id;
    private String restaurantName;
    private String contactNumber;
    private String openingTime;
    private String closingTime;
    private String restaurantStatus;

	public Restaurant(int restaurantId, String restaurantName, String contactNumber, String openingTime, String closingTime,String restaurantStatus) {
        this.restaurant_id = restaurantId;
        this.restaurantName = restaurantName;
        this.contactNumber = contactNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.restaurantStatus = restaurantStatus;
    }

    public int getRestaurantId() {
        return restaurant_id;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurant_id = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }
    
    public String getRestaurantStatus() {
		return restaurantStatus;
	}

	public void setRestaurantStatus(String restaurantStatus) {
		this.restaurantStatus = restaurantStatus;
	}
}
