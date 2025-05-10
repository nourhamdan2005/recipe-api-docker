package projectoop.recipeApi.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import projectoop.recipeApi.model.Recipe;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    List<Recipe> findByTitleContainingIgnoreCase(String title);
    List<Recipe> findByCreatedBy(String createdBy);  // ✅ This line is required
}