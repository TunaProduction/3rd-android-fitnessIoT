package com.etime.training_presentation.util

import com.polar.sdk.api.model.PolarAccelerometerData
import kotlin.math.sqrt

fun getDeltaLinearAcceleration(accelerometerData: List<PolarAccelerometerData.PolarAccelerometerDataSample>, timeIntervalSeconds: Double): Pair<Double,Double> {
    // Initialize variables to store the previous accelerometer reading
    var prevXAccelerationMg = 0
    var prevYAccelerationMg = 0
    var prevZAccelerationMg = 0

    // Initialize variable to store the total linear acceleration change
    var deltaLinearAcceleration = 0.0

    // Initialize variable to store the total speed change
    var deltaSpeed = 0.0

    for (dataSample in accelerometerData) {
        // Extract x, y, and z acceleration values from the data sample
        val xAccelerationMg = dataSample.x
        val yAccelerationMg = dataSample.y
        val zAccelerationMg = dataSample.z

        // Calculate acceleration changes in mG (milligees)
        val deltaX = xAccelerationMg - prevXAccelerationMg
        val deltaY = yAccelerationMg - prevYAccelerationMg
        val deltaZ = zAccelerationMg - prevZAccelerationMg

        // Calculate linear acceleration change for the current reading in m/s²
        val linearAccelerationChange = calculateLinearAcceleration(deltaX, deltaY, deltaZ)

        // Accumulate the linear acceleration changes
        deltaLinearAcceleration += linearAccelerationChange

        // Calculate speed change for the current reading in m/s
        val speedChange = linearAccelerationChange * timeIntervalSeconds

        // Accumulate the speed changes
        deltaSpeed += speedChange

        // Update previous acceleration values for the next iteration
        prevXAccelerationMg = xAccelerationMg
        prevYAccelerationMg = yAccelerationMg
        prevZAccelerationMg = zAccelerationMg
    }

    println("Total Linear Acceleration Change (m/s²): $deltaLinearAcceleration")
    return Pair(deltaLinearAcceleration, deltaSpeed)
}

fun calculateLinearAcceleration(deltaX: Int, deltaY: Int, deltaZ: Int): Double {
    // Convert milligees to m/s² (1 mG = 0.001 m/s²)
    val accelerationX = deltaX * 0.001
    val accelerationY = deltaY * 0.001
    val accelerationZ = deltaZ * 0.001

    // Calculate the magnitude of linear acceleration
    return sqrt(accelerationX * accelerationX + accelerationY * accelerationY + accelerationZ * accelerationZ)
}