package com.etime.training_presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.errors.PolarNotificationNotEnabled
import com.polar.sdk.api.model.PolarAccelerometerData
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarExerciseEntry
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarSensorSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel(){
    // ATTENTION! Replace with the device ID from your device.
    //private var deviceId = "C4E55B27"

    private var sdkModeEnabledStatus = false
    private var bluetoothEnabled = false

    private var accDisposable: Disposable? = null

    private var exerciseEntries: MutableList<PolarExerciseEntry> = mutableListOf()

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

    companion object {
        private const val TAG = "MainActivity"
        private const val API_LOGGER_TAG = "API LOGGER"
        private const val PERMISSION_REQUEST_CODE = 1
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

    fun startPolar(){
        Throwable().addSuppressed(PolarNotificationNotEnabled())
        api.setApiCallback(object : PolarBleApiCallback() {
            override fun blePowerStateChanged(powered: Boolean) {
                Log.d(TAG, "BLE power: $powered")
                bluetoothEnabled = powered
                if (powered) {
                    //enableAllButtons()
                    Log.d("May-Polar","Phone Bluetooth on")
                } else {
                    //disableAllButtons()
                    Log.d("May-Polar","Phone Bluetooth off")
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

    fun getTraining(deviceId: String) {

        val isDisposed = accDisposable?.isDisposed ?: true
        if (isDisposed) {
            accDisposable = api.startAccStreaming(
                deviceId,
                PolarSensorSetting(getDefaultSettings())
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { polarAccelerometerData: PolarAccelerometerData ->
                        for (data in polarAccelerometerData.samples) {
                            _accData.value = data
                            /*Log.d(
                                TAG,
                                "ACC    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}"
                            )*/
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
        } else {
            accDisposable?.dispose()
        }

        viewModelScope.launch {

            /*api.startAccStreaming(
                deviceId,
                PolarSensorSetting(getDefaultSettings())
            )
                .observeOn(AndroidSchedulers.mainThread())
                .asFlow()
                .collect()*/

            api.startHrStreaming(deviceId)
                .asFlow()
                .collect{ hrCollectedData ->
                    for (sample in hrCollectedData.samples) {
                        _hrData.value = sample
                        //Log.d(TAG, "HR     bpm: ${sample.hr} rrs: ${sample.rrsMs} rrAvailable: ${sample.rrAvailable} contactStatus: ${sample.contactStatus} contactStatusSupported: ${sample.contactStatusSupported}")
                    }
                }

            /*.collect { polarAccelerometerData ->
                for (data in polarAccelerometerData.samples) {
                    _accData.value = data
                }
            }*/
                /*.subscribe(
                    { polarAccelerometerData: PolarAccelerometerData ->
                        for (data in polarAccelerometerData.samples) {
                            _accData.value = data
                            /*Log.d(
                                TAG,
                                "ACC    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}"
                            )*/
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
                )*/



         /*   getSettings(deviceId, PolarBleApi.PolarDeviceDataType.ACC)
                .collect {
                    api.startAccStreaming(deviceId, it)
                        .asFlow()
                        .collect { polarAccelerometerData ->
                            for (data in polarAccelerometerData.samples) {
                                _accData.value = data
                                Log.d(TAG, "ACC    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}")
                            }
                        }
                }*/

            /*.asFlow()
            .collect { polarAccelerometerData ->
                for (data in polarAccelerometerData.samples) {
                    _accData.value = data
                    Log.d(TAG, "ACC    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}")
                }
            }*/



           /* requestStreamSettings(deviceId, PolarSensorSetting(
                settings = Pair<PolarSensorSetting.SettingType()>
            ))*/

            //api.startAccStreaming(deviceId, PolarBleApi.PolarDeviceDataType.ACC)
           /* api.startHrStreaming(deviceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { hrData: PolarHrData ->
                        for (sample in hrData.samples) {
                            Log.d(TAG, "HR     bpm: ${sample.hr} rrs: ${sample.rrsMs} rrAvailable: ${sample.rrAvailable} contactStatus: ${sample.contactStatus} contactStatusSupported: ${sample.contactStatusSupported}")
                        }
                    },
                    { error: Throwable ->
                        toggleButtonUp(hrButton, R.string.start_hr_stream)
                        Log.e(TAG, "HR stream failed. Reason $error")
                    },
                    { Log.d(TAG, "HR stream complete") }
                )*/
        }
    }

    fun getDefaultSettings(): MutableMap<PolarSensorSetting.SettingType, Int> {
        val defaultSettings: MutableMap<PolarSensorSetting.SettingType, Int> = mutableMapOf()
        defaultSettings[PolarSensorSetting.SettingType.SAMPLE_RATE] = 52
        defaultSettings[PolarSensorSetting.SettingType.RESOLUTION] = 16
        defaultSettings[PolarSensorSetting.SettingType.RANGE] = 8
        defaultSettings[PolarSensorSetting.SettingType.CHANNELS] = 3

        return defaultSettings
    }

    fun getSettings(deviceId: String, feature: PolarBleApi.PolarDeviceDataType): Flow<PolarSensorSetting> {
        /*return settings*/
        var settings = PolarSensorSetting(getDefaultSettings())

        return api.requestFullStreamSettings(deviceId, feature)
            .toFlowable()
            .asFlow()

        /*return api.requestStreamSettings(deviceId, feature)
            .toFlowable()
            .asFlow()*/

    }

    /*
    fun getSettings(deviceId: String, feature: PolarBleApi.PolarDeviceDataType): Single<PolarSensorSetting> {
        /*return settings*/
        var settings = PolarSensorSetting(getDefaultSettings())

        return api.requestFullStreamSettings(deviceId, feature)

       /* val availableSettings = api.requestStreamSettings(deviceId, feature)
        val allSettings = api.requestFullStreamSettings(deviceId, feature)
            .onErrorReturn { error: Throwable ->
                Log.w(TAG, "Full stream settings are not available for feature $feature. REASON: $error")
                PolarSensorSetting(emptyMap())
            }

        return Single.zip(availableSettings, allSettings) { available: PolarSensorSetting, all: PolarSensorSetting ->
            if (available.settings.isEmpty()) {
                throw Throwable("Settings are not available")
            } else {
                Log.d(TAG, "Feature " + feature + " available settings " + available.settings)
                Log.d(TAG, "Feature " + feature + " all settings " + all.settings)
                return@zip android.util.Pair(available, all)
            }
        }
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable()
            .flatMap {
                Single.create { e: SingleEmitter<PolarSensorSetting> ->
                    PolarSensorSetting(getDefaultSettings())
                }.subscribeOn(AndroidSchedulers.mainThread()).toFlowable()
            }*/
    }
     */

    private fun disposeAllStreams() {
        accDisposable?.dispose()
    }

}