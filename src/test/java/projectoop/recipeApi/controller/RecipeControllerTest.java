package projectoop.recipeApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import projectoop.recipeApi.model.Recipe;
import java.util.Arrays;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Important: Disable security filters for tests
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For converting Java objects to JSON

    @Test
    public void testCreateRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setTitle("Test Recipe");
        recipe.setIngredients(Arrays.asList("Test Ingredient 1", "Test Ingredient 2"));
        recipe.setInstructions(Arrays.asList("Step 1", "Step 2"));
        recipe.setCookingTime(10);
        recipe.setCategory("Test Category");

        mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllRecipes() throws Exception {
        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPagination() throws Exception {
        mockMvc.perform(get("/recipes/page")
                .param("page", "0")
                .param("size", "5")
                .param("sortBy", "title"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchByTitle() throws Exception {
        mockMvc.perform(get("/recipes/search")
                .param("title", "Test"))
                .andExpect(status().isOk());
    }
    @Test
public void testDeleteRecipe() throws Exception {
    // First create a recipe
    Recipe recipe = new Recipe();
    recipe.setTitle("Recipe to Delete");
    recipe.setIngredients(Arrays.asList("Item1", "Item2"));
    recipe.setInstructions(Arrays.asList("Step1", "Step2"));
    recipe.setCookingTime(15);
    recipe.setCategory("Main Dish");

    String createdRecipe = mockMvc.perform(post("/recipes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(recipe)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

    // Extract the ID
    String id = objectMapper.readTree(createdRecipe).get("id").asText();

    // Now delete the created recipe
    mockMvc.perform(delete("/recipes/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Recipe deleted successfully!"));
}
}
