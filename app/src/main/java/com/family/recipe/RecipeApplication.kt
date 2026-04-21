package com.family.recipe

import android.app.Application
import com.family.recipe.core.engine.CategoryProvider
import com.family.recipe.core.di.appModule
import com.family.recipe.data.local.RecipeDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RecipeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RecipeDatabase.getInstance(this)
        startKoin {
            androidLogger()
            androidContext(this@RecipeApplication)
            modules(appModule)
        }
    }
}
