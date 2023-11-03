package com.etime.training_presentation.remote

import com.etime.training_presentation.data.FinishedTrainingData
import com.etime.training_presentation.data.TrainingHistorial
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ThirdTimeApi {

    @POST("posttraining")
    suspend fun sendTraining(
        @Body query: FinishedTrainingData,
    ): String

    @GET("gettraining")
    suspend fun getTrainings(
        @Query("folderName") id: String,
    ): TrainingHistorial

    companion object {
        const val BASE_URL = "https://jm7pkxpsze.execute-api.us-east-1.amazonaws.com/dev/api/"
    }
}