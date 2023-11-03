package com.etime.training_presentation.data

data class TrainingHistorial(
    val trainings: List<FinishedTrainingData>
)

data class FinishedTrainingData (
    val folderName: String,
    val fileName: String,
    val avgHr: String,
    val avgAcceleration: String,
    val falls: Int,
    val steps: Int,
    val motionTime: String,
    val totalTime: String,
    val timerWithHR: List<TimeWithHeartRate>,
    val profile: Profile? = null
)

data class TimeWithHeartRate(
    val time: String,
    val hr: String
)