package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CartService;
import service.CusineService;
import service.DeliveryService;
import service.FoodService;
import service.MealCourseTypeService;
import service.MenuItemService;
import service.OrderService;
import service.OrderStatsService;
import service.PaymentService;
import service.PaymentStatService;
import service.RestaurantService;
import service.SalesStatsService;
import service.SubCategoryService;
import service.UserRoleService;
import service.UserService;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String path = request.getPathInfo();
            if (path.startsWith("/users")) {
            	UserService userService = new UserService();
                userService.handlePost(request, response);
            } else if (path.startsWith("/restaurants")) {
            	RestaurantService restaurantService = new RestaurantService();
                restaurantService.handlePost(request, response);
            } else if (path.startsWith("/cusines")) {
            	CusineService cusineService = new CusineService();
                cusineService.handlePost(request, response);
            } else if (path.startsWith("/subcategories")) {
            	SubCategoryService subCategoryService = new SubCategoryService();
            	subCategoryService.handlePost(request, response);
            } else if (path.startsWith("/mealcoursetypes")) {
            	MealCourseTypeService mealCourseTypeService = new MealCourseTypeService();
            	mealCourseTypeService.handlePost(request, response);
            } else if (path.startsWith("/foods")) {
            	FoodService foodService = new FoodService();
            	foodService.handlePost(request, response);
            } else if (path.startsWith("/menu-items")) {
            	MenuItemService menuItemService = new MenuItemService();
            	menuItemService.handlePost(request, response);
            }  else if (path.startsWith("/carts")) {
            	CartService cartService = new CartService();
            	cartService.handlePost(request, response);
            } else if (path.startsWith("/orders")) {
            	OrderService orderService = new OrderService();
            	orderService.handlePost(request, response);
            } else if (path.startsWith("/payment")) {
            	PaymentService paymentService = new PaymentService();
            	paymentService.handlePost(request, response);
            } 
            else {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Resource not found");
            }
        } catch (Exception e) {
            logError("POST request failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String path = request.getPathInfo();
            if (path.startsWith("/user-list")) {
            	UserService userService = new UserService();
                userService.handleGetUserList(request, response);
            } else if (path.startsWith("/user-details")) {
            	UserService userService = new UserService();
                userService.handleGetUserDetails(request, response);
            } else if (path.startsWith("/search/users")) {
               // String query = request.getParameter("query");
                UserService userService = new UserService();
                userService.handleSearchUsers(request, response);
         
            } else if (path.startsWith("/restaurant-list")) {
            	RestaurantService restaurantService = new RestaurantService();
                restaurantService.handleGetRestaurantList(request, response);
            } else if (path.startsWith("/restaurant-details")) {
            	RestaurantService restaurantService = new RestaurantService();
                restaurantService.handleGetRestaurantDetails(request, response);
            } else if (path.startsWith("/search/restaurants")) {
               // String query = request.getParameter("query");
            	RestaurantService restaurantService = new RestaurantService();
            	restaurantService.handleSearchUsers(request, response);
         
            } else if (path.startsWith("/cusines")) {
            	CusineService cusineService = new CusineService();
                cusineService.handleGet(request, response);
            } else if (path.startsWith("/subcategories")) {
            	SubCategoryService subCategoryService = new SubCategoryService();
            	subCategoryService.handleGet(request, response);
            } else if (path.startsWith("/mealcoursetypes")) {
            	MealCourseTypeService mealCourseTypeService = new MealCourseTypeService();
            	mealCourseTypeService.handleGet(request, response);
            } else if (path.startsWith("/foods")) {
            	FoodService foodService = new FoodService();
            	foodService.handleGet(request, response);
            } else if (path.startsWith("/menu-items")) {
            	MenuItemService menuItemService = new MenuItemService();
            	menuItemService.handleGet(request, response);
            } else if (path.startsWith("/carts")) {
            	CartService cartService = new CartService();
            	cartService.handleGet(request, response);
            } else if (path.startsWith("/orders")) {
            	OrderService orderService = new OrderService();
            	orderService.handleGet(request, response);
            } else if (path.startsWith("/payment")) {
            	PaymentService paymentService = new PaymentService();
            	paymentService.handleGet(request, response);
            }  else if (path.startsWith("/allpayments")) {
            	PaymentService paymentService = new PaymentService();
            	paymentService.handleGetAll(request, response);
            } else if (path.startsWith("/order-stats/food-wise")) {
            	OrderStatsService orderStatsService = new OrderStatsService();
                orderStatsService.handleGet(request, response);
            } else if (path.startsWith("/sales-stats")) {
            	SalesStatsService salesStatsService = new SalesStatsService();
                salesStatsService.handleGet(request, response);
            }

            else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Resource not found");
            } 
        } catch (Exception e) {
        	System.out.print(e.getMessage());
            logError("GET request failed", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String path = request.getPathInfo();
            if (path.startsWith("/users")) {
            	UserService userService = new UserService();
                userService.handlePut(request, response);
            } else if (path.startsWith("/update-role")) {
            	UserRoleService userRoleService = new UserRoleService();
                userRoleService.handlePut(request, response);
            } else if (path.startsWith("/restaurants")) {
            	RestaurantService restaurantService = new RestaurantService();
                restaurantService.handlePut(request, response);
            } else if (path.startsWith("/cusines")) {
            	CusineService cusineService = new CusineService();
                cusineService.handlePut(request, response);
            } else if (path.startsWith("/subcategories")) {
            	SubCategoryService subCategoryService = new SubCategoryService();
            	subCategoryService.handlePut(request, response);
            } else if (path.startsWith("/mealcoursetypes")) {
            	MealCourseTypeService mealCourseTypeService = new MealCourseTypeService();
            	mealCourseTypeService.handlePut(request, response);
            } else if (path.startsWith("/foods")) {
            	FoodService foodService = new FoodService();
            	foodService.handlePut(request, response);
            } else if (path.startsWith("/carts")) {
            	CartService cartService = new CartService();
            	cartService.handlePut(request, response);
            } else if (path.startsWith("/menu-items")) {
            	MenuItemService menuItemService = new MenuItemService();
            	menuItemService.handlePut(request, response);
            } else if (path.startsWith("/delivery-stats")) {
            	DeliveryService deliveryService  = new DeliveryService ();
            	deliveryService.handlePut(request, response);
            } else if (path.startsWith("/payment-stats")) {
            	PaymentStatService paymentStatService = new PaymentStatService();
            	paymentStatService.handlePut(request, response);
            } else {
            	 response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Resource not found");
            }
        } catch (Exception e) {
        	 response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logError("PUT request failed", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
        	String path = request.getPathInfo();
            if (path.startsWith("/carts")) {
            	CartService cartService = new CartService();
                cartService.handleDelete(request, response);
            } 
        } catch (Exception e) {
            logError("DELETE request failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    private void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", errorMessage));
    }
}