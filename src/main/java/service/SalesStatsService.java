package service;

import dao.SalesStatsDAO;
import util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class SalesStatsService {
    private SalesStatsDAO salesStatsDAO = new SalesStatsDAO();
    private JsonUtil jsonUtil = new JsonUtil();

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Parameters to determine the type of statistics and optional date range
        String statType = request.getParameter("type");
        String startDateParam = request.getParameter("start_date");
        String endDateParam = request.getParameter("end_date");

        try {
            // Parse optional date parameters
            LocalDate startDate = startDateParam != null ? LocalDate.parse(startDateParam) : null;
            LocalDate endDate = endDateParam != null ? LocalDate.parse(endDateParam) : null;

            List<Map<String, Object>> stats = null;

            // Determine the type of statistics to fetch based on the 'type' parameter
            if ("restaurant".equalsIgnoreCase(statType)) {
                stats = salesStatsDAO.getRestaurantWiseSalesStats(startDate, endDate);
            } else if ("user".equalsIgnoreCase(statType)) {
                stats = salesStatsDAO.getUserWiseSpendStats(startDate, endDate);
            } else if ("location".equalsIgnoreCase(statType)) {
                stats = salesStatsDAO.getLocationWiseSalesStats(startDate, endDate);
            } else {
            	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Please provide a valid 'type' parameter: 'restaurant', 'user', or 'location'."));
                return;
            }

            // Convert the results to JSON and send the response
            JSONArray statsArray = new JSONArray(stats);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("Sales statistics", statsArray);

            jsonUtil.sendResponse(response, jsonResponse);
        } catch (DateTimeParseException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid date format. Please use YYYY-MM-DD."));
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonUtil.sendResponse(response, jsonUtil.createStatusResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving sales statistics"));
        }
    }
}
