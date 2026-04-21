package com.family.recipe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun observeAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun observeRecipesByCategory(categoryId: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun observeFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Query("SELECT * FROM recipe_content WHERE recipeId = :recipeId ORDER BY sortOrder ASC")
    fun observeContentByRecipeId(recipeId: String): Flow<List<RecipeContentEntity>>

    @Query("SELECT * FROM recipe_content WHERE recipeId = :recipeId ORDER BY sortOrder ASC")
    suspend fun getContentByRecipeId(recipeId: String): List<RecipeContentEntity>

    @Query("SELECT * FROM ingredients WHERE recipeId = :recipeId ORDER BY sortOrder ASC")
    suspend fun getIngredientsByRecipeId(recipeId: String): List<IngredientEntity>

    @Insert
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert
    suspend fun insertContent(content: RecipeContentEntity)

    @Insert
    suspend fun insertContentList(contents: List<RecipeContentEntity>)

    @Insert
    suspend fun insertIngredientList(ingredients: List<IngredientEntity>)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: String)

    @Query("DELETE FROM recipe_content WHERE recipeId = :recipeId")
    suspend fun deleteContentByRecipeId(recipeId: String)

    @Query("DELETE FROM ingredients WHERE recipeId = :recipeId")
    suspend fun deleteIngredientsByRecipeId(recipeId: String)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE recipes SET categoryId = :categoryId, name = :name, description = :description, coverImageUri = :coverImageUri WHERE id = :id")
    suspend fun updateRecipeBasicInfo(id: String, categoryId: String, name: String, description: String, coverImageUri: String?)

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>
}
