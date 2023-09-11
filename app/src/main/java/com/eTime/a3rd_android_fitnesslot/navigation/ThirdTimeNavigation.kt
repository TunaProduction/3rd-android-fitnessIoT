package com.eTime.a3rd_android_fitnesslot.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.etime.auth_presentation.login.LoginScreen
import com.etime.training_presentation.TrackTrainingScreen
import com.etime.training_presentation.TrainingScreen
import com.etime.training_presentation.TrainingViewModel

@Composable
fun ThirdTimeNavigation() {
    val navController = rememberNavController()
    val trainingViewModel: TrainingViewModel = hiltViewModel()
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
            TrainingScreen(
                trainingViewModel = trainingViewModel,
                onNextClick = {
                    navController.navigate(Route.TRACK_TRAINING)
                }
            )
        }

        composable(Route.TRACK_TRAINING){
            TrackTrainingScreen(
                trainingViewModel = trainingViewModel,
                backNavigation = {
                    navController.popBackStack()
                }
            )
        }
    }
}