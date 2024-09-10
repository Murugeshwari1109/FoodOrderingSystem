package service;

import dao.PaymentDAO;
import database.DatabaseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.JsonUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class PaymentStatService {
    private PaymentDAO paymentDAO = new PaymentDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	HttpSession session = request.getSession(false);
        int sessionUserRoleId = (int) session.getAttribute("user_role_id");
        try (Connection conn = DatabaseUtil.getConnection()){
        	if(sessionUserRoleId == 3) { 
        		String orderID = request.getParameter("order_id");
        
        		int orderId = Integer.parseInt(orderID);
        		boolean pay_success = paymentDAO.updateOrderPaymentStatus(orderId,conn);
    
        		
                if (pay_success ) {
                	response.setStatus(HttpServletResponse.SC_OK);
                	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Payment Status updated successfully"));
                } else {
                	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update status"));
                }
        	}else {
        		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Failed to Update status. User is not a Delivery boy"));

        	}
            
        } catch (NumberFormatException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid order ID format: " + e.getMessage()));
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating status: " + e.getMessage()));
        }
    }
}