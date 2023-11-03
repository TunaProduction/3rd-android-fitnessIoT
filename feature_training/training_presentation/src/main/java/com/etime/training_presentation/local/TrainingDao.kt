package com.etime.training_presentation.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.etime.training_presentation.data.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface TrainingDao {
    @Insert
    suspend fun insert(note: Profile): Long

    @Update
    suspend fun update(note: Profile)

    @Delete
    suspend fun delete(note: Profile)

    @Query("SELECT deviceId FROM user_table WHERE userId = 1")
    fun selectDeviceId(): Flow<String>

    @Query("UPDATE user_table SET userType = :userType, name = :name WHERE userId = :userId")
    fun updateSpecificFields(userType: String, name: String, userId: String): Int

    @Query("UPDATE user_table SET deviceId = :deviceId WHERE userId = 1")
    fun updateDeviceId(deviceId: String): Int

    @Query("SELECT * FROM user_table WHERE userId = 1")
    fun getProfile(): Flow<Profile>

    @Query("SELECT * FROM user_table")
    fun verifyExistence(): Flow<List<Profile>>
}