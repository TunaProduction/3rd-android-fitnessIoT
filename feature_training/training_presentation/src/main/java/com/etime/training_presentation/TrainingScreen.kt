package com.etime.training_presentation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl


// ATTENTION! Replace with the device ID from your device.
private var deviceId = "8C4E5023"


@Composable
fun TrainingScreen(trainingViewModel: TrainingViewModel = hiltViewModel()){
    Text(text = "From Training Screen")
    Button(onClick = { trainingViewModel.connectDevice() }) {
        Text(text = "connect device")
    }
}

