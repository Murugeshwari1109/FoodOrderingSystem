package service;

import dao.OrderStatsDAO;
import dao.RestaurantDAO;
import util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderStatsService {
    private OrderStatsDAO orderStatsDAO = new OrderStatsDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fetch request parameters
        String month = request.getParameter("month");
        String startDateParam = request.getParameter("start_date");
        String endDateParam = request.getParameter("end_date");
        String restaurantIdParam = request.getParameter("restaurant_id");
        String userIdParam = request.getParameter("user_id");
        String statType = request.getParameter("type");
        String downloadFlag = request.getParameter("download");

        JSONObject jsonResponse = new JSONObject();
        try {
            List<Map<String, Object>> orderStats = null;
            LocalDate startDate = null;
            LocalDate endDate = null;

            if (startDateParam != null && endDateParam != null) {
                startDate = LocalDate.parse(startDateParam);
                endDate = LocalDate.parse(endDateParam);
            }

            if (userIdParam != null) {
                // Retrieve food-wise order counts for the given user_id
                orderStats = orderStatsDAO.getFoodWiseOrderCountByUser(userIdParam, month);

            } else if (restaurantIdParam != null) {
                // Retrieve food-wise order counts for the given restaurant_id
                orderStats = orderStatsDAO.getFoodWiseOrderCountByRestaurant(restaurantIdParam, month);

            } else if (month != null) {
                // Retrieve food-wise order counts for the given month
                orderStats = orderStatsDAO.getFoodWiseOrderCountByMonth(month);

            } else if (startDate != null && endDate != null) {
                if ("city".equalsIgnoreCase(statType)) {
                    // Retrieve location-wise food split-up
                    orderStats = orderStatsDAO.getLocationWiseFoodSplit(startDate, endDate);
                } else {
                    orderStats = orderStatsDAO.getFoodWiseOrderCountByDateRange(startDate, endDate, restaurantIdParam, userIdParam);
                }

            } else {
                // Invalid query parameters
            	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid query parameters"));
                return;
            }

            // Build the JSON response
            if (orderStats != null) {
                JSONArray statsArray = new JSONArray();

                if ("city".equalsIgnoreCase(statType)) {
                    // Handle location-wise food split-up
                    for (Map<String, Object> cityData : orderStats) {
                        JSONObject cityJson = new JSONObject();
                        cityJson.put("city", cityData.get("city"));
                        JSONObject foodCountsJson = new JSONObject((Map<?, ?>) cityData.get("foodCounts"));
                        cityJson.put("foodCounts", foodCountsJson);
                        statsArray.put(cityJson);
                    }

                    jsonResponse.put("startDate", startDate != null ? startDate.toString() : null);
                    jsonResponse.put("endDate", endDate != null ? endDate.toString() : null);

                } else if (restaurantIdParam != null) {
                    int restaurantId = Integer.parseInt(restaurantIdParam);
                    String restaurantName = restaurantDAO.getRestaurantByNameId(restaurantId);
                    // Handle restaurant-wise order counts
                    JSONObject uniqueFoodStats = new JSONObject();
                    for (Map<String, Object> stat : orderStats) {
                        String foodName = (String) stat.get("foodName");
                        int orderCount = (Integer) stat.get("orderCount");

                        if (!uniqueFoodStats.has(foodName)) {
                            uniqueFoodStats.put(foodName, orderCount);
                        } else {
                            uniqueFoodStats.put(foodName, uniqueFoodStats.getInt(foodName) + orderCount);
                        }
                    }
                    
                    for (String foodName : uniqueFoodStats.keySet()) {
                        JSONObject statJson = new JSONObject();
                        statJson.put("foodName", foodName);
                        statJson.put("orderCount", uniqueFoodStats.getInt(foodName));
                        statsArray.put(statJson);
                    }
                    jsonResponse.put("restaurantName", restaurantName);

                } else if (userIdParam != null) {
                    // Handle user-wise order counts
                    JSONObject uniqueUserFoodStats = new JSONObject();
                    for (Map<String, Object> stat : orderStats) {
                        String foodName = (String) stat.get("foodName");
                        int orderCount = (Integer) stat.get("orderCount");

                        if (!uniqueUserFoodStats.has(foodName)) {
                            uniqueUserFoodStats.put(foodName, orderCount);
                        } else {
                            uniqueUserFoodStats.put(foodName, uniqueUserFoodStats.getInt(foodName) + orderCount);
                        }
                    }

                    for (String foodName : uniqueUserFoodStats.keySet()) {
                        JSONObject statJson = new JSONObject();
                        statJson.put("foodName", foodName);
                        statJson.put("orderCount", uniqueUserFoodStats.getInt(foodName));
                        statJson.put("firstName", orderStats.get(0).get("firstName"));
                        statJson.put("lastName", orderStats.get(0).get("lastName"));
                        statJson.put("contactNumber", orderStats.get(0).get("contactNumber"));
                        statsArray.put(statJson);
                    }

                } else if (month != null) {
                    // Handle month-wise order counts
                    JSONObject uniqueMonthFoodStats = new JSONObject();
                    for (Map<String, Object> stat : orderStats) {
                        String foodName = (String) stat.get("foodName");
                        int orderCount = (Integer) stat.get("orderCount");

                        if (!uniqueMonthFoodStats.has(foodName)) {
                            uniqueMonthFoodStats.put(foodName, orderCount);
                        } else {
                            uniqueMonthFoodStats.put(foodName, uniqueMonthFoodStats.getInt(foodName) + orderCount);
                        }
                    }

                    for (String foodName : uniqueMonthFoodStats.keySet()) {
                        JSONObject statJson = new JSONObject();
                        statJson.put("foodName", foodName);
                        statJson.put("orderCount", uniqueMonthFoodStats.getInt(foodName));
                        statsArray.put(statJson);
                    }

                    jsonResponse.put("month", month);

                } else {
                    // Handle default or unexpected cases
                    JSONObject uniqueDefaultFoodStats = new JSONObject();
                    for (Map<String, Object> stat : orderStats) {
                        String foodName = (String) stat.get("foodName");
                        int orderCount = (Integer) stat.get("orderCount");

                        if (!uniqueDefaultFoodStats.has(foodName)) {
                            uniqueDefaultFoodStats.put(foodName, orderCount);
                        } else {
                            uniqueDefaultFoodStats.put(foodName, uniqueDefaultFoodStats.getInt(foodName) + orderCount);
                        }
                    }

                    for (String foodName : uniqueDefaultFoodStats.keySet()) {
                        JSONObject statJson = new JSONObject();
                        statJson.put("foodName", foodName);
                        statJson.put("orderCount", uniqueDefaultFoodStats.getInt(foodName));
                        statsArray.put(statJson);
                    }
                }

                jsonResponse.put("orderStats", statsArray);
                if ("true".equalsIgnoreCase(downloadFlag)) {
                    // Set headers for CSV download
                    response.setContentType("text/csv");
                    response.setHeader("Content-Disposition", "attachment; filename=\"Foodwise Order Count.csv\"");
                    PrintWriter out = response.getWriter();
                    writeCsv(out, jsonResponse,statType);
                    out.flush();  
                } else {
                    // Set content type for regular JSON response
                    response.setContentType("application/json");
                    jsonUtil.sendResponse(response, jsonResponse);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving order statistics"));
        }
    }
    
    private void writeCsv(PrintWriter out, JSONObject jsonResponse, String statType) {
        JSONArray statsArray = jsonResponse.optJSONArray("orderStats");
        if (statsArray == null || statsArray.length() == 0) {
            // Handle the case where there are no rows to write
            return;
        }

        if ("city".equalsIgnoreCase(statType)) {
            // Location-wise food split-up
            // Determine all unique food names
            Set<String> foodNamesSet = new LinkedHashSet<>();
            Set<String> citiesSet = new LinkedHashSet<>();
            
            for (int i = 0; i < statsArray.length(); i++) {
                JSONObject cityData = statsArray.getJSONObject(i);
                String city = cityData.optString("city", "");
                citiesSet.add(city);
                
                JSONObject foodCounts = cityData.optJSONObject("foodCounts");
                if (foodCounts != null) {
                    foodNamesSet.addAll(foodCounts.keySet());
                }
            }

            // Write headers
            out.print("FoodName");
            for (String city : citiesSet) {
                out.print("," + city);
            }
            out.println();

            // Write rows
            for (String foodName : foodNamesSet) {
                out.print(foodName);
                for (String city : citiesSet) {
                    int count = 0;
                    for (int i = 0; i < statsArray.length(); i++) {
                        JSONObject cityData = statsArray.getJSONObject(i);
                        if (city.equals(cityData.optString("city", ""))) {
                            JSONObject foodCounts = cityData.optJSONObject("foodCounts");
                            count = foodCounts != null ? foodCounts.optInt(foodName, 0) : 0;
                            break;
                        }
                    }
                    out.print("," + count);
                }
                out.println();
            }

        } else {
            // Other types of data (restaurant-wise, month-wise, user-wise)
            out.println("FoodName,FoodCount");

            for (int i = 0; i < statsArray.length(); i++) {
                JSONObject statJson = statsArray.getJSONObject(i);
                String foodName = statJson.optString("foodName", "");
                int orderCount = statJson.optInt("orderCount", 0);
                out.println(foodName + "," + orderCount);
            }
        }
    }


}
