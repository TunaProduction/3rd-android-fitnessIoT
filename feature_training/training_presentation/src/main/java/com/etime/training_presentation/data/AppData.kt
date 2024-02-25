package com.etime.training_presentation.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_table")
data class AppData (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "appId") val appId: Int = 0,
    val runningTraining: Boolean
)