package model;

public class MealCourseType {
    private int mealCourseTypeId;
    private String mealCourseTypeName;

    public MealCourseType() {}

    public MealCourseType(int mealCourseTypeId, String mealCourseTypeName) {
        this.mealCourseTypeId = mealCourseTypeId;
        this.mealCourseTypeName = mealCourseTypeName;
    }

    public int getMealCourseTypeId() {
        return mealCourseTypeId;
    }

    public void setMealCourseTypeId(int mealCourseTypeId) {
        this.mealCourseTypeId = mealCourseTypeId;
    }

    public String getMealCourseTypeName() {
        return mealCourseTypeName;
    }

    public void setMealCourseTypeName(String mealCourseTypeName) {
        this.mealCourseTypeName = mealCourseTypeName;
    }
}
