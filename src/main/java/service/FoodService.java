package service;

import dao.FoodDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Food;
import util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FoodService {
    private FoodDAO foodDAO = new FoodDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        String foodName = jsonRequest.optString("food_name");
        String foodType = jsonRequest.optString("food_type");
        String spiceLevel = jsonRequest.optString("spice_level");
        String mealType = jsonRequest.optString("meal_type");
        int mealCourseTypeId = jsonRequest.optInt("meal_course_type_id");
        int cusineId = jsonRequest.optInt("cusine_id");
        int subCategoryId = jsonRequest.optInt("sub_category_id");
        
        HttpSession session = request.getSession(false);
        int userRoleId = (int) session.getAttribute("user_role_id");

        try {
            if (userRoleId != 4) {
            	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Restaurant Admin only"));
                return;
            }
            Food food = new Food(0, foodName, foodType, spiceLevel, mealType, mealCourseTypeId, cusineId, subCategoryId);
            int foodId = foodDAO.addFood(food);

            response.setStatus(HttpServletResponse.SC_CREATED);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_CREATED, "Food successfully created");
            jsonResponse.put("food_id", foodId);
            jsonUtil.sendResponse(response, jsonResponse);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating food " + e.getMessage());
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String foodIdParam = request.getParameter("food_id");

        try {
            if (foodIdParam == null) {
                List<Food> foods = foodDAO.getAllFoods();
                JSONArray foodsArray = new JSONArray();
                for (Food food : foods) {
                    JSONObject foodJson = new JSONObject();
                    foodJson.put("food_id", food.getFoodId());
                    foodJson.put("food_name", food.getFoodName());
                    foodJson.put("food_type", food.getFoodType());
                    foodJson.put("spice_level", food.getSpiceLevel());
                    
                    // Use the method to get meal_type as JSONArray
                    JSONArray mealTypeArray = food.getMealTypeAsJsonArray();
                    foodJson.put("meal_type", mealTypeArray);

                    // Fetch names for IDs
                    String mealCourseTypeName = foodDAO.getMealCourseTypeNameById(food.getMealCourseTypeId());
                    String cusineName = foodDAO.getCusineNameById(food.getCusineId());
                    String subCategoryName = foodDAO.getSubCategoryNameById(food.getSubCategoryId());

                    foodJson.put("meal_course_type_name", mealCourseTypeName);
                    foodJson.put("cusine_name", cusineName);
                    foodJson.put("sub_category_name", subCategoryName);
                    foodsArray.put(foodJson);
                }
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("foods", foodsArray);
                response.getWriter().write(jsonResponse.toString());
            } else {
                int foodId = Integer.parseInt(foodIdParam);
                Food food = foodDAO.getFoodById(foodId);
                if (food != null) {
                    JSONObject foodJson = new JSONObject();
                    foodJson.put("food_id", food.getFoodId());
                    foodJson.put("food_name", food.getFoodName());
                    foodJson.put("food_type", food.getFoodType());
                    foodJson.put("spice_level", food.getSpiceLevel());

                    // Use the method to get meal_type as JSONArray
                    JSONArray mealTypeArray = food.getMealTypeAsJsonArray();
                    foodJson.put("meal_type", mealTypeArray);

                    // Fetch names for IDs
                    String mealCourseTypeName = foodDAO.getMealCourseTypeNameById(food.getMealCourseTypeId());
                    String cusineName = foodDAO.getCusineNameById(food.getCusineId());
                    String subCategoryName = foodDAO.getSubCategoryNameById(food.getSubCategoryId());

                    foodJson.put("meal_course_type_name", mealCourseTypeName);
                    foodJson.put("cusine_name", cusineName);
                    foodJson.put("sub_category_name", subCategoryName);
                    response.getWriter().write(foodJson.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Food not found\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error\"}");
            e.printStackTrace();
        }
    }


    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonRequest = jsonUtil.parseRequestToJson(request);
        int foodId = jsonRequest.optInt("food_id");
        String foodName = jsonRequest.optString("food_name", null);
        String foodType = jsonRequest.optString("food_type", null);
        String spiceLevel = jsonRequest.optString("spice_level", null);
        String mealType = jsonRequest.optString("meal_type", null);
        Integer mealCourseTypeId = jsonRequest.has("meal_course_type_id") ? jsonRequest.optInt("meal_course_type_id") : null;
        Integer cusineId = jsonRequest.has("cusine_id") ? jsonRequest.optInt("cusine_id") : null;
        Integer subCategoryId = jsonRequest.has("sub_category_id") ? jsonRequest.optInt("sub_category_id") : null;

        HttpSession session = request.getSession(false);
        int userRoleId = (int) session.getAttribute("user_role_id");

        try {
            if (userRoleId != 4) {
            	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_UNAUTHORIZED, "Access denied: Restaurant Admin only"));
                return;
            }

            // Retrieve the existing food item
            Food existingFood = foodDAO.getFoodById(foodId);
            if (existingFood == null) {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_NOT_FOUND, "Food item not found"));
                return;
            }

            // Update only the fields provided in the request
            if (foodName != null) existingFood.setFoodName(foodName);
            if (foodType != null) existingFood.setFoodType(foodType);
            if (spiceLevel != null) existingFood.setSpiceLevel(spiceLevel);
            if (mealType != null) existingFood.setMealType(mealType);
            if (mealCourseTypeId != null) existingFood.setMealCourseTypeId(mealCourseTypeId);
            if (cusineId != null) existingFood.setCusineId(cusineId);
            if (subCategoryId != null) existingFood.setSubCategoryId(subCategoryId);

            // Update the food item in the database
            foodDAO.updateFood(existingFood);
            response.setStatus(HttpServletResponse.SC_OK);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_OK, "Food successfully updated");
            jsonUtil.sendResponse(response, jsonResponse);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject jsonResponse = jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating food " + e.getMessage());
            jsonUtil.sendResponse(response, jsonResponse);
        }
    }

}
