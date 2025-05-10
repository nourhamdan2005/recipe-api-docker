package projectoop.recipeApi.controller;

import java.util.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import projectoop.recipeApi.model.Recipe;
import projectoop.recipeApi.repository.RecipeRepository;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    // ✅ Create recipe - Admin and Client allowed
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PostMapping
    public Recipe createRecipe(@Valid @RequestBody Recipe recipe) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        recipe.setCreatedBy(currentUser);
        return recipeRepository.save(recipe);
    }

    // ✅ Get all recipes - Open to all
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // ✅ Get recipe by ID
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        return recipe.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Update recipe by ID - Admin or Owner only
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable String id, @RequestBody Recipe updatedRecipe) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        if (recipeOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Recipe recipe = recipeOptional.get();
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!recipe.getCreatedBy().equals(currentUser) && !isAdmin) {
            return ResponseEntity.status(403).body("❌ You can only update your own recipes.");
        }

        recipe.setTitle(updatedRecipe.getTitle());
        recipe.setIngredients(updatedRecipe.getIngredients());
        recipe.setInstructions(updatedRecipe.getInstructions());
        recipe.setCookingTime(updatedRecipe.getCookingTime());
        recipe.setCategory(updatedRecipe.getCategory());

        recipeRepository.save(recipe);
        return ResponseEntity.ok(recipe);
    }

    // ✅ Delete all recipes - Admin only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllRecipes() {
        recipeRepository.deleteAll();
        return ResponseEntity.ok("✅ All recipes deleted successfully.");
    }

    // ✅ Delete recipe by ID - Admin or Owner only (with logs)
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable String id) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            System.out.println("❌ Recipe not found: " + id);
            return ResponseEntity.notFound().build();
        }

        Recipe recipe = recipeOpt.get();
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        System.out.println("🔎 Deletion requested by: " + currentUser + " (ADMIN? " + isAdmin + ")");
        System.out.println("📦 Recipe created by: " + recipe.getCreatedBy());

        if (!recipe.getCreatedBy().equals(currentUser) && !isAdmin) {
            System.out.println("🚫 Not allowed to delete.");
            return ResponseEntity.status(403).body("❌ You can only delete your own recipes.");
        }

        recipeRepository.deleteById(id);
        System.out.println("✅ Deleted successfully.");
        return ResponseEntity.ok(Map.of("message", "✅ Recipe deleted successfully!"));
    }

    // ✅ Get paginated recipes
    @GetMapping("/page")
    public Page<Recipe> getRecipesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "title") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return recipeRepository.findAll(pageable);
    }

    // ✅ Search recipes by title
    @GetMapping("/search")
    public List<Recipe> searchRecipesByTitle(@RequestParam String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title);
    }

    // ✅ Filter recipes by category and time
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
    }// ✅ Sort recipes by title or cookingTime
@GetMapping("/sort")
public List<Recipe> sortRecipes(@RequestParam String by) {
    if (!by.equals("title") && !by.equals("cookingTime")) {
        return Collections.emptyList(); // or you can return ResponseEntity.badRequest()
    }
    return recipeRepository.findAll(Sort.by(by));
}
    // ✅ Get recipes created by the currently logged-in client
@PreAuthorize("hasRole('CLIENT')")
@GetMapping("/mine")
public List<Recipe> getMyRecipes() {
    String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
    return recipeRepository.findByCreatedBy(currentUser);
}
}