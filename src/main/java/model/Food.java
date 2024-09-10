package model;

import org.json.JSONArray;
import org.json.JSONException;

public class Food {
    private int foodId;
    private String foodName;
    private String foodType; // foodType remains unchanged
    private String spiceLevel;
    private String mealType; // mealType is now JSON
    private int mealCourseTypeId;
    private int cusineId;
    private int subCategoryId;

    public Food(int foodId, String foodName, String foodType, String spiceLevel, String mealType, int mealCourseTypeId, int cusineId, int subCategoryId) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodType = foodType;
        this.spiceLevel = spiceLevel;
        this.mealType = mealType;
        this.mealCourseTypeId = mealCourseTypeId;
        this.cusineId = cusineId;
        this.subCategoryId = subCategoryId;
    }

    // Getters and setters

    public Food() {
		// TODO Auto-generated constructor stub
	}

	public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(String spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public int getMealCourseTypeId() {
        return mealCourseTypeId;
    }

    public void setMealCourseTypeId(int mealCourseTypeId) {
        this.mealCourseTypeId = mealCourseTypeId;
    }

    public int getCusineId() {
        return cusineId;
    }

    public void setCusineId(int cusineId) {
        this.cusineId = cusineId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }
    public JSONArray getMealTypeAsJsonArray() {
        try {
            return new JSONArray(mealType); 
        } catch (JSONException e) {
       
            e.printStackTrace();
            return new JSONArray(); 
        }
    }
}
