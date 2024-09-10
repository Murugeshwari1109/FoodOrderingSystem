package service;

import model.Cusine;
import util.JsonUtil;
import dao.CusineDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CusineService {
    private CusineDAO cusinesDAO = new CusineDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String cusineIdParam = request.getParameter("cusine_id");

        try {
            if (cusineIdParam == null) {
                // Retrieve all cusines
                List<Cusine> cusines = cusinesDAO.getAllCusines();
                JSONArray cusinesArray = new JSONArray();
                for (Cusine cusine : cusines) {
                    cusinesArray.put(new JSONObject(cusine));
                }
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("cusines", cusinesArray);
                jsonUtil.sendResponse(response, jsonResponse);
            } else {
                int cusineId = Integer.parseInt(cusineIdParam);
                Cusine cusine = cusinesDAO.getCusineById(cusineId);
                if (cusine != null) {
                	jsonUtil. sendResponse(response, new JSONObject(cusine));
                } else {
                	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Cusine not found"));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving cusine"));
        }
    }

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	 HttpSession session = request.getSession(false);
        int userRoleId = (int) session.getAttribute("user_role_id");
        
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String cusineName = jsonRequest.optString("cusine_name");
        
        try {
        	 if (userRoleId != 2) {
        		 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		 jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admins only"));
                 return;
             }
            Cusine cusine = new Cusine(0, cusineName);
            int cusineId = cusinesDAO.addCusine(cusine);
            
            response.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_CREATED, "Cusine created successfully");
            jsonResponse.put("cusine_id", cusineId);
            jsonUtil.sendResponse(response, jsonResponse);
        } catch (SQLException e) {
            e.printStackTrace();// Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating cusine " + e.getMessage()));
        }
    }

    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String cusineIdParam = jsonRequest.optString("cusine_id");
        

        if (cusineIdParam == null) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cusine ID is required"));
            return;
        }
        int cusineId = Integer.parseInt(cusineIdParam);
        HttpSession session = request.getSession(false);

        int userRoleId = (int) session.getAttribute("user_role_id");

        try {
        	 if (userRoleId != 2) {
        		 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		 jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admins only"));
                 return;
             }
            Cusine existingCusine = cusinesDAO.getCusineById(cusineId);
            if (existingCusine == null) {
            	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Cusine not found"));
                return;
            }

            String cusineName = jsonRequest.optString("cusine_name", existingCusine.getCusineName());
            Cusine updatedCusine = new Cusine(cusineId, cusineName);
            cusinesDAO.updateCusine(updatedCusine);
            response.setStatus(HttpServletResponse.SC_OK);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Cusine updated successfully"));
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating cusine " + e.getMessage()));
        }
    }
}