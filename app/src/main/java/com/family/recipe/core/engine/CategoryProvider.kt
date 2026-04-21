package com.family.recipe.core.engine

import com.family.recipe.R
import com.family.recipe.domain.model.RecipeCategory

object CategoryProvider {
    val categories = listOf(
        RecipeCategory(id = "drinks", name = "饮料", iconResId = R.drawable.ic_drinks),
        RecipeCategory(id = "hot_dishes", name = "热菜", iconResId = R.drawable.ic_hot_dishes),
        RecipeCategory(id = "cold_dishes", name = "凉菜", iconResId = R.drawable.ic_cold_dishes),
        RecipeCategory(id = "bbq", name = "烧烤", iconResId = R.drawable.ic_bbq),
        RecipeCategory(id = "soups", name = "汤类", iconResId = R.drawable.ic_soups)
    )

    fun getById(id: String): RecipeCategory? = categories.find { it.id == id }
}
