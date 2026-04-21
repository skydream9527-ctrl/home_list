package com.family.recipe.presentation.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.recipe.domain.model.Ingredient
import com.family.recipe.domain.model.RecipeContent
import com.family.recipe.domain.repository.RecipeRepository
import com.family.recipe.domain.repository.RecipeWithContent
import kotlinx.coroutines.launch

data class DetailUiState(
    val recipeName: String = "",
    val recipeDescription: String = "",
    val coverImageUri: String? = null,
    val isFavorite: Boolean = false,
    val categoryId: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val contents: List<RecipeContent> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class DetailViewModel(
    private val repository: RecipeRepository,
    private val recipeId: String
) : ViewModel() {

    var state by mutableStateOf(DetailUiState())
        private set

    init {
        loadRecipe()
    }

    fun loadRecipe() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            repository.getRecipeWithContent(recipeId)
                .onSuccess { data: RecipeWithContent ->
                    state = DetailUiState(
                        recipeName = data.recipe.name,
                        recipeDescription = data.recipe.description,
                        coverImageUri = data.recipe.coverImageUri,
                        isFavorite = data.recipe.isFavorite,
                        categoryId = data.recipe.categoryId,
                        ingredients = data.ingredients,
                        contents = data.contents,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    state = state.copy(isLoading = false, error = e.message ?: "加载失败")
                }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.toggleFavorite(recipeId, !state.isFavorite)
            state = state.copy(isFavorite = !state.isFavorite)
        }
    }

    fun deleteRecipe(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteRecipe(recipeId)
            onSuccess()
        }
    }
}
