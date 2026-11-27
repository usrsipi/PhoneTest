package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.OceanAbyss
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SeafoamTeal
import kotlin.math.sin

private data class AmbientParticle(
  val xFraction: Float,
  val startY: Float,
  val speed: Float,
  val radius: Float,
  val isGold: Boolean,
)

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

  val particlePhase by
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 12000, easing = LinearEasing),
          repeatMode = RepeatMode.Restart,
        ),
      label = "particle_rise",
    )

  val particles = remember {
    listOf(
      AmbientParticle(0.12f, 0.85f, 1.0f, 5f, false),
      AmbientParticle(0.28f, 0.40f, 0.8f, 3.5f, true),
      AmbientParticle(0.45f, 0.92f, 1.2f, 4.5f, false),
      AmbientParticle(0.62f, 0.25f, 0.9f, 4f, true),
      AmbientParticle(0.78f, 0.70f, 1.1f, 5.5f, false),
      AmbientParticle(0.88f, 0.55f, 0.75f, 3.5f, true),
      AmbientParticle(0.18f, 0.15f, 1.05f, 4f, false),
      AmbientParticle(0.54f, 0.60f, 0.85f, 3f, true),
    )
  }

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
          x = size.width * (0.26f + 0.14f * drift),
          y = size.height * (0.18f + 0.04f * drift),
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

      val goldGlowCenter =
        Offset(
          x = size.width * (0.80f - 0.10f * drift),
          y = size.height * (0.30f + 0.06f * drift),
        )
      drawCircle(
        brush =
          Brush.radialGradient(
            colors = listOf(RoyalGold.copy(alpha = 0.11f), Color.Transparent),
            center = goldGlowCenter,
            radius = size.maxDimension * 0.40f,
          ),
        center = goldGlowCenter,
        radius = size.maxDimension * 0.40f,
      )

      val bottomGlowCenter =
        Offset(
          x = size.width * (0.72f - 0.12f * drift),
          y = size.height * (0.82f - 0.05f * drift),
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

      val waveBaseY = size.height * 0.74f
      for (layer in 0..2) {
        val path = Path()
        val amplitude = 16f + layer * 10f
        val yOffset = waveBaseY + layer * 42f
        val phase = drift * 2.5f + layer * 1.15f
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

      particles.forEachIndexed { idx, p ->
        val rawY = (p.startY - particlePhase * p.speed)
        val wrappedY = ((rawY % 1f) + 1f) % 1f
        val sway = sin((particlePhase * 6.28f) + idx) * 14f
        val center = Offset(size.width * p.xFraction + sway, size.height * wrappedY)
        val tint = if (p.isGold) RoyalGold else BiolumCyan
        val fade = (1f - kotlin.math.abs(wrappedY - 0.5f) * 1.8f).coerceIn(0.15f, 0.65f)
        drawCircle(
          color = tint.copy(alpha = fade),
          radius = p.radius,
          center = center,
        )
      }
    }
    content()
  }
}
