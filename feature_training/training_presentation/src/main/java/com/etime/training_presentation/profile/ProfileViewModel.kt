package com.etime.training_presentation.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etime.training_presentation.data.Profile
import com.etime.training_presentation.local.TrainingDao
import com.etime.training_presentation.remote.ThirdTimeApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val trainingDao: TrainingDao
) : ViewModel() {

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading = _loading.asStateFlow()

    private val _finished = MutableStateFlow<Boolean>(false)
    val finished = _finished.asStateFlow()

    private val _deviceId = MutableStateFlow<String>("")
    val deviceId = _deviceId.asStateFlow()

    fun createOrEditUser(profile: Profile){
        _loading.value = true
        viewModelScope.launch {
            trainingDao.verifyExistence().collectLatest {
                if(it.isEmpty()) {
                    trainingDao.insert(profile).also {
                        _loading.value = false
                        _finished.value = true
                    }

                } else {
                    trainingDao.update(profile).also {
                        _loading.value = false
                        _finished.value = true
                    }
                }
            }
        }
    }

    fun getDeviceId() {
        viewModelScope.launch {
            trainingDao.selectDeviceId().collectLatest {
                _deviceId.value = it
            }
        }
    }
}