package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONObject;
import database.DatabaseUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.print("Please log in.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        JSONObject jsonRequest = new JSONObject(jsonBuilder.toString());
        String username = jsonRequest.getString("user_name");
        String password = jsonRequest.getString("password");

        JSONObject jsonResponse = new JSONObject();

        try (Connection conn = DatabaseUtil.getConnection()) {
        	String sql = "SELECT u.user_id, u.user_role_id FROM UserCredentials uc JOIN Users u ON uc.user_id = u.user_id WHERE uc.user_name = ? AND uc.password = ?";
        	
        	
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                int userRoleId = rs.getInt("user_role_id");

                HttpSession session = request.getSession();
                session.setAttribute("user_id", userId);
                session.setAttribute("user_role_id", userRoleId);
                String sessionId = session.getId();

                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Login successful");
                jsonResponse.put("user_id", userId);
                jsonResponse.put("session_id", sessionId);
            } else {
                jsonResponse.put("status", "failure");
                jsonResponse.put("user_name", username);
                jsonResponse.put("message", "Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
        }

        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}



