package model;

import org.json.JSONObject;

public class UserCredentials {
    private int userId;
    private String username;
    private String password;

    public UserCredentials(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

  
    @Override
    public String toString() {
        return "UserCredentials{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                '}';
    }


    // Method to convert UserCredentials to JSON without password
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("username", username);
        // Do not include password
        return json;
    }
}
