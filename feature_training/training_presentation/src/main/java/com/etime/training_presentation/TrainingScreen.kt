package com.etime.training_presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.model.PolarDeviceInfo


// ATTENTION! Replace with the device ID from your device.
private var deviceId = "8C4E5023"


@Composable
fun TrainingScreen(trainingViewModel: TrainingViewModel = hiltViewModel()){
    Text(text = "From Training Screen")
    Column (horizontalAlignment = Alignment.CenterHorizontally){
        Button(onClick = { trainingViewModel.searchDevice() }) {
            Text(text = "Find Devices")
        }
        DevicesList(trainingViewModel = trainingViewModel)
    }

}

@Composable
fun DevicesList(trainingViewModel: TrainingViewModel){
    val foundDevicesList = trainingViewModel.polarDevicesList.collectAsState()

    LazyColumn() {
        items(foundDevicesList.value) { device ->
            DeviceRow(device) {
                // hVModel.requestConnectToResult(it)
            }
        }
    }
}

@Composable
fun DeviceRow(
    deviceInfo: PolarDeviceInfo,
    action : (PolarDeviceInfo) -> Unit = {}
) {
    Column {
        Row {
            Text(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                text = "Nombre: "
            )

            Text(
                color = Color.Black,
                text = deviceInfo.name
            )
        }

        Row {
            Text(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                text = "Id: "
            )

            Text(
                color = Color.Black,
                text = deviceInfo.deviceId
            )
        }

        Text(
            color = if(deviceInfo.isConnectable) Color.Green else Color.Red,
            text = if(deviceInfo.isConnectable) "Conectable" else "No conectable"
        )
    }

}

