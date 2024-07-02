package com.example.ajiri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ajiri.ui.theme.AjiriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AjiriTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { LoginScreen(navController) }
                        composable("signup") { SignUpScreen(navController) }
                        composable("main") { MainScreen(navController) }
                        composable("categories") { CategoriesScreen(navController) }
                        composable("taskerList/{categoryId}") { backStackEntry ->
                            TaskerListScreen(
                                navController,
                                backStackEntry.arguments?.getString("categoryId") ?: ""
                            )}
                        composable("taskerDetail/{taskerId}") { backStackEntry ->
                            TaskerDetailScreen(
                                navController,
                                backStackEntry.arguments?.getString("taskerId") ?: ""
                            )}
                        composable("userDashboard/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                            UserDashboardScreen(navController, userId)
                        }
                        composable("appointment/{taskerId}") { backStackEntry ->
                            val taskerId = backStackEntry.arguments?.getString("taskerId") ?: ""
                            AppointmentScreen(navController, taskerId)
                        }
                    }
                }
            }
        }
    }
}