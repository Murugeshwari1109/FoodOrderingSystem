package service;

import model.MealCourseType;
import util.JsonUtil;
import dao.MealCourseTypeDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MealCourseTypeService {
    private MealCourseTypeDAO mealCourseTypesDAO = new MealCourseTypeDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String mealCourseTypeName = jsonRequest.optString("meal_course_type_name");
        HttpSession session = request.getSession(false);
        int userRoleId = (int) session.getAttribute("user_role_id");

        try {
        	 if (userRoleId != 2) {
        		 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		 jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admins only"));
                 return;
             }
            MealCourseType mealCourseType = new MealCourseType(0, mealCourseTypeName);
            int mealCourseTypeId = mealCourseTypesDAO.addMealCourseType(mealCourseType);
            response.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_CREATED, "Meal Course Type successfully created");
            jsonResponse.put("meal_course_type_id", mealCourseTypeId);
            jsonUtil.sendResponse(response, jsonResponse);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating meal course type");
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String mealCourseTypeIdParam = request.getParameter("meal_course_type_id");

        try {
            if (mealCourseTypeIdParam == null) {
                // Retrieve all meal course types
                List<MealCourseType> mealCourseTypes = mealCourseTypesDAO.getAllMealCourseTypes();
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("mealCourseTypes", mealCourseTypes);
                jsonUtil.sendResponse(response, jsonResponse);
            } else {
                int mealCourseTypeId = Integer.parseInt(mealCourseTypeIdParam);
                MealCourseType mealCourseType = mealCourseTypesDAO.getMealCourseTypeById(mealCourseTypeId);
                if (mealCourseType != null) {
                    JSONObject jsonResponse = new JSONObject(mealCourseType);
                    jsonUtil.sendResponse(response, jsonResponse);
                } else {
                	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Meal Course Type not found"));
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving meal course type");
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String mealCourseTypeIdParam = jsonRequest.optString("meal_course_type_id");

        if (mealCourseTypeIdParam == null) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Meal Course Type ID is required"));
            return;
        }
        HttpSession session = request.getSession(false);
       int userRoleId = (int) session.getAttribute("user_role_id");

        int mealCourseTypeId;
        try {
        	mealCourseTypeId = Integer.parseInt(mealCourseTypeIdParam);
        } catch (NumberFormatException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid Meal Course Type ID format"));
            return;
        }

        try {
        	 if (userRoleId != 2) {
        		 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		 jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Admins only"));
                 return;
             }
            MealCourseType existingMealCourseType = mealCourseTypesDAO.getMealCourseTypeById(mealCourseTypeId);
            if (existingMealCourseType == null) {
            	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            	jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Meal Course Type not found"));
                return;
            }

            String mealCourseTypeName = jsonRequest.optString("meal_course_type_name", existingMealCourseType.getMealCourseTypeName());
            MealCourseType updatedMealCourseType = new MealCourseType(mealCourseTypeId, mealCourseTypeName);
            mealCourseTypesDAO.updateMealCourseType(updatedMealCourseType);
            response.setStatus(HttpServletResponse.SC_OK);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Meal Course Type updated successfully"));

        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating meal course type"));
        }
    }
}