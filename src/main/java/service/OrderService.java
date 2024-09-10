package service;
import dao.CartDAO;
import dao.CartItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import model.Address;
import model.Cart;
import model.CartItem;
import model.UserOrder;
import util.JsonUtil;
import model.OrderItem;
import model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private CartDAO cartDAO = new CartDAO();
    private CartItemDAO cartItemDAO = new CartItemDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderItemDAO orderItemDAO = new OrderItemDAO();
    private JsonUtil jsonUtil = new JsonUtil();
    
    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
    	 HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("user_id");
        int deliveryAddressId =jsonRequest.optInt("delivery_address_id");

        try {
            Cart cart = cartDAO.getCartByUserId(userId);
         
            if (cart == null) {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Cart not found for user ID: " + userId));
                return;
            }
            int cartId = cart.getCartId(); 
            List<CartItem> cartItems = cartItemDAO.getCartItemsByCartId(cartId);
            if (cartItems.isEmpty()) {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Cart ID: " + cartId + " is empty"));
                return;
            }

            double totalAmount = 0.0;
            int restaurantId = 0;

            // Calculate total amount and get restaurant_id
            for (CartItem cartItem : cartItems) {
                double price = orderDAO.getMenuItemPrice(cartItem.getMenuItemId());
                totalAmount += price * cartItem.getQuantity();
                restaurantId = orderDAO.getRestaurantIdFromMenuItem(cartItem.getMenuItemId());
            }

            // Create Order with pending status
            UserOrder order = new UserOrder(userId, restaurantId, totalAmount, deliveryAddressId);
            int orderId = orderDAO.createOrder(order);

            if (orderId <= 0) {
            	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create order record"));
                return;
            }

            // Insert Order Items
            for (CartItem cartItem : cartItems) {
                double price = orderDAO.getMenuItemPrice(cartItem.getMenuItemId());
                OrderItem orderItem = new OrderItem(orderId, cartItem.getMenuItemId(), cartItem.getQuantity(), price);
                orderItemDAO.createOrderItem(orderItem);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Order placed successfully with Order ID: " + orderId));
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage()));
        } catch (NumberFormatException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid number format: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error placing order: " + e.getMessage()));
        }
    }

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userIdParam = request.getParameter("user_id");
        String orderIdParam = request.getParameter("order_id");

        try {
            if (userIdParam != null && !userIdParam.isEmpty()) {
                int userId = Integer.parseInt(userIdParam);
                List<UserOrder> orders = orderDAO.getOrdersByUserId(userId);
                JSONArray ordersArray = new JSONArray();
                JSONObject orderJson = new JSONObject();
                
                for (UserOrder order : orders) {
                	
                    orderJson.put("order_id", order.getOrderId());
                    
                    orderJson.put("restaurant_name", order.getRestaurantName());
                    
                    JSONObject addressJson = new JSONObject();
                    Address address = order.getRestaurantAddress();
                    if (address != null) {
                        addressJson.put("address_line1", address.getAddressLine1());
                        addressJson.put("address_line2", address.getAddressLine2());
                        addressJson.put("city", address.getCity());
                        addressJson.put("state", address.getState());
                        addressJson.put("pincode", address.getPincode());
                    }
                    orderJson.put("restaurant_address", addressJson);

                    User user = order.getUser();
                    orderJson.put("first_name", user.getFirstName());
                    orderJson.put("last_name",user.getLastName());
                    orderJson.put("order_date", order.getOrderDate());
                    orderJson.put("total_amount", order.getTotalAmount());
                    orderJson.put("payment_status", order.getPaymentStatus());
                    orderJson.put("delivery_status", order.getDeliveryStatus());
                    //orderJson.put("payment_method_id", order.getPaymentMethodId());
                    ordersArray.put(orderJson);
                }
                JSONObject jsonResponse = new JSONObject();
               
               
                orderJson.put("user_id", userId);
                jsonResponse.put("orders", ordersArray);
                jsonUtil.sendResponse(response, jsonResponse);

            } else if (orderIdParam != null && !orderIdParam.isEmpty()) {
                try {
                    int orderId = Integer.parseInt(orderIdParam);
                    
                    // Create DAO instances
                    OrderDAO orderDAO = new OrderDAO();
                    OrderItemDAO orderItemDAO = new OrderItemDAO();
                    
                    // Fetch the order details
                    UserOrder order = orderDAO.getOrderById(orderId);
                    
                    if (order != null) {
                        // Fetch the order items
                        List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderId(orderId);
                        
                        // Set the order items in the UserOrder object
                        order.setOrderItems(orderItems);
                        
                        // Convert order and order items to JSON or other response format
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("order_id", order.getOrderId());
                        jsonResponse.put("total_amount", order.getTotalAmount());
                        jsonResponse.put("payment_status", order.getPaymentStatus());
                        jsonResponse.put("delivery_status", order.getDeliveryStatus());
                        jsonResponse.put("restaurant_name", order.getRestaurantName());
                        jsonResponse.put("order_date", order.getOrderDate());
                        jsonResponse.put("user_id", order.getUserId());
                        
                        // Add restaurant address details
                        JSONObject addressJson = new JSONObject();
                        Address address = order.getRestaurantAddress();
                        if (address != null) {
                            addressJson.put("address_line1", address.getAddressLine1());
                            addressJson.put("address_line2", address.getAddressLine2());
                            addressJson.put("city", address.getCity());
                            addressJson.put("state", address.getState());
                            addressJson.put("pincode", address.getPincode());
                        }
                        jsonResponse.put("restaurant_address", addressJson);
                        
                        User user = order.getUser();
                        jsonResponse.put("first_name", user.getFirstName());
                        jsonResponse.put("last_name",user.getLastName());
                        
                        
                        // Add order items
                        JSONArray orderItemsArray = new JSONArray();
                        for (OrderItem orderItem : order.getOrderItems()) {
                            JSONObject orderItemJson = new JSONObject();
                       
                            //orderItemJson.put("menu_item_id", orderItem.getMenuItemId());
                            orderItemJson.put("food_name", orderItem.getFoodName());
                            orderItemJson.put("quantity", orderItem.getQuantity());
                            orderItemJson.put("price", orderItem.getPrice());
                            orderItemsArray.put(orderItemJson);
                        }
                        jsonResponse.put("order_items", orderItemsArray);
                        
                        // Send the JSON response
                        response.setContentType("application/json");
                        response.getWriter().write(jsonResponse.toString());
                    } else {
                        // Handle case where order is not found
                    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid order ID format
                	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format.");
                } catch (SQLException e) {
                    // Handle SQL exceptions
                	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred.");
                } catch (IOException e) {
                    // Handle IO exceptions
                	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing response.");
                }
            }
            else {
                List<UserOrder> allOrders = orderDAO.getAllOrders();
                JSONArray ordersArray = new JSONArray();
                for (UserOrder order : allOrders) {
                    JSONObject orderJson = new JSONObject();
                    orderJson.put("order_id", order.getOrderId());
                    orderJson.put("restaurant_name", order.getRestaurantName());
                    orderJson.put("restaurant_status", order.getRestaurantStatus());
                    JSONObject addressJson = new JSONObject();
                    Address address = order.getRestaurantAddress();
                    if (address != null) {
                        addressJson.put("address_line1", address.getAddressLine1());
                        addressJson.put("address_line2", address.getAddressLine2());
                        addressJson.put("city", address.getCity());
                        addressJson.put("state", address.getState());
                        addressJson.put("pincode", address.getPincode());
                    }
                    orderJson.put("restaurant_address", addressJson);
                    User user = order.getUser();
                    orderJson.put("user_id", user.getUserId());
                    orderJson.put("first_name", user.getFirstName());
                    orderJson.put("last_name",user.getLastName());

                    orderJson.put("order_date", order.getOrderDate());
                    orderJson.put("total_amount", order.getTotalAmount());
                    orderJson.put("payment_status", order.getPaymentStatus());
                    orderJson.put("delivery_status", order.getDeliveryStatus());
                    //orderJson.put("payment_method_id", order.getPaymentMethodId());
                    ordersArray.put(orderJson);
                }
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("orders", ordersArray);
                jsonUtil.sendResponse(response, jsonResponse);
            }

        }  catch (NumberFormatException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid number format: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace to the console or log file
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error placing order: " + e.getMessage()));
        }

    }

}
