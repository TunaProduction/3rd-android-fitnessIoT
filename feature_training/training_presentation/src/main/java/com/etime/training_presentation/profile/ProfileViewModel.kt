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
import kotlinx.coroutines.flow.firstOrNull
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

    fun createOrEditUser2(profile: Profile){
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

    fun createOrEditUser(profile: Profile) {
        _loading.value = true
        viewModelScope.launch {
            val existingProfiles = trainingDao.verifyExistence().firstOrNull()
            if (existingProfiles.isNullOrEmpty()) {
                trainingDao.insert(profile)
            } else {
                // Here you'd retrieve the existing profile, update only the fields that changed,
                // and then call update
                val currentProfile = existingProfiles.first()
                val updatedProfile = currentProfile.copy(
                    // Assuming you have a way to determine if a field has been edited
                    userType = profile.userType.takeIf { it.isNotEmpty() } ?: currentProfile.userType,
                    name = profile.name.takeIf { it.isNotEmpty() } ?: currentProfile.name,
                    weight = profile.weight.takeIf { it.isNotEmpty() } ?: currentProfile.weight,
                    height = profile.height.takeIf { it.isNotEmpty() } ?: currentProfile.height,
                    age = profile.age.takeIf { it.isNotEmpty() } ?: currentProfile.age,
                    // add similar logic for weight, height, and age
                )
                trainingDao.update(updatedProfile)
            }
            _loading.value = false
            _finished.value = true
        }
    }

    fun createUser(profile: Profile) {
        _loading.value = true
        viewModelScope.launch {
            val existingProfiles = trainingDao.verifyExistence().firstOrNull()
            if (existingProfiles.isNullOrEmpty()) {
                trainingDao.insert(profile)
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