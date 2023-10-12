package com.etime.training_presentation.remote

import com.etime.training_presentation.FinishedTrainingData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ThirdTimeApi {

    @POST("posttraining")
    suspend fun searchFood(
        @Body query: FinishedTrainingData,
    ): String

    companion object {
        const val BASE_URL = "https://jm7pkxpsze.execute-api.us-east-1.amazonaws.com/dev/api/"
    }
}