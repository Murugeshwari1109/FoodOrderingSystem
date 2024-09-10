package service;

import model.MenuItem;
import util.JsonUtil;
import dao.MenuItemDAO;
import dao.RestaurantDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MenuItemService {
    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private JsonUtil jsonUtil = new JsonUtil();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        int restaurantId = jsonRequest.optInt("restaurant_id");
        int foodId = jsonRequest.optInt("food_id");
        double price = jsonRequest.optDouble("price");
        boolean available = jsonRequest.optBoolean("available");
        
        HttpSession session = request.getSession(false);
 
        int userRoleId = (int) session.getAttribute("user_role_id");

        try {
            if (userRoleId != 4) {
            	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Restaurant Admin only"));
                return;
            }
            MenuItem menuItem = new MenuItem(0, restaurantId, foodId, "", price, available);
            int menuItemId = menuItemDAO.addMenuItem(menuItem);

            response.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_CREATED, "Menu item successfully created");
            jsonResponse.put("menu_item_id", menuItemId);
            jsonUtil.sendResponse(response, jsonResponse);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating menu item: " + e.getMessage());
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String menuItemIdParam = request.getParameter("menu_item_id");
        String restaurantIdParam = request.getParameter("restaurant_id");

        try {
            if (menuItemIdParam == null && restaurantIdParam == null) {
                List<MenuItem> menuItems = menuItemDAO.getAllMenuItems();
                JSONArray menuItemsArray = new JSONArray();
                for (MenuItem menuItem : menuItems) {
                    JSONObject menuItemJson = new JSONObject(menuItem);
                    menuItemJson.remove("restaurantId"); // Ensure restaurantId is removed
                    menuItemsArray.put(menuItemJson);
                }
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("menu_items", menuItemsArray);
                jsonUtil.sendResponse(response, jsonResponse);

            } else if (menuItemIdParam != null) {
                int menuItemId = Integer.parseInt(menuItemIdParam);
                MenuItem menuItem = menuItemDAO.getMenuItemById(menuItemId);
                if (menuItem != null) {
                    JSONObject menuItemJson = new JSONObject(menuItem);
                    menuItemJson.remove("restaurantId"); // Ensure restaurantId is removed
                    jsonUtil.sendResponse(response, menuItemJson);
                } else {
                	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Menu item not found");
                    jsonUtil.sendResponse(response, jsonResponse);
                }
            } else if (restaurantIdParam != null) {
                int restaurantId = Integer.parseInt(restaurantIdParam);
                List<MenuItem> menuItems = menuItemDAO.getMenuItemsByRestaurantId(restaurantId);
                JSONArray menuItemsArray = new JSONArray();
                
                // Get restaurant name and add it to the response
                String restaurantName = restaurantDAO.getRestaurantByNameId(restaurantId);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("restaurant_name", restaurantName != null ? restaurantName : "Unknown Restaurant");
                
                for (MenuItem menuItem : menuItems) {
                    JSONObject menuItemJson = new JSONObject(menuItem);
                    menuItemJson.remove("restaurantId"); // Ensure restaurantId is removed
                    menuItemsArray.put(menuItemJson);
                }
                
                jsonResponse.put("menu_items", menuItemsArray);
                jsonUtil.sendResponse(response, jsonResponse);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving menu items: " + e.getMessage());
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);

        int menuItemId = jsonRequest.optInt("menu_item_id", -1);
        if (menuItemId == -1) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Menu item ID is required"));
            return;
        }

        HttpSession session = request.getSession(false);

        int userRoleId = (int) session.getAttribute("user_role_id");

        if (userRoleId != 4) {
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Restaurant Admin only"));
            return;
        }

        try {
            MenuItem existingMenuItem = menuItemDAO.getMenuItemById(menuItemId);
            if (existingMenuItem == null) {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Menu item not found"));
                return;
            }

            int restaurantId = jsonRequest.optInt("restaurant_id", existingMenuItem.getRestaurantId());
            int foodId = jsonRequest.optInt("food_id", existingMenuItem.getFoodId());
            double price = jsonRequest.optDouble("price", existingMenuItem.getPrice());
            boolean available = jsonRequest.optBoolean("available", existingMenuItem.isAvailable());

            MenuItem updatedMenuItem = new MenuItem(menuItemId, restaurantId, foodId, "", price, available);
            menuItemDAO.updateMenuItem(updatedMenuItem);
            response.setStatus(HttpServletResponse.SC_OK);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Menu item updated successfully"));
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating menu item: " + e.getMessage());
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String menuItemIdParam = request.getParameter("menu_item_id");

        if (menuItemIdParam == null) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Menu item ID is required"));
            return;
        }

        try {
            int menuItemId = Integer.parseInt(menuItemIdParam);

            HttpSession session = request.getSession(false);
 
            int userRoleId = (int) session.getAttribute("user_role_id");

            if (userRoleId != 4) {
            	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Restaurant Admin only"));
                return;
            }

            menuItemDAO.deleteMenuItem(menuItemId);
            response.setStatus(HttpServletResponse.SC_OK);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Menu item deleted successfully"));
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting menu item: " + e.getMessage()));
        }
    }
}
