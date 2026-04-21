package com.family.recipe.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.recipe.domain.model.Recipe
import com.family.recipe.domain.model.RecipeCategory
import com.family.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val categories: List<RecipeCategory> = emptyList(),
    val favorites: List<Recipe> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {
    var state by mutableStateOf(HomeUiState())
        private set

    init {
        viewModelScope.launch {
            combine(
                repository.observeCategories(),
                repository.observeFavoriteRecipes()
            ) { categories, favorites ->
                HomeUiState(categories = categories, favorites = favorites, isLoading = false)
            }.collect { state = it }
        }
    }
}
