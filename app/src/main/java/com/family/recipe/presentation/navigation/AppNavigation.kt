package com.family.recipe.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.family.recipe.presentation.screens.detail.DetailScreen
import com.family.recipe.presentation.screens.home.HomeScreen
import com.family.recipe.presentation.screens.recipes.RecipesScreen
import com.family.recipe.presentation.screens.search.SearchScreen
import com.family.recipe.presentation.screens.upload.UploadScreen

object Routes {
    const val HOME = "home"
    const val RECIPES = "recipes/{categoryId}"
    const val UPLOAD = "upload/{categoryId}"
    const val EDIT = "edit/{categoryId}/{recipeId}"
    const val DETAIL = "detail/{recipeId}"
    const val SEARCH = "search"

    fun recipes(categoryId: String) = "recipes/$categoryId"
    fun upload(categoryId: String) = "upload/$categoryId"
    fun edit(categoryId: String, recipeId: String) = "edit/$categoryId/$recipeId"
    fun detail(recipeId: String) = "detail/$recipeId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onCategoryClick = { navController.navigate(Routes.recipes(it)) },
                onSearchClick = { navController.navigate(Routes.SEARCH) }
            )
        }

        composable(
            Routes.RECIPES,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { entry ->
            val categoryId = entry.arguments?.getString("categoryId") ?: ""
            RecipesScreen(
                categoryId = categoryId,
                onRecipeClick = { navController.navigate(Routes.detail(it)) },
                onAddRecipe = { navController.navigate(Routes.upload(categoryId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.UPLOAD,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { entry ->
            val categoryId = entry.arguments?.getString("categoryId") ?: ""
            UploadScreen(
                categoryId = categoryId,
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.EDIT,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("recipeId") { type = NavType.StringType }
            )
        ) { entry ->
            val categoryId = entry.arguments?.getString("categoryId") ?: ""
            val recipeId = entry.arguments?.getString("recipeId") ?: ""
            UploadScreen(
                categoryId = categoryId,
                editingRecipeId = recipeId,
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { entry ->
            val recipeId = entry.arguments?.getString("recipeId") ?: ""
            DetailScreen(
                recipeId = recipeId,
                onBack = { navController.popBackStack() },
                onEdit = { id, catId ->
                    navController.navigate(Routes.edit(catId, id))
                }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onRecipeClick = { navController.navigate(Routes.detail(it)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
