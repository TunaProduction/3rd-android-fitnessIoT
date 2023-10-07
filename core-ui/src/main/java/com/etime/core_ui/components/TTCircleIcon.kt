package com.etime.core_ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.etime.core_ui.LocalSpacing
import com.etime.core_ui.shape.Circle

@Composable
fun TTCircleIcon(
    iconId: Int
) {
    val color = MaterialTheme.colorScheme.secondary

    Box(
        contentAlignment = Alignment.Center
    ) {

        Circle()

        Icon(
            modifier = Modifier.size(LocalSpacing.current.spaceExtraLarge),
            painter = painterResource(id = iconId),
            contentDescription = "Training icon",
            tint = Color.White
        )
    }
}