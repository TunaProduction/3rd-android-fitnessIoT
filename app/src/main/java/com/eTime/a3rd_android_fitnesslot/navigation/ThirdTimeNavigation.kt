package com.eTime.a3rd_android_fitnesslot.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.etime.auth_presentation.login.LoginScreen
import com.etime.training_presentation.TrainingScreen

@Composable
fun ThirdTimeNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Route.LOGIN
    ) {
        composable(Route.LOGIN) {
            LoginScreen(
                onNextClick = {
                    navController.navigate(Route.TRAINING)
                }
            )
        }

        composable(Route.TRAINING){
            TrainingScreen()
        }
    }
}