package util;

import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import jakarta.servlet.http.HttpServletRequest;

public class JsonUtil {

    // Parses the request body into a JSON object
    public JSONObject parseRequestToJson(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return new JSONObject(sb.toString());
    }

    // Sends a JSON response to the client
    public void sendResponse(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }

    // Creates a JSON object representing the status of an operation
    public  JSONObject createStatusResponse(int status, String message) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", status);
        jsonResponse.put("message", message);
        return jsonResponse;
    }
    public  JSONObject createStatusResponse(String status, String message) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", status);
        jsonResponse.put("message", message);
        return jsonResponse;
    }
    public  JSONObject createStatusResponse(boolean status, String message) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", status);
        jsonResponse.put("message", message);
        return jsonResponse;
    }
    
    public JSONObject convertCamelToSnake(JSONObject camelCaseJson) {
        JSONObject snakeCaseJson = new JSONObject();
        for (String key : camelCaseJson.keySet()) {
            Object value = camelCaseJson.get(key);
            String snakeCaseKey = convertCamelToSnake(key);
            if (value instanceof JSONArray) {
                value = convertCamelToSnake((JSONArray) value);
            }
            snakeCaseJson.put(snakeCaseKey, value);
        }
        return snakeCaseJson;
    }

    public JSONArray convertCamelToSnake(JSONArray camelCaseArray) {
        JSONArray snakeCaseArray = new JSONArray();
        for (int i = 0; i < camelCaseArray.length(); i++) {
            Object value = camelCaseArray.get(i);
            if (value instanceof JSONObject) {
                value = convertCamelToSnake((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = convertCamelToSnake((JSONArray) value);
            }
            snakeCaseArray.put(value);
        }
        return snakeCaseArray;
    }

    private String convertCamelToSnake(String camelCase) {
        StringBuilder snakeCase = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (snakeCase.length() > 0) {
                    snakeCase.append('_');
                }
                snakeCase.append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
        }
        return snakeCase.toString();
    }
}
