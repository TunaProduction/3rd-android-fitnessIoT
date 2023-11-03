package com.etime.training_presentation

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etime.training_presentation.data.FinishedTrainingData
import com.etime.training_presentation.data.TimeWithHeartRate
import com.etime.training_presentation.local.TrainingDao
import com.etime.training_presentation.remote.ThirdTimeApi
import com.etime.training_presentation.trackTraining.TrainingStatus
import com.etime.training_presentation.util.detectFall
import com.etime.training_presentation.util.getDeltaLinearAcceleration
import com.etime.training_presentation.util.getDeviceId
import com.etime.training_presentation.util.getTimeStamp
import com.etime.training_presentation.util.getWalkedDistance
import com.etime.training_presentation.util.isHeavyMovement
import com.google.gson.Gson
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryOf
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.errors.PolarNotificationNotEnabled
import com.polar.sdk.api.model.PolarAccelerometerData
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarEcgData
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarSensorSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.future.future
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
@ExperimentalTime
class TrainingViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val dao: TrainingDao,
    private val network: ThirdTimeApi
) : ViewModel(){

    companion object {
        private const val TAG = "TrainingViewModel"
        private const val API_LOGGER_TAG = "API LOGGER"
        private const val PERMISSION_REQUEST_CODE = 1
    }

    private var accDisposable: Disposable? = null
    private var hrDisposable: Disposable? = null
    private var ppgDisposable: Disposable? = null
    private var ecgDisposable: Disposable? = null
    private var sdkModeDisposable: Disposable? = null

    private var sdkModeEnabledStatus = false
    private var bluetoothEnabled = false

    private val _polarDevicesList = MutableStateFlow<List<PolarDeviceInfo>>(listOf())
    val polarDevicesList = _polarDevicesList.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _connectedDeviceId = MutableStateFlow("")
    val connectedDeviceId = _connectedDeviceId.asStateFlow()

    private val _hrData = MutableStateFlow<PolarHrData.PolarHrSample?>(null)
    val hrData = _hrData.asStateFlow()

    private val _accData = MutableStateFlow<PolarAccelerometerData.PolarAccelerometerDataSample?>(null)
    val accData = _accData.asStateFlow()

    private val _acceleration = MutableStateFlow<Double>(0.000000)
    val acceleration = _acceleration.asStateFlow()

    private val _distance = MutableStateFlow<Double>(0.000000)
    val distance = _distance.asStateFlow()

    private val _steps = MutableStateFlow<Int>(0)
    val steps = _steps.asStateFlow()

    private val _falls = MutableStateFlow<Int>(0)
    val falls = _falls.asStateFlow()

    private val _onGoing = MutableStateFlow<Boolean>(false)
    val onGoing = _onGoing.asStateFlow()

    private val _timer = MutableStateFlow<String>("0")
    val timer: StateFlow<String> get() = _timer

    private val _hrActivated = MutableStateFlow<Boolean>(false)
    val hrActivated = _hrActivated.asStateFlow()

    private val _trainingStatus = MutableStateFlow<TrainingStatus>(TrainingStatus.OnGoing)
    val trainingStatus = _trainingStatus.asStateFlow()

    private val _movementTimer = MutableStateFlow<String>("0")
    val movementTimer: StateFlow<String> get() = _movementTimer

    private val _isMoving = MutableStateFlow<Boolean>(false)
    val isMoving: StateFlow<Boolean> get() = _isMoving

    private val _hrChartData = MutableStateFlow<List<PolarEcgData.PolarEcgDataSample>>(listOf())
    val hrChartData = _hrChartData.asStateFlow()

    private val _hrChartEntry = MutableStateFlow<List<FloatEntry>>(listOf())
    val hrChartEntry = _hrChartEntry.asStateFlow()

    private val _heartTrack = MutableStateFlow<MutableList<PolarHrData.PolarHrSample>>(mutableListOf())
    var heartTrackData = listOf<PolarHrData.PolarHrSample>()

    private val _accelerationTrack = MutableStateFlow<MutableList<Double>>(mutableListOf())
    var accelerationTrackData = listOf<Double>()

    private val _completeTraining = MutableStateFlow<Boolean>(false)
    val completeTraining: StateFlow<Boolean> get() = _completeTraining

    val timeWithHeartRate: MutableList<TimeWithHeartRate> = mutableListOf()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    var totalSeconds = 0

    private var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }

    private val api: PolarBleApi by lazy {
        // Notice all features are enabled
        PolarBleApiDefaultImpl.defaultImplementation(
            context,
            setOf(
                PolarBleApi.PolarBleSdkFeature.FEATURE_HR,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_SDK_MODE,
                PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_H10_EXERCISE_RECORDING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_OFFLINE_RECORDING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_DEVICE_TIME_SETUP,
                PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO
            )
        )
    }

    init {
        startPolar()
    }

    override fun onCleared() {
        super.onCleared()
        api.disconnectFromDevice(_connectedDeviceId.value)
        api.shutDown()
        disposeAllStreams()
    }

    fun changeTrainingStatus(status: TrainingStatus) {
        _trainingStatus.value = status
    }

    fun startStopwatch()
    {
        viewModelScope.launch(job) {
            while (isActive) {
                delay(1000) // delay for 1 second
                if(_onGoing.value){
                    totalSeconds++
                    val hours = totalSeconds / 3600
                    val minutes = (totalSeconds % 3600) / 60
                    val seconds = totalSeconds % 60
                    _timer.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                }
            }
        }
    }

    fun addHrHistorial()
    {
        viewModelScope.launch(job) {
            while (isActive) {
                delay(3000) // delay for 1 second
                if(_onGoing.value){
                    _hrData.value?.let {
                        timeWithHeartRate.add(
                            TimeWithHeartRate(
                            time = formatSeconds(totalSeconds), // Your logic for getting the timestamp here
                            hr = it.hr.toString()
                        )
                        )
                    }
                }
            }
        }
    }

    fun startMovementTimer() {
        var totalSeconds = 0
        viewModelScope.launch(job) {
            while (isActive) {
                delay(1000) // delay for 1 second
                if(_isMoving.value && _onGoing.value){
                    totalSeconds++
                    val hours = totalSeconds / 3600
                    val minutes = (totalSeconds % 3600) / 60
                    val seconds = totalSeconds % 60
                    _movementTimer .value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                }
            }
        }
    }

    fun pauseStopWatch() {
        _onGoing.value = false
        changeTrainingStatus(TrainingStatus.Paused)
    }

    fun restartStopWatch() {
        _onGoing.value = true
        changeTrainingStatus(TrainingStatus.OnGoing)
    }

    fun finishTraining(context: Context) {
        _onGoing.value = false
        heartTrackData = _heartTrack.value
        accelerationTrackData = _accelerationTrack.value
        changeTrainingStatus(TrainingStatus.Finished)
        createTrainingFile(context)
    }

    fun formatSeconds(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    fun trackStreamTraining(deviceId: String) {
        val ecgCreatedData = mutableListOf<FloatEntry>()
        val handler = Handler(Looper.getMainLooper())
        job.start()
        _onGoing.value = true
        hrDisposable = api.startHrStreaming(deviceId)
            .observeOn(Schedulers.io())
            .subscribe(
                { hrData: PolarHrData ->
                    if(_onGoing.value){
                        for (sample in hrData.samples) {
                            _hrData.value = sample
                            _heartTrack.value.add(sample)
                            ecgCreatedData.add(entryOf(totalSeconds.toFloat(), sample.hr))
                            _hrChartEntry.tryEmit(ecgCreatedData)
                            Log.d(TAG, "HR     bpm: ${sample.hr} rrs: ${sample.rrsMs} rrAvailable: ${sample.rrAvailable} contactStatus: ${sample.contactStatus} contactStatusSupported: ${sample.contactStatusSupported}")

                        }
                    }
                },
                { error: Throwable ->
                    Log.e(TAG, "HR stream failed. Reason $error")
                },
                { Log.d(TAG, "HR stream complete") }
            )

        accDisposable = api.startAccStreaming(
            deviceId,
            PolarSensorSetting(getDefaultSettings())
        )
            .observeOn(Schedulers.io())
            .subscribe(
                { polarAccelerometerData: PolarAccelerometerData ->
                    if(_onGoing.value) {
                        Log.d(TAG, "ACC RUNNING")
                        _acceleration.value = getDeltaLinearAcceleration(
                            polarAccelerometerData.samples,
                            0.000000
                        ).first

                        _accelerationTrack.value.add(_acceleration.value)

                        val walk = getWalkedDistance(polarAccelerometerData.samples)
                        _distance.value = walk.first
                        _steps.value = walk.second

                        _isMoving.value = isHeavyMovement(polarAccelerometerData)

                        if (detectFall(polarAccelerometerData))
                            _falls.value++
                    }
                },
                { error: Throwable ->
                    //toggleButtonUp(accButton, R.string.start_acc_stream)
                    Log.e(TAG, "ACC stream failed. Reason $error")
                },
                {
                    //showToast("ACC stream complete")
                    Log.d(TAG, "ACC stream complete")
                }
            )

        startStopwatch()
        startMovementTimer()
        addHrHistorial()

    }

    fun getDefaultSettings(): MutableMap<PolarSensorSetting.SettingType, Int> {
        val defaultSettings: MutableMap<PolarSensorSetting.SettingType, Int> = mutableMapOf()
        defaultSettings[PolarSensorSetting.SettingType.SAMPLE_RATE] = 52
        defaultSettings[PolarSensorSetting.SettingType.RESOLUTION] = 16
        defaultSettings[PolarSensorSetting.SettingType.RANGE] = 8
        defaultSettings[PolarSensorSetting.SettingType.CHANNELS] = 3

        return defaultSettings
    }

    fun searchDevice() {
        val foundDevices = mutableListOf<PolarDeviceInfo>()

        viewModelScope.launch(job) {
            api.searchForDevice()
                .asFlow()
                .onEach { polarDeviceInfo ->
                    foundDevices.add(polarDeviceInfo)
                    Log.d("may-connection", polarDeviceInfo.deviceId+" "+polarDeviceInfo.name+" "+polarDeviceInfo.isConnectable)
                    _polarDevicesList.tryEmit(foundDevices)
                }
                .collect{}
        }
    }

    fun connectDevice(founDevice: PolarDeviceInfo){
        try {
            if (_isConnected.value) {
                api.disconnectFromDevice(founDevice.deviceId)
            } else {
                api.connectToDevice(founDevice.deviceId)
            }
        } catch (polarInvalidArgument: PolarInvalidArgument) {
            val attempt = if (_isConnected.value) {
                "disconnect"
            } else {
                "connect"
            }
            Log.e(TAG, "Failed to $attempt. Reason $polarInvalidArgument ")
        }
    }

    fun connectDeviceByString(deviceId: String) {
        api.connectToDevice(deviceId)
    }

    fun startPolar(){
        Throwable().addSuppressed(PolarNotificationNotEnabled())
        api.setApiCallback(object : PolarBleApiCallback() {
            override fun blePowerStateChanged(powered: Boolean) {
                Log.d(TAG, "BLE power: $powered")
                bluetoothEnabled = powered
                if (powered) {
                    //enableAllButtons()
                    Log.d(TAG,"Phone Bluetooth on")
                } else {
                    //disableAllButtons()
                    Log.d(TAG,"Phone Bluetooth off")
                }
            }

            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
                viewModelScope.launch(Dispatchers.IO) {
                    dao.updateDeviceId(polarDeviceInfo.deviceId).also {
                        _connectedDeviceId.value = polarDeviceInfo.deviceId
                        _isConnected.value = true
                    }
                }

                //val buttonText = getString(R.string.disconnect_from_device, deviceId)
                //toggleButtonDown(connectButton, buttonText)
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
                _isConnected.value = false
                _connectedDeviceId.value = ""
                //val buttonText = getString(R.string.connect_to_device, deviceId)
                //toggleButtonUp(connectButton, buttonText)
                //toggleButtonUp(toggleSdkModeButton, R.string.enable_sdk_mode)
            }

            override fun disInformationReceived(identifier: String, uuid: UUID, value: String) {
                Log.d(TAG, "DIS INFO uuid: $uuid value: $value")
            }

            override fun batteryLevelReceived(identifier: String, level: Int) {
                Log.d(TAG, "BATTERY LEVEL: $level")
            }

            override fun hrNotificationReceived(identifier: String, data: PolarHrData.PolarHrSample) {
                // deprecated
            }
        })
    }

    private fun createTrainingFile(context: Context) {
        val avgHr = heartTrackData.map { it.hr }.average()
        val avgAcceleration = accelerationTrackData.average()

        _loading.value = true
        viewModelScope.launch(job) {
            dao.getProfile().collectLatest {
                val training = FinishedTrainingData(
                    getDeviceId(context)+"${Build.BRAND}-${Build.MODEL}",
                    getTimeStamp(),
                    avgHr.toString(),
                    avgAcceleration.toString(),
                    _falls.value,
                    _steps.value,
                    _movementTimer.value,
                    _timer.value,
                    timeWithHeartRate,
                    it
                )

                sendTraining(training)
            }
        }
        //saveTrainingToFile(context, training, "training.json")
    }

    fun saveTrainingToFile(context: Context, training: FinishedTrainingData, fileName: String) {
        val gson = Gson()
        val jsonString = gson.toJson(training)

        try {
            val file = File(context.filesDir, fileName)
            val printWriter = PrintWriter(FileWriter(file))
            printWriter.print(jsonString)
            printWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun disposeAllStreams() {
        accDisposable?.dispose()
        hrDisposable?.dispose()
        sdkModeDisposable?.dispose()

        job.cancel()

        _trainingStatus.value = TrainingStatus.OnGoing
        _polarDevicesList.value = listOf()
        _hrData.value = null
        _accData.value = null
        _acceleration.value = 0.000000
        _distance.value = 0.000000
        _steps.value = 0
        _falls.value = 0
        _onGoing.value = false
        _timer.value = "0"
        _hrActivated.value = false
        _movementTimer.value = "0"
        _isMoving.value = false
        _hrChartData.value = listOf()
        _hrChartEntry.value = listOf()
        _heartTrack.value = mutableListOf()
        _accelerationTrack.value = mutableListOf()
        _completeTraining.value = false
        _loading.value = false
        totalSeconds = 0
    }


    suspend fun sendTraining(
        query: FinishedTrainingData
    ): Result<String> {
        return try {
            val sendTraining = network.sendTraining(query)
            Result.success(sendTraining).also {
                _loading.value = false
                _completeTraining.value = true
            }

        } catch(e: Exception) {
            e.printStackTrace()
            Result.failure<String>(e).also {
                _loading.value = false
                _completeTraining.value = false
            }
        }
    }

}