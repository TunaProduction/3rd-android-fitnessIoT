package com.etime.training_presentation

import android.devicelock.DeviceId
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import com.etime.core_ui.LocalSpacing
import com.etime.core_ui.components.TTButton
import com.etime.core_ui.components.TTCircleIcon
import com.etime.training_presentation.profile.ProfileViewModel
import com.patrykandpatrick.vico.compose.component.shape.chartShape
import com.polar.sdk.api.model.PolarDeviceInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onHistorialNavigation: () -> Unit,
    onNextClick: () -> Unit,
    onRequestConnectionClick: () -> Unit,
    onProfileClick: () -> Unit
){

    val isConnected = trainingViewModel.isConnected.collectAsState()
    val deviceId = profileViewModel.deviceId.collectAsState()

    LaunchedEffect(key1 = true) {
        profileViewModel.getDeviceId()
    }

    Column (
        modifier = Modifier.padding(top = LocalSpacing.current.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(deviceId.value.isNotEmpty()){
            lastConnectedDevice(
                isConnected = isConnected.value,
                deviceId = deviceId.value
            ) {
                trainingViewModel.connectDeviceByString(deviceId.value)
            }
        }

        SelectionSectionContainer(
            isConnected,
            onRequestConnectionClick,
            onNextClick,
            onHistorialNavigation,
            onProfileClick
        )
    }
}

@Composable
fun lastConnectedDevice(
    isConnected: Boolean,
    deviceId: String,
    connect: () -> Unit
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
        }

    }
}

@Composable
private fun SelectionSectionContainer(
    isConnected: State<Boolean>,
    onRequestConnectionClick: () -> Unit,
    onNextClick: () -> Unit,
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

            TTButton(
                text = "Start Training",
                isEnabled = isConnected.value
            ) {
                onNextClick()
            }

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

