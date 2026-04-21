package com.family.recipe.presentation.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.recipe.domain.model.Recipe
import com.family.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<Recipe> = emptyList(),
    val isSearching: Boolean = false
)

class SearchViewModel(private val repository: RecipeRepository) : ViewModel() {
    var state by mutableStateOf(SearchUiState())
        private set

    private val _queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        state = state.copy(results = emptyList(), isSearching = false)
                    } else {
                        state = state.copy(isSearching = true)
                        repository.searchRecipes(query).collect { results ->
                            state = state.copy(results = results, isSearching = false)
                        }
                    }
                }
        }
    }

    fun onQueryChange(query: String) {
        state = state.copy(query = query)
        _queryFlow.value = query
    }
}
