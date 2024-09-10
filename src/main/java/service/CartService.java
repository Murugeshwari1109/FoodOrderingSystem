package service;

import dao.CartDAO;
import dao.CartItemDAO;
import model.Cart;
import model.CartItem;
import util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CartService {
    private CartDAO cartDAO = new CartDAO();
    private CartItemDAO cartItemDAO = new CartItemDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("user_id");
        
        int menuItemId = jsonRequest.optInt("menu_item_id");
        int quantity = jsonRequest.optInt("quantity", 1);

        try {
            // Check if user already has a cart
            Cart cart = cartDAO.getCartByUserId(userId);
            if (cart == null) {
                // Create a new cart for the user
                cart = new Cart(0, userId);
                int cartId = cartDAO.addCart(cart);
                cart.setCartId(cartId);
            }

            // Check if item already exists in the cart
            CartItem existingCartItem = cartItemDAO.getCartItem(cart.getCartId(), menuItemId);
            if (existingCartItem != null) {
                // Update quantity if item already exists
                existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
                cartItemDAO.updateCartItem(existingCartItem);
            } else {
                // Add new item to the cart
                CartItem cartItem = new CartItem(0, cart.getCartId(), menuItemId, quantity);
                cartItemDAO.addCartItem(cartItem);
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_CREATED, "Item added to cart successfully");
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding item to cart");
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);
        }
    }

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String userIdParam = request.getParameter("user_id");

        try {
            if (userIdParam == null) {
                // Retrieve all carts if no user_id is provided
                List<Cart> allCarts = cartDAO.getAllCarts();
                JSONArray allCartsArray = new JSONArray();

                for (Cart cart : allCarts) {
                    List<CartItem> cartItems = cartItemDAO.getCartItemsByCartId(cart.getCartId());
                    JSONArray cartItemsArray = new JSONArray();
                    for (CartItem item : cartItems) {
                        JSONObject itemJson = new JSONObject(item);
                        cartItemsArray.put(itemJson);
                    }

                    JSONObject cartJson = new JSONObject();
                    cartJson.put("cart_id", cart.getCartId());
                    cartJson.put("user_id", cart.getUserId());
                    cartJson.put("cart_items", cartItemsArray);
                    allCartsArray.put(cartJson);
                }

                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("carts", allCartsArray);
                JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                jsonUtil.sendResponse(response, snakeCaseResponse);

            } else {
                int userId = Integer.parseInt(userIdParam);

                // Retrieve the cart for the specific user
                Cart cart = cartDAO.getCartByUserId(userId);
                if (cart != null) {
                    List<CartItem> cartItems = cartItemDAO.getCartItemsByCartId(cart.getCartId());
                    JSONArray cartItemsArray = new JSONArray();
                    for (CartItem item : cartItems) {
                        JSONObject itemJson = new JSONObject(item);
                        cartItemsArray.put(itemJson);
                    }

                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("cart_items", cartItemsArray);
                    JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                    jsonUtil.sendResponse(response, snakeCaseResponse);
                } else {
                	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Cart not found for the user");
                    JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                    jsonUtil.sendResponse(response, snakeCaseResponse);
                }
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving cart");
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);
        }
    }

    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        
        int menuItemId = jsonRequest.optInt("menu_item_id");
        int newQuantity = jsonRequest.optInt("quantity");

        if (newQuantity <= 0) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid quantity. It must be greater than zero.");
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);
            return;
        }
        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("user_id");

        try {
            // Fetch the cart by user ID
            Cart cart = cartDAO.getCartByUserId(userId);
            if (cart != null) {
                // Fetch the cart item by cart ID and menu item ID
                CartItem cartItem = cartItemDAO.getCartItem(cart.getCartId(), menuItemId);
                if (cartItem != null) {
                    cartItem.setQuantity(newQuantity);
                    cartItemDAO.updateCartItem(cartItem);

                    // Send a success response
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Cart item updated successfully");
                    JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                    jsonUtil.sendResponse(response, snakeCaseResponse);
                } else {
                	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Item not found in the cart");
                    JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                    jsonUtil.sendResponse(response, snakeCaseResponse);
                }
            } else {
            	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Cart not found for the user");
                JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                jsonUtil.sendResponse(response, snakeCaseResponse);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating cart item");
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);
        }
    }

    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("user_id");
        int menuItemId = jsonRequest.optInt("menu_item_id");

        try {
            Cart cart = cartDAO.getCartByUserId(userId);
            if (cart != null) {
                CartItem cartItem = cartItemDAO.getCartItem(cart.getCartId(), menuItemId);
                if (cartItem != null) {
                    cartItemDAO.deleteCartItem(cartItem.getCartItemId());
                    response.setStatus(HttpServletResponse.SC_OK);
                    JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Item removed from cart successfully");
                    JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                    jsonUtil.sendResponse(response, snakeCaseResponse);
                } else { 
                	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Item not found in the cart");
                    JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                    jsonUtil.sendResponse(response, snakeCaseResponse);
                }
            } else {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Cart not found for the user");
                JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
                jsonUtil.sendResponse(response, snakeCaseResponse);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error removing item from cart");
            JSONObject snakeCaseResponse = jsonUtil.convertCamelToSnake(jsonResponse);
            jsonUtil.sendResponse(response, snakeCaseResponse);
        }
    }
}
