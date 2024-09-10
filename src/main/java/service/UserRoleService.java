package service;

import dao.UserRoleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.JsonUtil;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

public class UserRoleService extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserRoleDAO userRoleDAO = new UserRoleDAO();
    private JsonUtil jsonUtil = new JsonUtil();
    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        int userId = jsonRequest.getInt("user_id");
        int newRoleId = jsonRequest.getInt("user_role_id");

        JSONObject jsonResponse = new JSONObject();

        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
            	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("status",HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("message", "No active session found");
            } else {
                Integer loggedUserRoleId = (Integer) session.getAttribute("user_role_id");
                
                if (loggedUserRoleId != null && loggedUserRoleId == 2) {
                    boolean success = userRoleDAO.updateUserRole(userId, newRoleId);

                    if (success) {
                    	response.setStatus(HttpServletResponse.SC_OK);
                        jsonResponse.put("status", HttpServletResponse.SC_OK);
                        jsonResponse.put("message", "User role updated successfully");
                    } else {
                    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        jsonResponse.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        jsonResponse.put("message", "Failed to update user role");
                    }
                } else {
                	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    jsonResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                    jsonResponse.put("message", "You do not have permission to update user roles");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse.toString());
            out.flush();
        }
    }
}