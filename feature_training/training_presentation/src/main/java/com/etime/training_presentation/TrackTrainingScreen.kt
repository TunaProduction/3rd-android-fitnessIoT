package com.etime.training_presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import java.sql.Date
import java.sql.Timestamp

@Composable
fun TrackTrainingScreen(
    trainingViewModel: TrainingViewModel,
    backNavigation: () -> Unit
) {

    val deviceId = trainingViewModel.connectedDeviceId.collectAsState()
    val hrData = trainingViewModel.hrData.collectAsState()
    val accData = trainingViewModel.accData.collectAsState()
    val acceleration = trainingViewModel.acceleration.collectAsState()

    if(deviceId.value.isEmpty()) {
        backNavigation()
        return
    }

    LaunchedEffect(true) {
        trainingViewModel.trackStreamTraining(deviceId.value)
    }

    trainingViewModel.getTraining(deviceId.value)

    Column {
        hrData.value?.let {
            Row {
                Text(
                    text = "Hr: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = it.hr.toString())
            }

            Row {
                Text(
                    text = "Contact Status: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = it.contactStatus.toString())
            }

            Row {
                Text(
                    text = "Contact Status Supported: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = it.contactStatusSupported.toString())
            }

            Row {
                Text(
                    text = "RRSMS: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = it.rrsMs.toString())
            }

            Row {
                Text(
                    text = "RR Available: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = it.rrAvailable.toString())
            }
        }

        Text(
            text = "--ACC--",
            fontWeight = FontWeight.Bold
        )

        accData.value?.let {
            Row {
                Text(
                    text = "X: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = it.x.toString())
            }

            Row {
                Text(
                    text = "Y: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = it.y.toString())
            }

            Row {
                Text(
                    text = "Z: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = it.z.toString())
            }

            val stamp = Timestamp(it.timeStamp)
            val date = Date(stamp.time)
            Row {
                Text(
                    text = "TimeStamp: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = date.toString())
            }
        }

        Row {
            Text(
                text = "Total Linear Acceleration Change (m/s²): ",
                fontWeight = FontWeight.Bold
            )
            Text(text = acceleration.value.toString())
        }
    }

}