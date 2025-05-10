package projectoop.recipeApi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "recipes")  // This maps to the MongoDB collection "recipes"
public class Recipe {

    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Ingredients are required")
    @Size(min = 1, message = "At least one ingredient is required")
    private List<String> ingredients;

    private List<String> instructions;

    @NotNull(message = "Cooking time is required")
    private int cookingTime; // in minutes

    @NotBlank(message = "Category is required")
    private String category;

    private String createdBy; // ✅ NEW FIELD

    // Constructors
    public Recipe() {}

    public Recipe(String title, List<String> ingredients, List<String> instructions, int cookingTime, String category) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cookingTime = cookingTime;
        this.category = category;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Recipe {\n" +
               "  ID: " + id + "\n" +
               "  Title: " + title + "\n" +
               "  Ingredients: " + ingredients + "\n" +
               "  Instructions: " + instructions + "\n" +
               "  Cooking Time: " + cookingTime + " minutes\n" +
               "  Category: " + category + "\n" +
               "  Created By: " + createdBy + "\n" +
               "}";
    }
}