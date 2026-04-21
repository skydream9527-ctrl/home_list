package com.family.recipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import com.family.recipe.presentation.navigation.AppNavigation
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                MaterialTheme(colorScheme = darkColorScheme()) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}
