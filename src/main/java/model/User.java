package model;

import org.json.JSONObject;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private int userRoleId;
  
    private Address address;
    private UserCredentials userCredentials;

    public User(int userId, String firstName, String lastName, String email, String contactNumber, int userRoleId) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.userRoleId = userRoleId;
   
    }

    public User() {
		// TODO Auto-generated constructor stub
	}

	// Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getUserRoleId() { return userRoleId; }
    public void setUserRoleId(int userRoleId) { this.userRoleId = userRoleId; }



    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public UserCredentials getUserCredentials() { return userCredentials; }
    public void setUserCredentials(UserCredentials userCredentials) { this.userCredentials = userCredentials; }

    // Method to convert User to JSON excluding sensitive data
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("firstName", firstName);
        json.put("lastName", lastName);
        json.put("email", email);
        json.put("contactNumber", contactNumber);
        json.put("userRoleId", userRoleId);
    

        if (address != null) {
            json.put("address", address.toJson());
        }
        
        return json;
    }
}
