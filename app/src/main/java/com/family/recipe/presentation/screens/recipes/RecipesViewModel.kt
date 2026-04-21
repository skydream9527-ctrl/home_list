package com.family.recipe.presentation.screens.recipes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.recipe.domain.model.Recipe
import com.family.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.launch

data class RecipesUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true
)

class RecipesViewModel(
    private val repository: RecipeRepository,
    private val categoryId: String
) : ViewModel() {
    var state by mutableStateOf(RecipesUiState())
        private set

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            repository.observeRecipesByCategory(categoryId).collect { recipes ->
                state = state.copy(recipes = recipes, isLoading = false)
            }
        }
    }
}
