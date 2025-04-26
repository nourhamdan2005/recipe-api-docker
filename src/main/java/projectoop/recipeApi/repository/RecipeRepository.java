/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package projectoop.recipeApi.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import projectoop.recipeApi.model.Recipe;





@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    // ✅ Add this method to enable search by title
    List<Recipe> findByTitleContainingIgnoreCase(String title);
}