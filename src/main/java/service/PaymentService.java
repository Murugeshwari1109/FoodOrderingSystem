package service;

import dao.PaymentDAO;
import model.Payment;
import util.JsonUtil;
import database.DatabaseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PaymentService {
    private PaymentDAO paymentDAO = new PaymentDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        int sessionUserRoleId = (int) session.getAttribute("user_role_id");
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        int orderId = jsonRequest.optInt("order_id");
        int paymentMethodId = jsonRequest.optInt("payment_method_id");
        

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            // Fetch order details
            double amount = paymentDAO.getOrderAmountById(orderId, conn);
            Integer orderUserId = paymentDAO.getOrderUserIdById(orderId, conn);

            if (amount <= 0 || orderUserId == null) {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Invalid order ID or details not found"));
                return;
            }
            
            // Check if the payment method requires delivery person involvement
            if (paymentMethodId == 5 && sessionUserRoleId != 4) {
            	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Only delivery person can make payment with this method"));
                return;
            }
			
            // Create Payment Record
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setUserId(orderUserId);
            payment.setAmount(amount);
            payment.setPaymentMethodId(paymentMethodId);

            int paymentId = paymentDAO.createPayment(payment, conn);
            if (paymentId <= 0) {
                conn.rollback();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create payment record"));
                return;
            }

            if (paymentMethodId != 5 || sessionUserRoleId == 3) {
            	paymentDAO.updateOrderPaymentStatus(orderId, conn);
            	paymentDAO.updatePaymentStatus(orderId,conn);
            }

            conn.commit();
            response.setStatus(HttpServletResponse.SC_OK);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Payment processed successfully with Payment ID: " + paymentId));
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
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing payment: " + e.getMessage()));
        }
    }
 
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        int userId = (int) session.getAttribute("user_id");
        String paymentIdParam = request.getParameter("payment_id");
        
        try {
            Payment payment = null;
            if (paymentIdParam != null) {
                int paymentId = Integer.parseInt(paymentIdParam);
                payment = paymentDAO.getPaymentById(paymentId);
            } 
            
            else{
                payment = paymentDAO.getPaymentByUserId(userId);
            }
             
            if (payment == null) {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Payment details not found"));
                return;
            }

            // Create JSON response with payment details
            JSONObject paymentDetails = new JSONObject();
            paymentDetails.put("payment_id", payment.getPaymentId());
            paymentDetails.put("order_id", payment.getOrderId());
            paymentDetails.put("user_id", payment.getUserId());
            paymentDetails.put("amount", payment.getAmount());
            paymentDetails.put("payment_method_id", payment.getPaymentMethodId());

            jsonUtil.sendResponse(response, paymentDetails);

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
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving payment details: " + e.getMessage()));
        }
    }
    
    public void handleGetAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        // Retrieve user role to check if the user is an admin
        int userRoleId = (int) session.getAttribute("user_role_id");

        try {
            if (userRoleId == 2) {  
                List<Payment> payments = paymentDAO.getAllPayments();
                JSONArray paymentArray = new JSONArray();

                for (Payment payment : payments) {
                    JSONObject paymentDetails = new JSONObject();
                    paymentDetails.put("payment_id", payment.getPaymentId());
                    paymentDetails.put("order_id", payment.getOrderId());
                    paymentDetails.put("user_id", payment.getUserId());
                    paymentDetails.put("amount", payment.getAmount());
                    paymentDetails.put("payment_method_id", payment.getPaymentMethodId());
                    paymentArray.put(paymentDetails);
                }

                JSONObject responseObject = new JSONObject();
                responseObject.put("payments", paymentArray);
                jsonUtil.sendResponse(response, responseObject);

            } else {
            	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admin only"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving payments: " + e.getMessage()));
        }
    }
}
