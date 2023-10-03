package com.etime.training_presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.etime.core.util.Constants.ACTION_SERVICE_CANCEL
import com.etime.core.util.Constants.ACTION_SERVICE_START
import com.etime.core.util.Constants.ACTION_SERVICE_STOP
import com.etime.core_ui.LocalSpacing
import com.etime.core_ui.R
import com.etime.core_ui.components.TTButton
import com.etime.core_ui.components.TTTrainingCell
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.sql.Date
import java.sql.Timestamp
import java.util.Random
import kotlin.time.ExperimentalTime


val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

@OptIn(ExperimentalAnimationApi::class, ExperimentalTime::class)
@Composable
fun TrackTrainingScreen(
    trainingViewModel: TrainingViewModel,
    backNavigation: () -> Unit,
) {
    val context = LocalContext.current

    val deviceId = trainingViewModel.connectedDeviceId.collectAsState()

    if(deviceId.value.isEmpty()) {
        backNavigation()
        return
    }

    LaunchedEffect(true) {
        trainingViewModel.trackStreamTraining(deviceId.value)
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TrackTrainingContent(trainingViewModel)
        TrackTrainingControl()
    }

}

@Composable
fun TrackTrainingControl() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        TTButton(text = stringResource(id = R.string.training_start_button)) {

        }

        TTButton(
            text = stringResource(id = R.string.training_pause_button),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {

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
    /*
    val timer = trainingViewModel.pTimer.collectAsState()
    val movementTimer = trainingViewModel.movementTimer.collectAsState()
    val ecgData = trainingViewModel.ecgEntry.collectAsState()*/

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
                    name = stringResource(id = R.string.training_walked_distance_label),
                    value = distance.value.toString(),
                )
            }

            item {
                TTTrainingCell(
                    name = stringResource(id = R.string.training_steps_label),
                    value = steps.value.toString(),
                )
            }

            /*item {
                TTTrainingCell(
                    name = stringResource(id = R.string.training_timer_label),
                    value = timer.value,
                )
            }

            item {
                TTTrainingCell(
                    name = stringResource(id = R.string.training_steps_label),
                    value = movementTimer.value,
                )
            }*/
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