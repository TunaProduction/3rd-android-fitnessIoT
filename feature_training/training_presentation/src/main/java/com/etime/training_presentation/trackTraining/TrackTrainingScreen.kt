package com.etime.training_presentation.trackTraining

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.etime.core_ui.LocalSpacing
import com.etime.core_ui.R
import com.etime.core_ui.components.TTButton
import com.etime.core_ui.components.TTProgressBar
import com.etime.core_ui.components.TTTrainingCell
import com.etime.training_presentation.TrainingViewModel
import com.etime.training_presentation.data.Profile
import com.etime.training_presentation.local.TrainingDao
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.ExperimentalTime


val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

@OptIn(ExperimentalTime::class)
@Composable
fun TrackTrainingScreen(
    trainingViewModel: TrainingViewModel,
    trigger: Boolean,
    backNavigation: () -> Unit,
    finishedNavigation: () -> Unit = { },
) {

    val context = LocalContext.current
    val view = LocalView.current

    val count by rememberUpdatedState(trigger)
    val deviceId = trainingViewModel.connectedDeviceId.collectAsState()

    Log.d("TrackTrainingScreen", "Recomposition triggered - Device ID: ${deviceId.value}")

    if(deviceId.value.isEmpty()) {
        backNavigation()
        return
    }

    val trainingStatus = trainingViewModel.trainingStatus.collectAsState()
    val hrChartData = trainingViewModel.hrChartEntry.collectAsState()
    val finishedTraining = trainingViewModel.completeTraining.collectAsState()
    val loading = trainingViewModel.loading.collectAsState()
    chartEntryModelProducer.setEntries(hrChartData.value)

    DisposableEffect(key1 = deviceId.value, effect = {
        trainingViewModel.trackStreamTraining(deviceId.value)
        view.keepScreenOn = true

        onDispose {
            view.keepScreenOn = false
        }
    })

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TrackTrainingContent(trainingViewModel)

        Chart(
            chart = lineChart(),
            chartModelProducer = chartEntryModelProducer,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
        )

        TrackTrainingControl(
            trainingStatus = trainingStatus.value,
            onPause = {
                if (trainingStatus.value == TrainingStatus.OnGoing){
                    trainingViewModel.pauseStopWatch()
                }else{
                  //  trainingViewModel.finishTraining()
                }
              },
            onRestart = { trainingViewModel.restartStopWatch() },
            onFinished = { trainingViewModel.finishTraining(context) }
        )
    }

    if(loading.value) {
        TTProgressBar()
    }

    if (finishedTraining.value) {
        LaunchedEffect(key1 = true) {
            trainingViewModel.disposeAllStreams()
            backNavigation()
        }
    }
}

