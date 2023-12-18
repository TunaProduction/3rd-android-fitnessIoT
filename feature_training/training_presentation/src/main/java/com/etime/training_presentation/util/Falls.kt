package com.etime.training_presentation.util

import com.polar.sdk.api.model.PolarAccelerometerData

fun detectFall(polarData: PolarAccelerometerData): Boolean {
    val heavyFallThreshold = 6000  // Increase this threshold for detecting only heavy falls in millig
    val durationThreshold = 400_000_000  // Duration threshold in nanoseconds
    var heavyFallDetected = false
    var startFallTime = 0L
    var anguloThreshold = 65; //faltaria definir  bien este parametro pero en el documento menciona 65 o 55.
    for (sample in polarData.samples) {
        val magnitude = Math.sqrt((sample.x * sample.x + sample.y * sample.y + sample.z * sample.z).toDouble()).toInt()

        if (magnitude > heavyFallThreshold && startFallTime == 0L) {
            // Potential heavy fall detected
            startFallTime = sample.timeStamp
        }

        if (startFallTime > 0L) {
            // Check if potential fall duration meets the criteria
            if (sample.timeStamp - startFallTime < durationThreshold) {


                //calcularAngulo

                val x2 = Math.pow(sample.x.toDouble(), 2.toDouble());
                val y2 = Math.pow(sample.y.toDouble(), 2.toDouble());
                val z2 = Math.pow(sample.z.toDouble(), 2.toDouble());

                val primerValor = Math.sqrt(y2+z2)/x2;  //lo de adentro del parentecis
                val segundoValor = Math.atan(primerValor); //tangente inversa del resultado
                val anguloActualEnGrados = Math.toDegrees(segundoValor); //multiplicado por 180/pi

                //aqui agregemos tambien un thereshold del "Angulo" en que deberia de estar el accelerometro para que sea una caida.
                if(anguloActualEnGrados>anguloThreshold)
                    heavyFallDetected = true


                break
            } else {
                // Reset start time as this is a false alarm
                startFallTime = 0L
            }
        }
    }

    return heavyFallDetected
}