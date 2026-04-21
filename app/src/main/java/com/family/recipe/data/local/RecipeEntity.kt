package com.family.recipe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val coverImageUri: String?,
    val isFavorite: Boolean = false,
    val createdAt: Long
)

@Entity(tableName = "recipe_content")
data class RecipeContentEntity(
    @PrimaryKey val id: String,
    val recipeId: String,
    val type: String, // "step", "text", "image", "video"
    val content: String,
    val caption: String,
    val sortOrder: Int
)

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: String,
    val recipeId: String,
    val name: String,
    val amount: String,
    val sortOrder: Int
)
