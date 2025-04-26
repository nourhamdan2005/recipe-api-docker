package projectoop.recipeApi.controller;

import java.util.*;
import jakarta.validation.Valid;
import projectoop.recipeApi.model.Recipe;
import projectoop.recipeApi.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    // ✅ POST /recipes - Create a new recipe
    @PostMapping
    public Recipe createRecipe(@Valid @RequestBody Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    // ✅ GET /recipes - Get all recipes
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // ✅ GET /recipes/{id} - Get recipe by ID
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        return recipe.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ PUT /recipes/{id} - Update recipe by ID
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody Recipe updatedRecipe) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        if (recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            recipe.setTitle(updatedRecipe.getTitle());
            recipe.setIngredients(updatedRecipe.getIngredients());
            recipe.setInstructions(updatedRecipe.getInstructions());
            recipe.setCookingTime(updatedRecipe.getCookingTime());
            recipe.setCategory(updatedRecipe.getCategory());
            recipeRepository.save(recipe);
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ DELETE /recipes/{id} - Delete recipe by ID with success message
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRecipe(@PathVariable String id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Recipe deleted successfully!");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ GET /recipes/page - Pagination and Sorting
    @GetMapping("/page")
    public Page<Recipe> getRecipesWithPagination(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "title") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return recipeRepository.findAll(pageable);
    }

    // ✅ GET /recipes/search?title=x - Search recipes by title
    @GetMapping("/search")
    public List<Recipe> searchRecipesByTitle(@RequestParam String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title);
    }
    
    
    @GetMapping("/filter")
public List<Recipe> filterRecipes(
    @RequestParam(required = false) String category,
    @RequestParam(required = false) Integer minTime,
    @RequestParam(required = false) Integer maxTime
) {
    List<Recipe> allRecipes = recipeRepository.findAll();
    
    return allRecipes.stream()
            .filter(recipe -> {
                boolean matches = true;
                if (category != null && !recipe.getCategory().equalsIgnoreCase(category)) {
                    matches = false;
                }
                if (minTime != null && recipe.getCookingTime() < minTime) {
                    matches = false;
                }
                if (maxTime != null && recipe.getCookingTime() > maxTime) {
                    matches = false;
                }
                return matches;
            })
            .toList();
}
    
    
    
}