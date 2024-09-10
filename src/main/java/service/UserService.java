package service;

import model.User;
import model.UserCredentials;
import util.JsonUtil;
import model.Address;
import dao.UserDAO;
import dao.UserCredentialsDAO;
import dao.AddressDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO = new UserDAO();
    private UserCredentialsDAO userCredentialsDAO = new UserCredentialsDAO();
    private AddressDAO addressDAO = new AddressDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String email = jsonRequest.optString("email");
        String firstName = jsonRequest.optString("first_name");
        String lastName = jsonRequest.optString("last_name");
        String contactNumber = jsonRequest.optString("contact_number");
        String addressLine1 = jsonRequest.optString("address_line1");
        String addressLine2 = jsonRequest.optString("address_line2");
        String city = jsonRequest.optString("city");
        String state = jsonRequest.optString("state");
        String country = jsonRequest.optString("country");
        String pincode = jsonRequest.optString("pincode");
        String userName = jsonRequest.optString("user_name");
        String password = jsonRequest.optString("password");
        int restaurantId = jsonRequest.optInt("restaurant_id", 0); 

        User user = new User(0, firstName, lastName, email, contactNumber, 1); 

        try {
            int userId = userDAO.addUser(user);

            UserCredentials userCredentials = new UserCredentials(userId, userName, password);
            userCredentialsDAO.addUserCredentials(userCredentials);

            // Create Address with the generated userId
            Address address = new Address(0, addressLine1, addressLine2, city, state, country, pincode, userId, restaurantId);
            addressDAO.addAddress(address);

            // Create success response
            response.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_CREATED, "User successfully created");
            jsonUtil.sendResponse(response, jsonResponse);

        } catch (SQLException e) {
            e.printStackTrace(); 
            String errorMessage = e.getMessage();
            int errorCode = e.getErrorCode(); 
            JSONObject jsonResponse = jsonUtil.createStatusResponse(errorCode, errorMessage);
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

   /* public void handleGetUserList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
                List<User> users = userDAO.getAllUsers();
                JSONArray usersArray = new JSONArray();
                for (User user : users) {
                    JSONObject userJson = new JSONObject(user);
                    usersArray.put(userJson);
                }
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("users", usersArray);
                JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                jsonUtil.sendResponse(response, snakeCaseResponse);

            
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); 
            String errorMessage = e.getMessage();
            int errorCode = ((SQLException) e).getErrorCode(); 
            JSONObject jsonResponse = jsonUtil.createStatusResponse(errorCode, errorMessage);
            //JSONObject jsonResponse = jsonUtil.createStatusResponse(false, "Error retrieving user");
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);
        }
    }*/
    
    public void handleSearchUsers( HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String query = request.getParameter("query");
        List<User> users = userDAO.searchUsersByQuery(query);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (users != null && !users.isEmpty()) {
        	JSONArray usersArray = new JSONArray();
            for (User user : users) {
                JSONObject userJson = new JSONObject();
                userJson.put("user_id", user.getUserId());
                userJson.put("first_name", user.getFirstName());
                userJson.put("last_name", user.getLastName());
                userJson.put("email", user.getEmail());
                userJson.put("contact_number", user.getContactNumber());
                userJson.put("user_role_id", user.getUserRoleId());
                usersArray.put(userJson);
                
            }
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("users", usersArray);
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse); // Convert to snake_case if needed
            jsonUtil.sendResponse(response, snakeCaseResponse);
        } else {
        	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND,"User not found"));
        }
    }
   
    public void handleGetUserList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int pageNumber = 1; 
        int pageSize = 5;  

        try {
            // Read page number and page size from request parameters
            String pageNumberParam = request.getParameter("page");
            String pageSizeParam = request.getParameter("size");

            if (pageNumberParam != null) {
                pageNumber = Integer.parseInt(pageNumberParam);
            }
            if (pageSizeParam != null) {
                pageSize = Integer.parseInt(pageSizeParam);
            }

            // Fetch paginated user list
            List<User> users = userDAO.getUsers(pageNumber, pageSize);
            int totalUserCount = userDAO.getTotalUserCount();
            int totalPages = (int) Math.ceil((double) totalUserCount / pageSize);

            JSONArray usersArray = new JSONArray();
            for (User user : users) {
                JSONObject userJson = new JSONObject();
                userJson.put("user_id", user.getUserId());
                userJson.put("first_name", user.getFirstName());
                userJson.put("last_name", user.getLastName());
                userJson.put("email", user.getEmail());
                userJson.put("contact_number", user.getContactNumber());
                userJson.put("user_role_id", user.getUserRoleId());
                usersArray.put(userJson);
            }

            // Build JSON response
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("users", usersArray);
            jsonResponse.put("page_number", pageNumber);
            jsonResponse.put("page_size", pageSize);
            jsonResponse.put("total_pages", totalPages);
            jsonResponse.put("total_users", totalUserCount);

            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse); // Convert to snake_case if needed
            jsonUtil.sendResponse(response, snakeCaseResponse);

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            int errorCode = (e instanceof SQLException) ? ((SQLException) e).getErrorCode() : 500; // Use 500 for generic exceptions
            JSONObject jsonResponse = jsonUtil.createStatusResponse(errorCode, errorMessage);
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);
        }
    }
    
    public void handleGetUserDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String userIdParam = request.getParameter("user_id");

        try {
                int userId = Integer.parseInt(userIdParam);
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    UserCredentials userCredentials = userCredentialsDAO.getUserCredentialsByUserId(userId);
                    Address address = addressDAO.getAddressByUserId(userId);
                    if (userCredentials != null && address != null) {
                        user.setUserCredentials(userCredentials);
                        user.setAddress(address);
                        JSONObject userJson = new JSONObject(user);
                        JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(userJson);
                        jsonUtil.sendResponse(response, snakeCaseResponse);
                    } else {
                    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        JSONObject jsonResponse =jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "User details not found");
                        JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                        jsonUtil.sendResponse(response, snakeCaseResponse);
                    }
                } else {
                	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                    jsonUtil.sendResponse(response, snakeCaseResponse);
                }
            
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving user");
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);
        }
    }
    
    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);

        String userIdParam = jsonRequest.optString("user_id");
        String addressIdParam =jsonRequest.optString("address_id");

        if (userIdParam == null) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User ID are required"));
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid User ID format"));
            return;
        }

        try {
            User existingUser = userDAO.getUserById(userId);
            if (existingUser == null) {
            	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND,"User not found"));
                return;
            }

            String firstName = jsonRequest.optString("first_name", existingUser.getFirstName());
            String lastName = jsonRequest.optString("last_name", existingUser.getLastName());
            String email = jsonRequest.optString("email", existingUser.getEmail());
            String contactNumber = jsonRequest.optString("contact_number", existingUser.getContactNumber());
            String username = jsonRequest.optString("user_name", null);
            String password = jsonRequest.optString("password", null);

            User updatedUser = new User(userId, firstName, lastName, email, contactNumber, existingUser.getUserRoleId());
            userDAO.updateUser(updatedUser);

            if (username != null || password != null) {
                UserCredentials existingCredentials = userCredentialsDAO.getUserCredentialsByUserId(userId);
                if (existingCredentials != null) {
                    UserCredentials updatedCredentials = new UserCredentials(userId,
                        username != null ? username : existingCredentials.getUsername(),
                        password != null ? password : existingCredentials.getPassword()
                    );
                    userCredentialsDAO.updateUserCredentials(updatedCredentials);
                }
            }
            
            
            
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
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "User and address updated successfully"));
        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating user or address"));
        }
    }
}
