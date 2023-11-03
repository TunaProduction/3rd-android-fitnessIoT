package com.etime.core_ui.shape

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Circle(){
    val color = MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.size(150.dp), onDraw = {
        drawCircle(color = color)
    })
}