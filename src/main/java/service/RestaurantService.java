package service;

import model.Address;
import model.Restaurant;
import model.User;
import util.JsonUtil;
import dao.AddressDAO;
import dao.RestaurantDAO;
import dao.UserRoleDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RestaurantService {
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private AddressDAO addressDAO = new AddressDAO();
    private UserRoleDAO userRoleDAO = new UserRoleDAO();
    private JsonUtil jsonUtil = new JsonUtil();
    
   
    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	 HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("user_id");
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String restaurantName = jsonRequest.optString("restaurant_name");
        String contactNumber = jsonRequest.optString("contact_number");
        String openingTime = jsonRequest.optString("opening_time");
        String closingTime = jsonRequest.optString("closing_time");
        String addressLine1 = jsonRequest.optString("address_line1");
        String addressLine2 = jsonRequest.optString("address_line2");
        String city = jsonRequest.optString("city");
        String state = jsonRequest.optString("state");
        String country = jsonRequest.optString("country");
        String pincode = jsonRequest.optString("pincode");
    

        try {
        	if (!isAdmin(userId)) {
        		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admins only"));
                return;
            }
        	userId = jsonRequest.optInt("user_id", 0); // To store in address schema
            String restaurantStatus = null;
			Restaurant restaurant = new Restaurant(0, restaurantName, contactNumber, openingTime, closingTime,restaurantStatus);
            int restaurantId = restaurantDAO.addRestaurant(restaurant);
            
            Address address = new Address(0, addressLine1, addressLine2, city, state, country, pincode, userId, restaurantId);
            addressDAO.addAddress(address);

            // Create success response
            response.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_CREATED, "Restaurant successfully created");
            jsonUtil.sendResponse(response, jsonResponse);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating restaurant");
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handleGetRestaurantList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
                List<Restaurant> restaurants = restaurantDAO.getAllRestaurants();
                JSONArray restaurantsArray = new JSONArray();
                
                for (Restaurant restaurant : restaurants) {
                    JSONObject restaurantJson = new JSONObject(restaurant);

                    restaurantsArray.put(restaurantJson);
                }
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("restaurants", restaurantsArray);
                jsonUtil.sendResponse(response, jsonResponse);

            
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving restaurant");
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }
    
    public void handleGetRestaurantDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String restaurantIdParam = request.getParameter("restaurant_id");

        try {

                int restaurantId = Integer.parseInt(restaurantIdParam);
                Restaurant restaurant = restaurantDAO.getRestaurantById(restaurantId);
                if (restaurant != null) {
                    JSONObject restaurantJson = new JSONObject(restaurant);

                    // Get address for the specific restaurant
                    Address address = addressDAO.getAddressByRestaurantId(restaurantId);
                    if (address != null) {
                        JSONObject addressJson = new JSONObject(address);
                        restaurantJson.put("address", addressJson);
                    }

                    jsonUtil.sendResponse(response, restaurantJson);
                } else {
                	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Restaurant not found"));
                }
           
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving restaurant");
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }
    
    public void handleSearchUsers( HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String query = request.getParameter("query");
        List<Restaurant> restaurants = restaurantDAO.searchRestaurantByQuery(query);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (restaurants != null && !restaurants.isEmpty()) {


            JSONArray restaurantsArray = new JSONArray();
            
            for (Restaurant restaurant : restaurants) {
                JSONObject restaurantJson = new JSONObject(restaurant);

                restaurantsArray.put(restaurantJson);
            }
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("restaurants", restaurantsArray);
            jsonUtil.sendResponse(response, jsonResponse);
        } else {
        	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND,"Restaurant not found"));
        }
    }
    
    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	 HttpSession session = request.getSession(false);

        int userId = (int) session.getAttribute("user_id");
    	 try {
			if (!isAdmin(userId)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admins only"));
			     return;
			 }
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	 
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String restaurantIdParam = jsonRequest.optString("restaurant_id");
        String addressIdParam =jsonRequest.optString("address_id");
        

        if (restaurantIdParam == null) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Restaurant ID is required"));
            return;
        }

        int restaurantId;
        try {
            restaurantId = Integer.parseInt(restaurantIdParam);
        } catch (NumberFormatException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid Restaurant ID format"));
            return;
        }

        try {
            // Update Restaurant information
            Restaurant existingRestaurant = restaurantDAO.getRestaurantById(restaurantId);
            if (existingRestaurant == null) {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Restaurant not found"));
                return;
            }

            String restaurantName = jsonRequest.optString("restaurant_name", existingRestaurant.getRestaurantName());
            String contactNumber = jsonRequest.optString("contact_number", existingRestaurant.getContactNumber());
            String openingTime = jsonRequest.optString("opening_time", existingRestaurant.getOpeningTime());
            String closingTime = jsonRequest.optString("closing_time", existingRestaurant.getClosingTime());

            String restaurantStatus = jsonRequest.optString("restaurant_status", existingRestaurant.getRestaurantStatus());;
			Restaurant updatedRestaurant = new Restaurant(restaurantId, restaurantName, contactNumber, openingTime, closingTime,restaurantStatus );
            restaurantDAO.updateRestaurant(updatedRestaurant);

            // Update Address information
            
            if (!addressIdParam.isEmpty() ) {
            	System.out.println("hello");
            	System.out.println(addressIdParam);
            	AddressUpdateService  addressUpdateService = new  AddressUpdateService(addressDAO);
                 Address updatedAddress = addressUpdateService.updateAddress(jsonRequest, response);
                 if (updatedAddress != null) {
                     addressDAO.updateAddress(updatedAddress);
                 } else {
                	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                     jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Address not found"));
                     return;
                 }
            }
            response.setStatus(HttpServletResponse.SC_OK);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Restaurant and address updated successfully"));
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating restaurant or address"));
        }
    }
    
    private boolean isAdmin(int userId) throws SQLException {
        int userRoleId = userRoleDAO.getUserRoleByUserId(userId);
        return userRoleId == 2;
    }

}
