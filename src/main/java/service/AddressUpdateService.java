package service;

import model.Address;
import dao.AddressDAO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONObject;

public class AddressUpdateService {
    private AddressDAO addressDAO;

    public AddressUpdateService(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }

    public Address updateAddress(JSONObject jsonRequest,HttpServletResponse response) throws SQLException, IOException {
    	String userIdParam = jsonRequest.optString("user_id");
        String addressIdParam =jsonRequest.optString("address_id");
        String restaurantIdParam = jsonRequest.optString("restaurant_id");
        
        int userId = 0;
        int addressId = 0;
        int restaurantId = 0;
        Address existingAddress = null;
        if(userIdParam!=null) {
        	userId = Integer.parseInt(userIdParam);
        	addressId = Integer.parseInt(addressIdParam);
        	 existingAddress = addressDAO.getAddressByUserIdAndAddressId(userId, addressId);
        }
        else if(restaurantIdParam!=null) {
        	restaurantId = Integer.parseInt(restaurantIdParam);
        	addressId = Integer.parseInt(addressIdParam);
            existingAddress = addressDAO.getAddressByRestaurantId(restaurantId);
        }
      
        String addressLine1 = jsonRequest.optString("address_line1", null);
        String addressLine2 = jsonRequest.optString("address_line2", null);
        String city = jsonRequest.optString("city", null);
        String state = jsonRequest.optString("state", null);
        String country = jsonRequest.optString("country", null);
        String pincode = jsonRequest.optString("pincode", null);

       
        if (existingAddress != null) {
            // Update fields only if they are provided in the request
            String updatedAddressLine1 = addressLine1 != null ? addressLine1 : existingAddress.getAddressLine1();
            String updatedAddressLine2 = addressLine2 != null ? addressLine2 : existingAddress.getAddressLine2();
            String updatedCity = city != null ? city : existingAddress.getCity();
            String updatedState = state != null ? state : existingAddress.getState();
            String updatedCountry = country != null ? country : existingAddress.getCountry();
            String updatedPincode = pincode != null ? pincode : existingAddress.getPincode();
            int userID = userIdParam!=null ? userId: existingAddress.getUserId();
            int restaurantID =  restaurantIdParam!=null ? restaurantId :existingAddress.getRestaurantId();

            return new Address(
                addressId, 
                updatedAddressLine1, 
                updatedAddressLine2, 
                updatedCity, 
                updatedState, 
                updatedCountry, 
                updatedPincode, 
                userID,
                restaurantID
            );
        }
        return null; // Address not found
    }
}
