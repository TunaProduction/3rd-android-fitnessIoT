package com.etime.training_presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etime.training_presentation.util.detectFall
import com.etime.training_presentation.util.getDeltaLinearAcceleration
import com.etime.training_presentation.util.getWalkedDistance
import com.etime.training_presentation.util.isHeavyMovement
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.errors.PolarNotificationNotEnabled
import com.polar.sdk.api.model.PolarAccelerometerData
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarPpgData
import com.polar.sdk.api.model.PolarSensorSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import java.util.UUID
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@HiltViewModel
@ExperimentalTime
class TrainingViewModel @Inject constructor(
    @ApplicationContext context: Context
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

    private val _isMoving = MutableStateFlow<Boolean>(false)

    private val _hrActivated = MutableStateFlow<Boolean>(false)
    val hrActivated = _hrActivated.asStateFlow()

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

    fun enableSdkMode(deviceId: String) {
        trackStreamTraining(deviceId)
    }

    fun trackStreamTraining(deviceId: String) {

        hrDisposable = api.startHrStreaming(deviceId)
            .observeOn(Schedulers.io())
            .subscribe(
                { hrData: PolarHrData ->
                    for (sample in hrData.samples) {
                        _hrData.value = sample
                        Log.d(TAG, "HR     bpm: ${sample.hr} rrs: ${sample.rrsMs} rrAvailable: ${sample.rrAvailable} contactStatus: ${sample.contactStatus} contactStatusSupported: ${sample.contactStatusSupported}")
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
                    for (data in polarAccelerometerData.samples) {
                        _accData.value = data
                        //getLinearAcceleration(data.x.toDouble(),data.y.toDouble(),data.z.toDouble())
                    }

                    _acceleration.value = getDeltaLinearAcceleration(polarAccelerometerData.samples, 0.000000).first
                    val walk = getWalkedDistance(polarAccelerometerData.samples)
                    _distance.value = walk.first
                    _steps.value = walk.second

                    _isMoving.value = isHeavyMovement(polarAccelerometerData)

                    if(detectFall(polarAccelerometerData))
                        _falls.value++
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

        viewModelScope.launch {
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
                _connectedDeviceId.value = polarDeviceInfo.deviceId
                _isConnected.value = true
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

    private fun disposeAllStreams() {
        accDisposable?.dispose()
        hrDisposable?.dispose()
        sdkModeDisposable?.dispose()
    }

}