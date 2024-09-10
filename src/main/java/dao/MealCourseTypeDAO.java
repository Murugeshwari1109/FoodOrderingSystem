package dao;

import model.MealCourseType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseUtil;

public class MealCourseTypeDAO {

    public int addMealCourseType(MealCourseType mealCourseType) throws SQLException {
        String sql = "INSERT INTO MealCourseTypes (meal_course_type_name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, mealCourseType.getMealCourseTypeName());
            stmt.executeUpdate();

            // Retrieve generated keys
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<MealCourseType> getAllMealCourseTypes() throws SQLException {
        List<MealCourseType> mealCourseTypes = new ArrayList<>();
        String sql = "SELECT * FROM MealCourseTypes";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MealCourseType mealCourseType = new MealCourseType(
                    rs.getInt("meal_course_type_id"),
                    rs.getString("meal_course_type_name")
                );
                mealCourseTypes.add(mealCourseType);
            }
        }
        return mealCourseTypes;
    }

    public MealCourseType getMealCourseTypeById(int mealCourseTypeId) throws SQLException {
        String sql = "SELECT * FROM MealCourseTypes WHERE meal_course_type_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mealCourseTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MealCourseType(
                        rs.getInt("meal_course_type_id"),
                        rs.getString("meal_course_type_name")
                    );
                }
            }
        }
        return null;
    }

    public void updateMealCourseType(MealCourseType mealCourseType) throws SQLException {
        String sql = "UPDATE MealCourseTypes SET meal_course_type_name = ? WHERE meal_course_type_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mealCourseType.getMealCourseTypeName());
            stmt.setInt(2, mealCourseType.getMealCourseTypeId());
            stmt.executeUpdate();
        }
    }
}
