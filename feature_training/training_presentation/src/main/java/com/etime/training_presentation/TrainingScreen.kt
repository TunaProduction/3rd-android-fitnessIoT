package com.etime.training_presentation

import androidx.compose.foundation.border
import com.etime.core_ui.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.res.painterResource
import com.etime.core_ui.LocalSpacing
import com.etime.core_ui.components.TTBattery
import com.etime.core_ui.components.TTButton
import com.etime.core_ui.components.TTCircleIcon
import com.etime.training_presentation.data.AppData
import com.etime.training_presentation.data.Profile
import com.etime.training_presentation.profile.ProfileViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onHistorialNavigation: () -> Unit,
    onStartTraining: () -> Unit,
    onStopTraining: () -> Unit,
    onRequestConnectionClick: () -> Unit,
    onProfileClick: () -> Unit
){

    val isConnected = trainingViewModel.isConnected.collectAsState()
    val isTrainingRunning = trainingViewModel.isTrainingRunning.collectAsState()
    val deviceId = profileViewModel.deviceId.collectAsState()

    LaunchedEffect(key1 = true) {
        profileViewModel.createUser(
            Profile(
                userType = "",
                name = "",
                weight = "",
                height = "",
                age = ""
            )
        )
        profileViewModel.getDeviceId()
    }

    Column (
        modifier = Modifier.padding(top = LocalSpacing.current.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(!deviceId.value.isNullOrEmpty()){
            val batteryLevel = trainingViewModel.connectedDeviceBattery.collectAsState()
            lastConnectedDevice(
                isConnected = isConnected.value,
                deviceId = deviceId.value,
                batteryLevel = batteryLevel.value
            ) {
                trainingViewModel.connectDeviceByString(deviceId.value)
            }
        }

        SelectionSectionContainer(
            isConnected,
            isTrainingRunning,
            onRequestConnectionClick,
            onStartTraining = {
                trainingViewModel.changeTrainingStatus(
                    AppData(runningTraining = true)
                )
            },
            onStopTraining = {
                trainingViewModel.changeTrainingStatus(
                    AppData(runningTraining = false)
                )
            },
            onHistorialNavigation,
            onProfileClick
        )
    }
}

@Composable
fun lastConnectedDevice(
    isConnected: Boolean,
    deviceId: String,
    batteryLevel: Int,
    connect: () -> Unit,
) {
    Column(
        modifier = Modifier
            .border(
                width = LocalSpacing.current.two,
                color = Color.DarkGray,
                shape = RoundedCornerShape(
                    corner = CornerSize(LocalSpacing.current.spaceSmall)
                )
            )
            .fillMaxWidth(0.8f)
            .padding(LocalSpacing.current.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(!isConnected) {
            Text(text = "Last connected device: $deviceId")
            TTButton(text = "Connect") {
                connect()
            }
        } else {
            Text(text = "Connected device: $deviceId")
            Row (verticalAlignment = Alignment.CenterVertically){
                Text(text = "Battery Level: $batteryLevel%")
                Spacer(modifier = Modifier.width(LocalSpacing.current.spaceExtraSmall))
                TTBattery(
                    level = batteryLevel,
                    size = LocalSpacing.current.spaceMedium
                )
            }
        }

    }
}

@Composable
private fun SelectionSectionContainer(
    isConnected: State<Boolean>,
    isTrainingRunning: State<Boolean>,
    onRequestConnectionClick: () -> Unit,
    onStartTraining: () -> Unit,
    onStopTraining: () -> Unit,
    onHistorialNavigation: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.spaceLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TTCircleIcon(iconId = R.drawable.ic_footbal)

            if (isConnected.value.not()) {
                TTButton(
                    text = "Connect Device"
                ) {
                    onRequestConnectionClick()
                }
            }

            if(isTrainingRunning.value.not()) {
                TTButton(
                    text = "Start Training",
                    isEnabled = isConnected.value
                ) {
                    onStartTraining()
                }
            } else {
                TTButton(
                    text = "Stop Training",
                    isEnabled = isConnected.value
                ) {
                    onStopTraining()
                }
            }


            /*TTButton(
                text = "Start Training",
                isEnabled = isConnected.value
            ) {
                onNextClick()
            }*/

            Text(
                text = "View historial",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable {
                    onHistorialNavigation()
                }
            )

            Row(
                modifier = Modifier.clickable { onProfileClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(LocalSpacing.current.spaceMedium),
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "Profile icon",
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.width(LocalSpacing.current.spaceSmall))

                Text(
                    text = "Edit Profile",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

        }
    }
}

