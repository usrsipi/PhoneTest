package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.OceanAbyss
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.SeafoamTeal
import kotlin.math.sin

@Composable
fun OceanBackdrop(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "ocean_ambient")
  val drift by
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 7000, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "glow_drift",
    )

  val surfaceBase = MaterialTheme.colorScheme.background

  Box(modifier = modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawRect(
        brush =
          Brush.verticalGradient(
            colors = listOf(OceanAbyss, OceanDeep, surfaceBase),
          ),
      )

      val topGlowCenter =
        Offset(
          x = size.width * (0.28f + 0.14f * drift),
          y = size.height * (0.22f + 0.04f * drift),
        )
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(BiolumCyan.copy(alpha = 0.22f), Color.Transparent),
            center = topGlowCenter,
            radius = size.maxDimension * 0.55f,
          ),
        center = topGlowCenter,
        radius = size.maxDimension * 0.55f,
      )

      val bottomGlowCenter =
        Offset(
          x = size.width * (0.76f - 0.12f * drift),
          y = size.height * (0.78f - 0.05f * drift),
        )
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(SeafoamTeal.copy(alpha = 0.16f), Color.Transparent),
            center = bottomGlowCenter,
            radius = size.maxDimension * 0.50f,
          ),
        center = bottomGlowCenter,
        radius = size.maxDimension * 0.50f,
      )

      val waveBaseY = size.height * 0.72f
      for (layer in 0..2) {
        val path = Path()
        val amplitude = 18f + layer * 10f
        val yOffset = waveBaseY + layer * 44f
        val phase = drift * 2.4f + layer * 1.1f
        path.moveTo(0f, yOffset)
        var x = 0f
        val step = (size.width / 24f).coerceAtLeast(16f)
        while (x <= size.width + step) {
          val normalized = (x / size.width) * 4.5f
          val y = yOffset + sin(normalized + phase) * amplitude
          path.lineTo(x, y)
          x += step
        }
        drawPath(
          path = path,
          color = BiolumCyan.copy(alpha = 0.08f - layer * 0.018f),
          style = Stroke(width = 2f),
        )
      }
    }
    content()
  }
}
