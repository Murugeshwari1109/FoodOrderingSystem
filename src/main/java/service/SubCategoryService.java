package service;

import model.SubCategory;
import util.JsonUtil;
import dao.SubCategoryDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SubCategoryService {
    private SubCategoryDAO subCategoriesDAO = new SubCategoryDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String subCategoryName = jsonRequest.optString("sub_category_name");
        HttpSession session = request.getSession(false);
        int userRoleId = (int) session.getAttribute("user_role_id");

        try {
        	if (userRoleId != 2) {
        		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admins only"));
                return;
            }
            SubCategory subCategory = new SubCategory(0, subCategoryName);
            int subCategoryId = subCategoriesDAO.addSubCategory(subCategory);
            response.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_CREATED, "SubCategory successfully created");
            jsonResponse.put("sub_category_id", subCategoryId);
            jsonUtil.sendResponse(response, jsonResponse);
            

        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating subCategory");
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String subCategoryIdParam = request.getParameter("sub_category_id");

        try {
            if (subCategoryIdParam == null) {
                List<SubCategory> subCategories = subCategoriesDAO.getAllSubCategories();
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("subCategories", subCategories);
                jsonUtil.sendResponse(response, jsonResponse);
            } else {
                int subCategoryId = Integer.parseInt(subCategoryIdParam);
                SubCategory subCategory = subCategoriesDAO.getSubCategoryById(subCategoryId);
                if (subCategory != null) {
                    JSONObject jsonResponse = new JSONObject(subCategory);
                    jsonUtil.sendResponse(response, jsonResponse);
                } else {
                	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "SubCategory not found"));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving subCategory");
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String subCategoryIdParam = jsonRequest.optString("sub_category_id");

        if (subCategoryIdParam == null) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SubCategory ID is required"));
            return;
        }
        
        HttpSession session = request.getSession(false);
        int userRoleId = (int) session.getAttribute("user_role_id");

        int subCategoryId;
        try {
            subCategoryId = Integer.parseInt(subCategoryIdParam);
        } catch (NumberFormatException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid SubCategory ID format"));
            return;
        }

        try {
        	if (userRoleId != 2) {
        		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admins only"));
                return;
            }
            SubCategory existingSubCategory = subCategoriesDAO.getSubCategoryById(subCategoryId);
            if (existingSubCategory == null) {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "SubCategory not found"));
                return;
            }

            String subCategoryName = jsonRequest.optString("sub_category_name", existingSubCategory.getSubCategoryName());

            SubCategory updatedSubCategory = new SubCategory(subCategoryId, subCategoryName);
            subCategoriesDAO.updateSubCategory(updatedSubCategory);
            response.setStatus(HttpServletResponse.SC_OK);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "SubCategory updated successfully"));

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating subCategory"));
        }
    }
}
