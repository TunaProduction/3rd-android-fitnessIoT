package com.etime.training_presentation.trainingHistorial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.etime.core_ui.LocalSpacing
import com.etime.core_ui.R
import com.etime.core_ui.components.TTButton
import com.etime.core_ui.components.TTProgressBar
import com.etime.core_ui.components.TTTrainingCell
import com.etime.core_ui.components.TTTrainingRow
import com.etime.training_presentation.data.FinishedTrainingData

@Composable
fun TrainingHistorialScreen(
    trainingHistorialViewModel: TrainingHistorialViewModel = hiltViewModel(),
    backNavigation: () -> Unit
) {

    val context = LocalContext.current

    var trainingDialogData by remember { mutableStateOf<FinishedTrainingData?>(null) }
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        trainingHistorialViewModel.getTraining(context)
    }

    val trainings = trainingHistorialViewModel.trainings.collectAsState()
    val loading = trainingHistorialViewModel.loading.collectAsState()

    LazyColumn() {

        item {
            Spacer(modifier = Modifier.height(LocalSpacing.current.spaceMedium))
            TTButton(
                text = "Back",
                modifier = Modifier.fillMaxWidth()
            ){
                backNavigation()
            }
            Spacer(modifier = Modifier.height(LocalSpacing.current.spaceLarge))
        }

        trainings.value?.let {
            items(items = it.trainings) { training ->
                PastTraining(training) { info ->
                    trainingDialogData = info
                    show = true
                }

            }
        }
    }

    if(loading.value) {
        TTProgressBar()
    }

    if(show) {
        trainingDialogData?.let {
            TrainingDialog(it) {
                show = false
            }
        }
    }

}

@Composable
private fun PastTraining(
    training: FinishedTrainingData,
    onClick: (FinishedTrainingData) -> Unit = { }
) {
    Column(
        modifier = Modifier.clickable { onClick(training) },
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.spaceSmall)
    ) {
        TTTrainingRow(
            name = "Date:",
            value = training.fileName,
            textStyle = MaterialTheme.typography.titleLarge,
            valueTextStyle = MaterialTheme.typography.labelLarge
        )

        Divider(thickness = LocalSpacing.current.two)
    }
}

@Composable
fun TrainingDialog(
    training: FinishedTrainingData,
    onClose: () -> Unit = { }) {
    Box(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.4f))
            .fillMaxSize()
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color.White)
                .padding(LocalSpacing.current.spaceMedium)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
                ){
                Icon(
                    modifier = Modifier
                        .size(LocalSpacing.current.spaceMedium)
                        .clickable { onClose() },
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "close",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            TTTrainingRow(
                name = stringResource(id = R.string.training_heart_rate_label),
                value = training.avgHr,
                textStyle = MaterialTheme.typography.titleMedium,
                valueTextStyle = MaterialTheme.typography.labelLarge
            )

            TTTrainingRow(
                name = stringResource(id = R.string.training_acceleration_label),
                value = training.avgAcceleration,
                textStyle = MaterialTheme.typography.titleMedium,
                valueTextStyle = MaterialTheme.typography.labelLarge
            )

            TTTrainingRow(
                name = stringResource(id = R.string.training_falls_label),
                value = training.falls.toString(),
                textStyle = MaterialTheme.typography.titleMedium,
                valueTextStyle = MaterialTheme.typography.labelLarge
            )
        }
    }
}