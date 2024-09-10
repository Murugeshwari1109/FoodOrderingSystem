package dao;

import model.Food;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseUtil;

public class FoodDAO {

    public int addFood(Food food) throws SQLException {
        String sql = "INSERT INTO Foods (food_name, food_type, spice_level, meal_type, meal_course_type_id, cusine_id, sub_category_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, food.getFoodName());
            stmt.setString(2, food.getFoodType()); 
            stmt.setString(3, food.getSpiceLevel());
            stmt.setString(4, food.getMealType()); // Assuming mealType is a JSON array in string format
            stmt.setInt(5, food.getMealCourseTypeId());
            stmt.setInt(6, food.getCusineId());
            stmt.setInt(7, food.getSubCategoryId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Food> getAllFoods() throws SQLException {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT * FROM Foods";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Food food = new Food(
                        rs.getInt("food_id"),
                        rs.getString("food_name"),
                        rs.getString("food_type"), // food_type is a JSON string
                        rs.getString("spice_level"),
                        rs.getString("meal_type"), // meal_type is a JSON string
                        rs.getInt("meal_course_type_id"),
                        rs.getInt("cusine_id"),
                        rs.getInt("sub_category_id")
                );
                foods.add(food);
            }
        }
        return foods;
    }

    public Food getFoodById(int foodId) throws SQLException {
        String sql = "SELECT * FROM Foods WHERE food_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, foodId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Food(
                            rs.getInt("food_id"),
                            rs.getString("food_name"),
                            rs.getString("food_type"), 
                            rs.getString("spice_level"),
                            rs.getString("meal_type"),
                            rs.getInt("meal_course_type_id"),
                            rs.getInt("cusine_id"),
                            rs.getInt("sub_category_id")
                    );
                }
            }
        }
        return null;
    }

    public void updateFood(Food food) throws SQLException {
        String sql = "UPDATE Foods SET food_name = ?, food_type = ?, spice_level = ?, meal_type = ?, meal_course_type_id = ?, cusine_id = ?, sub_category_id = ? WHERE food_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, food.getFoodName());
            stmt.setString(2, food.getFoodType()); // Assuming foodType is a JSON array in string format
            stmt.setString(3, food.getSpiceLevel());
            stmt.setString(4, food.getMealType()); // Assuming mealType is a JSON array in string format
            stmt.setInt(5, food.getMealCourseTypeId());
            stmt.setInt(6, food.getCusineId());
            stmt.setInt(7, food.getSubCategoryId());
            stmt.setInt(8, food.getFoodId());
            stmt.executeUpdate();
        }
    }

    public String getMealCourseTypeNameById(int mealCourseTypeId) throws SQLException {
        String query = "SELECT meal_course_type_name FROM mealcoursetypes WHERE meal_course_type_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, mealCourseTypeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("meal_course_type_name");
                } else {
                    return "Unknown";
                }
            }
        }
    }

    public String getCusineNameById(int cusineId) throws SQLException {
        String query = "SELECT cusine_name FROM cusines WHERE cusine_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cusineId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("cusine_name");
                } else {
                    return "Unknown"; 
                }
            }
        }
    }

    public String getSubCategoryNameById(int subCategoryId) throws SQLException {
        String query = "SELECT sub_category_name FROM subcategories WHERE sub_category_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, subCategoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("sub_category_name");
                } else {
                    return "Unknown";
                }
            }
        }
    }
}
