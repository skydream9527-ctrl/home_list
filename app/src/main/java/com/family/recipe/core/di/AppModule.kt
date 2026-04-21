package com.family.recipe.core.di

import com.family.recipe.core.engine.CategoryProvider
import com.family.recipe.data.local.RecipeDatabase
import com.family.recipe.data.repository.RecipeRepositoryImpl
import com.family.recipe.domain.repository.RecipeRepository
import com.family.recipe.presentation.screens.detail.DetailViewModel
import com.family.recipe.presentation.screens.home.HomeViewModel
import com.family.recipe.presentation.screens.recipes.RecipesViewModel
import com.family.recipe.presentation.screens.search.SearchViewModel
import com.family.recipe.presentation.screens.upload.UploadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { RecipeDatabase.getInstance(get()).recipeDao() }
    single { CategoryProvider.categories }

    single<RecipeRepository> {
        RecipeRepositoryImpl(get(), get())
    }

    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)

    viewModel { (categoryId: String) ->
        RecipesViewModel(get(), categoryId)
    }

    viewModel { (categoryId: String) ->
        UploadViewModel(get(), categoryId)
    }

    viewModel { (recipeId: String) ->
        DetailViewModel(get(), recipeId)
    }
}
