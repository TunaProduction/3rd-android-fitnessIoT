package com.etime.training_presentation.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.etime.training_presentation.data.AppData
import com.etime.training_presentation.data.Profile

@Database(entities = [Profile::class, AppData::class], version = 3)
abstract class TrainingDatabase : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao
}
