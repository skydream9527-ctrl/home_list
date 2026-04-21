package com.family.recipe.data.repository

import com.family.recipe.core.engine.CategoryProvider
import com.family.recipe.data.local.IngredientEntity
import com.family.recipe.data.local.RecipeContentEntity
import com.family.recipe.data.local.RecipeDao
import com.family.recipe.data.local.RecipeEntity
import com.family.recipe.domain.model.Ingredient
import com.family.recipe.domain.model.Recipe
import com.family.recipe.domain.model.RecipeCategory
import com.family.recipe.domain.model.RecipeContent
import com.family.recipe.domain.repository.RecipeRepository
import com.family.recipe.domain.repository.RecipeWithContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RecipeRepositoryImpl(
    private val dao: RecipeDao,
    private val categories: List<RecipeCategory>
) : RecipeRepository {

    override fun observeCategories(): Flow<List<RecipeCategory>> {
        return dao.observeAllRecipes().map { recipes ->
            categories.map { category ->
                category.copy(recipes = recipes.filter { it.categoryId == category.id }
                    .map { it.toDomain() })
            }
        }
    }

    override fun observeRecipesByCategory(categoryId: String): Flow<List<Recipe>> {
        return dao.observeRecipesByCategory(categoryId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeFavoriteRecipes(): Flow<List<Recipe>> {
        return dao.observeFavoriteRecipes().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getRecipeWithContent(id: String): Result<RecipeWithContent> {
        return try {
            val entity = dao.getRecipeById(id) ?: return Result.failure(Exception("菜谱不存在"))
            val ingredients = dao.getIngredientsByRecipeId(id).map { it.toDomain() }
            val contents = dao.getContentByRecipeId(id).map { it.toDomain() }
            Result.success(RecipeWithContent(entity.toDomain(), ingredients, contents))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveRecipe(
        categoryId: String,
        name: String,
        description: String,
        coverImageUri: String?,
        ingredients: List<Ingredient>,
        contentItems: List<RecipeContent>
    ) {
        val recipeId = UUID.randomUUID().toString()
        dao.insertRecipe(
            RecipeEntity(
                id = recipeId,
                categoryId = categoryId,
                name = name,
                description = description,
                coverImageUri = coverImageUri,
                isFavorite = false,
                createdAt = System.currentTimeMillis()
            )
        )
        insertIngredientsAndContents(recipeId, ingredients, contentItems)
    }

    override suspend fun updateRecipe(
        id: String,
        categoryId: String,
        name: String,
        description: String,
        coverImageUri: String?,
        ingredients: List<Ingredient>,
        contentItems: List<RecipeContent>
    ) {
        dao.updateRecipeBasicInfo(id, categoryId, name, description, coverImageUri ?: "")
        dao.deleteContentByRecipeId(id)
        dao.deleteIngredientsByRecipeId(id)
        insertIngredientsAndContents(id, ingredients, contentItems)
    }

    private suspend fun insertIngredientsAndContents(
        recipeId: String,
        ingredients: List<Ingredient>,
        contentItems: List<RecipeContent>
    ) {
        if (ingredients.isNotEmpty()) {
            dao.insertIngredientList(ingredients.mapIndexed { index, ing ->
                IngredientEntity(
                    id = ing.id.ifEmpty { UUID.randomUUID().toString() },
                    recipeId = recipeId,
                    name = ing.name,
                    amount = ing.amount,
                    sortOrder = index
                )
            })
        }
        if (contentItems.isNotEmpty()) {
            dao.insertContentList(contentItems.mapIndexed { index, content ->
                val (type, value, caption) = when (content) {
                    is RecipeContent.Step -> Triple("step", content.value, "")
                    is RecipeContent.Text -> Triple("text", content.value, "")
                    is RecipeContent.Image -> Triple("image", content.uri, content.caption)
                    is RecipeContent.Video -> Triple("video", content.uri, content.caption)
                }
                RecipeContentEntity(
                    id = UUID.randomUUID().toString(),
                    recipeId = recipeId,
                    type = type,
                    content = value,
                    caption = caption,
                    sortOrder = index
                )
            })
        }
    }

    override suspend fun deleteRecipe(id: String) {
        dao.deleteContentByRecipeId(id)
        dao.deleteIngredientsByRecipeId(id)
        dao.deleteRecipeById(id)
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        return dao.searchRecipes(query).map { list ->
            list.map { it.toDomain() }
        }
    }

    private fun RecipeEntity.toDomain() = Recipe(
        id = id,
        categoryId = categoryId,
        name = name,
        description = description,
        coverImageUri = coverImageUri,
        isFavorite = isFavorite,
        createdAt = createdAt
    )

    private fun IngredientEntity.toDomain() = Ingredient(
        id = id,
        name = name,
        amount = amount
    )

    private fun RecipeContentEntity.toDomain(): RecipeContent = when (type) {
        "step" -> RecipeContent.Step(content)
        "text" -> RecipeContent.Text(content)
        "image" -> RecipeContent.Image(content, caption)
        "video" -> RecipeContent.Video(content, caption)
        else -> RecipeContent.Text(content)
    }
}
