package com.example.hobbyyk_new.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SpotlightOverlay(
    targetCoordinates: LayoutCoordinates?,
    text: String,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    isLastStep: Boolean
) {
    if (targetCoordinates == null || !targetCoordinates.isAttached) return

    val bounds = targetCoordinates.boundsInWindow()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.99f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = Color.Black.copy(alpha = 0.7f))

            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(bounds.left - 10, bounds.top - 10),
                size = Size(bounds.width + 20, bounds.height + 20),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                blendMode = BlendMode.Clear
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text, fontSize = 16.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onSkip) {
                    Text("Skip")
                }
                Button(onClick = onNext) {
                    Text(if (isLastStep) "Selesai" else "Lanjut")
                }
            }
        }
    }
}