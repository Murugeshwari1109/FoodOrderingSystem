package filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

public class BasicAuthFilter implements Filter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "Basic ";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/FoodOrderingDB"; 
    private static final String JDBC_USER = "root"; 
    private static final String JDBC_PASSWORD = "murug@2003"; 

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization required
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader(AUTH_HEADER);

        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            String encodedCredentials = authHeader.substring(AUTH_PREFIX.length()).trim();
            String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials));
            String[] credentials = decodedCredentials.split(":", 2);

            if (credentials.length == 2 && validateCredentials(credentials[0], credentials[1])) {
                chain.doFilter(request, response);
                return;
            }
        }

        httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Access to the application\"");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean validateCredentials(String username, String password) {
        boolean isValid = false;
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM UserCredentials WHERE user_name = ? AND password = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        isValid = rs.getInt(1) > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }

    @Override
    public void destroy() {
        // No cleanup required
    }
}
