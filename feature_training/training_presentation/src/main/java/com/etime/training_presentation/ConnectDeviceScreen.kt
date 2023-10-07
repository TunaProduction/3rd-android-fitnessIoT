package com.etime.training_presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.polar.sdk.api.model.PolarDeviceInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ConnectDeviceScreen(
    trainingViewModel: TrainingViewModel = hiltViewModel(),
    onConnectedDevice: () -> Unit
){

    val isConnected = trainingViewModel.isConnected.collectAsState()

    if(isConnected.value) {
        LaunchedEffect(true){
            onConnectedDevice()
        }
    }

    Column (horizontalAlignment = Alignment.CenterHorizontally){
        Button(onClick = { trainingViewModel.searchDevice() }) {
            Text(text = "Find Devices")
        }
        DevicesList(trainingViewModel = trainingViewModel)
    }

}

@OptIn(ExperimentalTime::class)
@Composable
fun DevicesList(trainingViewModel: TrainingViewModel){
    val foundDevicesList = trainingViewModel.polarDevicesList.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(foundDevicesList.value) { device ->
            DeviceRow(device) {
                trainingViewModel.connectDevice(device)
            }
        }
    }
}

@Composable
fun DeviceRow(
    deviceInfo: PolarDeviceInfo,
    action : (PolarDeviceInfo) -> Unit = {}
) {
    Column (
        modifier = Modifier.clickable { action(deviceInfo) }
    ) {
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