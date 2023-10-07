package com.etime.training_presentation

import com.etime.core_ui.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.etime.core_ui.LocalSpacing
import com.etime.core_ui.components.TTButton
import com.etime.core_ui.components.TTCircleIcon
import com.polar.sdk.api.model.PolarDeviceInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TrainingScreen(
    trainingViewModel: TrainingViewModel = hiltViewModel(),
    onNextClick: () -> Unit,
    onRequestConnectionClick: () -> Unit
){

    val isConnected = trainingViewModel.isConnected.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.spaceLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TTCircleIcon(iconId = R.drawable.ic_footbal)

            if(isConnected.value.not()) {
                TTButton(
                    text = "Connect Device"
                ){
                    onRequestConnectionClick()
                }
            }

            TTButton(
                text = "Start Training",
                isEnabled = isConnected.value
            ){
                onNextClick()
            }

            Text(
                text = "View last training",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable {

                }
            )

        }
    }
}

