package com.etime.training_presentation.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class Profile (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "userId") val userId: Int = 0,
    val userType: String,
    val name: String,
    val deviceId: String? = null
)
