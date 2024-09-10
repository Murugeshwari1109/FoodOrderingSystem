package model;

import org.json.JSONObject;

public class Address {
    private int addressId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private int userId;
    private int restaurantId;  

    public Address(int addressId, String addressLine1, String addressLine2, String city, String state, String country, String pincode, int userId, int restaurantId) {
        this.addressId = addressId;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

	public Address() {
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    // Method to convert Address to JSON
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("addressId", addressId);
        json.put("addressLine1", addressLine1);
        json.put("addressLine2", addressLine2);
        json.put("city", city);
        json.put("state", state);
        json.put("country", country);
        json.put("pincode", pincode);
        json.put("userId", userId);
        if (restaurantId != 0) {
            json.put("restaurantId", restaurantId);
        }
        return json;
    }

}
