package com.family.recipe.presentation.screens.upload

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.recipe.domain.model.Ingredient
import com.family.recipe.domain.model.Recipe
import com.family.recipe.domain.model.RecipeContent
import com.family.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.launch

class UploadViewModel(
    private val repository: RecipeRepository,
    private val categoryId: String
) : ViewModel() {

    var isSaving by mutableStateOf(false)
        private set

    fun loadRecipeForEdit(
        recipeId: String,
        onLoaded: (Recipe, List<Ingredient>, List<RecipeContent>) -> Unit
    ) {
        viewModelScope.launch {
            repository.getRecipeWithContent(recipeId)
                .onSuccess { data ->
                    onLoaded(data.recipe, data.ingredients, data.contents)
                }
        }
    }

    fun saveRecipe(
        name: String,
        description: String,
        coverImageUri: String?,
        ingredients: List<Ingredient>,
        contentItems: List<RecipeContent>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isSaving = true
            try {
                repository.saveRecipe(categoryId, name, description, coverImageUri, ingredients, contentItems)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "保存失败")
            } finally {
                isSaving = false
            }
        }
    }

    fun updateRecipe(
        recipeId: String,
        name: String,
        description: String,
        coverImageUri: String?,
        ingredients: List<Ingredient>,
        contentItems: List<RecipeContent>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isSaving = true
            try {
                repository.updateRecipe(recipeId, categoryId, name, description, coverImageUri, ingredients, contentItems)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "更新失败")
            } finally {
                isSaving = false
            }
        }
    }
}
