package dao;

import model.SubCategory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseUtil;

public class SubCategoryDAO {

    // Add a new subcategory
    public int addSubCategory(SubCategory subCategory) throws SQLException {
        String sql = "INSERT INTO SubCategories (sub_category_name) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subCategory.getSubCategoryName());
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

    // Retrieve all subcategories
    public List<SubCategory> getAllSubCategories() throws SQLException {
        List<SubCategory> subCategories = new ArrayList<>();
        String sql = "SELECT * FROM SubCategories";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SubCategory subCategory = new SubCategory(
                    rs.getInt("sub_category_id"),
                    rs.getString("sub_category_name")
                    
                );
                subCategories.add(subCategory);
            }
        }
        return subCategories;
    }

    // Retrieve a subcategory by ID
    public SubCategory getSubCategoryById(int subCategoryId) throws SQLException {
        String sql = "SELECT * FROM SubCategories WHERE sub_category_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subCategoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new SubCategory(
                        rs.getInt("sub_category_id"),
                        rs.getString("sub_category_name")
                       
                    );
                }
            }
        }
        return null;
    }

    // Update a subcategory
    public void updateSubCategory(SubCategory subCategory) throws SQLException {
        String sql = "UPDATE SubCategories SET sub_category_name = ? WHERE sub_category_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subCategory.getSubCategoryName());
            stmt.setInt(2, subCategory.getSubCategoryId());
            stmt.executeUpdate();
        }
    }
}
