package com.eTime.a3rd_android_fitnesslot.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.etime.auth_presentation.login.LoginScreen
import com.etime.training_presentation.ConnectDeviceScreen
import com.etime.training_presentation.trackTraining.TrackTrainingScreen
import com.etime.training_presentation.TrainingScreen
import com.etime.training_presentation.TrainingViewModel
import com.etime.training_presentation.trainingHistorial.TrainingHistorialScreen
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@ExperimentalAnimationApi
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
                onHistorialNavigation = {
                    navController.navigate(Route.HISTORIAL_TRAINING)
                },
                onNextClick = {
                    navController.navigate(Route.TRACK_TRAINING)
                },
                onRequestConnectionClick = {
                    navController.navigate(Route.CONNECT_DEVICE)
                }
            )
        }

        composable(Route.CONNECT_DEVICE){
            ConnectDeviceScreen(
                trainingViewModel = trainingViewModel,
                onConnectedDevice = {
                    navController.popBackStack()
                }
            )
        }

        composable(Route.TRACK_TRAINING){
            TrackTrainingScreen(
                trainingViewModel = trainingViewModel,
                trigger = true,
                backNavigation = {
                    navController.navigate(Route.TRAINING) {
                        popUpTo(Route.TRAINING)
                    }
                }
            )
        }

        composable(Route.HISTORIAL_TRAINING) {
            TrainingHistorialScreen {
                navController.navigate(Route.TRAINING) {
                    popUpTo(Route.TRAINING)
                }
            }
        }
    }
}