@Composable
fun TrackTrainingControl(
    trainingStatus: TrainingStatus,
    onPause: () -> Unit = {},
    onRestart: () -> Unit = {},
    onFinished: () -> Unit = {}
) {
    var startContinueString by remember { mutableIntStateOf(R.string.training_start_button) }
    var pauseStopString by remember { mutableIntStateOf(R.string.training_pause_button) }
    var startContinueVisibility by remember { mutableStateOf(true) }
    var pauseStopAction by remember { mutableStateOf(true) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {

        when (trainingStatus) {
            TrainingStatus.OnGoing -> {
                startContinueVisibility = false
                pauseStopAction = true
                pauseStopString = R.string.training_pause_button
            }
            TrainingStatus.Paused -> {
                startContinueVisibility = true
                pauseStopAction = false
                pauseStopString = R.string.training_stop_button
            }
            TrainingStatus.Finished -> {}
        }

        if(startContinueVisibility) {
            TTButton(text = stringResource(id = startContinueString)) {
                onRestart.invoke()
            }
        }

        TTButton(
            text = stringResource(id = pauseStopString),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            if( pauseStopAction ) {
                onPause.invoke()
            }else{
                onFinished.invoke()
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun TrackTrainingContent(trainingViewModel: TrainingViewModel) {

    //val deviceId = trainingViewModel.connectedDeviceId.collectAsState()
    val hrData = trainingViewModel.hrData.collectAsState()
    val acceleration = trainingViewModel.acceleration.collectAsState()
    val falls = trainingViewModel.falls.collectAsState()
    val distance = trainingViewModel.distance.collectAsState()
    val steps = trainingViewModel.steps.collectAsState()

    val timer = trainingViewModel.timer.collectAsState()
    val movementTimer = trainingViewModel.movementTimer.collectAsState()
    //val ecgData = trainingViewModel.ecgEntry.collectAsState()*/
val currentProfile = trainingViewModel.getProfile()
    val restingHeartRateRecord = 100; //TODO no se como calcularias esto...
    val maxHeartRate = (220 - (currentProfile?.age?.toInt() ?: 0));
    val reserveHeartRate = maxHeartRate - restingHeartRateRecord;

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            horizontal = LocalSpacing.current.spaceMedium
        ),
        content = {


            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_heart_rate_label),
                        value = it.hr.toString(),
                    )
                }
            }

            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_max_heart_rate_label),
                        value = maxHeartRate.toString(),
                    )
                }
            }

            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_reserve_heart_rate_label),
                        value = reserveHeartRate.toString(),
                    )
                }
            }

            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_training_zones_label),
                        value = "",
                    )
                }
            }
            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_training_zones_1_label),
                        value = (reserveHeartRate*.5).toString() + "-" + (reserveHeartRate*.6).toString(),
                    )
                }
            }
            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_training_zones_2_label),
                        value = (reserveHeartRate*.6).toString() + "-" + (reserveHeartRate*.7).toString(),
                    )
                }
            }
            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_training_zones_3_label),
                        value = (reserveHeartRate*.7).toString() + "-" + (reserveHeartRate*.8).toString(),
                    )
                }
            }
            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_training_zones_4_label),
                        value = (reserveHeartRate*.8).toString() + "-" + (reserveHeartRate*.9).toString(),
                    )
                }
            }
            hrData.value?.let{
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_training_zones_5_label),
                        value = (reserveHeartRate*.9).toString() + "-" + (reserveHeartRate).toString(),
                    )
                }
            }

            acceleration.value?.let {
                item {
                    TTTrainingCell(
                        name = stringResource(id = R.string.training_acceleration_label),
                        value = it.toString(),
                    )
                }
            }

            item {
                TTTrainingCell(
                    name = stringResource(id = R.string.training_falls_label),
                    value = falls.value.toString(),
                )
            }

            item {
                TTTrainingCell(
                    name = stringResource(id = R.string.training_timer_label),
                    value = timer.value,
                )
            }

            item {
                TTTrainingCell(
                    name = stringResource(id = R.string.training_motion_timer_label),
                    value = movementTimer.value,
                )
            }
        }
    )
}


/*
@OptIn(ExperimentalAnimationApi::class, ExperimentalTime::class)
@Composable
fun TrackTrainingScreen(
    trainingViewModel: TrainingViewModel,
    backNavigation: () -> Unit,
) {

    val context = LocalContext.current

    val deviceId = trainingViewModel.connectedDeviceId.collectAsState()
    val hrData = trainingViewModel.hrData.collectAsState()
    val accData = trainingViewModel.accData.collectAsState()
    val acceleration = trainingViewModel.acceleration.collectAsState()
    val distance = trainingViewModel.distance.collectAsState()
    val steps = trainingViewModel.steps.collectAsState()
    val falls = trainingViewModel.falls.collectAsState()
    val timer = trainingViewModel.pTimer.collectAsState()
    val movementTimer = trainingViewModel.movementTimer.collectAsState()
    val ecgData = trainingViewModel.ecgEntry.collectAsState()

    if(deviceId.value.isEmpty()) {
        backNavigation()
        return
    }

    LaunchedEffect(true) {
        trainingViewModel.enableSdkMode(deviceId.value)
        chartEntryModelProducer.setEntries(ecgData.value)
        //trainingViewModel.trackStreamTraining(deviceId.value)
        //trainingViewModel.startStopwatch()
    }

    //trainingViewModel.getTraining(deviceId.value)

    LazyColumn{
        item {
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

            Row {
                Text(
                    text = "Walked distance: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = distance.value.toString() + "m")
            }

            Row {
                Text(
                    text = "Steps made: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = steps.value.toString())
            }

            Row {
                Text(
                    text = "Falls: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = falls.value.toString())
            }

            Row {
                Text(
                    text = "Timer: ",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = timer.value,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Blue
                    )
                )
            }

            Row {
                Text(
                    text = "MovementTimer: ",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = movementTimer.value,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Blue
                    )
                )
            }


            Chart(
                chart = lineChart(),
                chartModelProducer = chartEntryModelProducer,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
            )


        }

    }

}

@ExperimentalAnimationApi
fun addAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    ) with slideOutVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    )
}*/