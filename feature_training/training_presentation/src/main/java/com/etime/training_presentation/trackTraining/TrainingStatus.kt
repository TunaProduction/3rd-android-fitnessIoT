package com.etime.training_presentation.trackTraining

sealed class TrainingStatus {
    object OnGoing: TrainingStatus()
    object Paused: TrainingStatus()
    object Finished: TrainingStatus()
}