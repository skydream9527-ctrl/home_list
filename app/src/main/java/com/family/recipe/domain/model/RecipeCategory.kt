package com.family.recipe.domain.model

data class RecipeCategory(
    val id: String,
    val name: String,
    val iconResId: Int,
    val recipes: List<Recipe> = emptyList()
)

data class Recipe(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String = "",
    val coverImageUri: String? = null,
    val isFavorite: Boolean = false,
    val ingredients: List<Ingredient> = emptyList(),
    val contentItems: List<RecipeContent> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Ingredient(
    val id: String = "",
    val name: String,
    val amount: String
)

sealed interface RecipeContent {
    data class Step(val value: String) : RecipeContent
    data class Text(val value: String) : RecipeContent
    data class Image(val uri: String, val caption: String = "") : RecipeContent
    data class Video(val uri: String, val caption: String = "") : RecipeContent
}
