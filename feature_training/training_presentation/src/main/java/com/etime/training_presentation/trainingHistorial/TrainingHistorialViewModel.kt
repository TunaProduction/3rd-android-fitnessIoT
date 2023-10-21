package com.etime.training_presentation.trainingHistorial

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import com.etime.training_presentation.data.TrainingHistorial
import com.etime.training_presentation.remote.ThirdTimeApi
import com.etime.training_presentation.util.getDeviceId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TrainingHistorialViewModel @Inject constructor(
    private val network: ThirdTimeApi
) : ViewModel() {

    private val _completeTraining = MutableStateFlow<Boolean>(false)
    val completeTraining: StateFlow<Boolean> get() = _completeTraining

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _trainings = MutableStateFlow<TrainingHistorial?>(null)
    val trainings = _trainings.asStateFlow()

    suspend fun getTraining(context: Context): Result<TrainingHistorial> {
        val folder = getDeviceId(context)+"${Build.BRAND}-${Build.MODEL}"
        _loading.value = true
        return try {
            val getTrainings = network.getTrainings("1b1d51a7c13eaa01Redmi-M2006C3LG")
            Result.success(getTrainings).also {
                _loading.value = false
                _trainings.value = it.getOrNull()
            }

        } catch(e: Exception) {
            e.printStackTrace()
            Result.failure<TrainingHistorial>(e).also {
                _loading.value = false
                _trainings.value = null
            }
        }
    }
}