package com.family.recipe.domain.repository

import com.family.recipe.domain.model.Ingredient
import com.family.recipe.domain.model.Recipe
import com.family.recipe.domain.model.RecipeCategory
import com.family.recipe.domain.model.RecipeContent
import kotlinx.coroutines.flow.Flow

data class RecipeWithContent(
    val recipe: Recipe,
    val ingredients: List<Ingredient>,
    val contents: List<RecipeContent>
)

interface RecipeRepository {
    fun observeCategories(): Flow<List<RecipeCategory>>
    fun observeRecipesByCategory(categoryId: String): Flow<List<Recipe>>
    fun observeFavoriteRecipes(): Flow<List<Recipe>>
    suspend fun getRecipeWithContent(id: String): Result<RecipeWithContent>
    suspend fun saveRecipe(
        categoryId: String,
        name: String,
        description: String,
        coverImageUri: String?,
        ingredients: List<Ingredient>,
        contentItems: List<RecipeContent>
    )
    suspend fun updateRecipe(
        id: String,
        categoryId: String,
        name: String,
        description: String,
        coverImageUri: String?,
        ingredients: List<Ingredient>,
        contentItems: List<RecipeContent>
    )
    suspend fun deleteRecipe(id: String)
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)
    fun searchRecipes(query: String): Flow<List<Recipe>>
}
