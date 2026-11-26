package com.example.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.BiolumGlow
import com.example.ui.theme.OceanAbyss
import com.example.ui.theme.OceanCardGlass
import com.example.ui.theme.OceanRim
import com.example.ui.theme.SeafoamTeal

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WelcomeScreen(
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  onEnterHome: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val pulseTransition = rememberInfiniteTransition(label = "welcome_pulse")
  val auraScale by
    pulseTransition.animateFloat(
      initialValue = 0.96f,
      targetValue = 1.06f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "aura_scale",
    )

  val boundsSpring = spring<Rect>(dampingRatio = 0.82f, stiffness = 320f)

  with(sharedTransitionScope) {
    Box(
      modifier =
        modifier
          .fillMaxSize()
          .windowInsetsPadding(WindowInsets.safeDrawing)
          .padding(horizontal = 24.dp, vertical = 24.dp),
      contentAlignment = Alignment.Center,
    ) {
      Surface(
        modifier =
          Modifier
            .widthIn(max = 520.dp)
            .fillMaxWidth()
            .sharedBounds(
              sharedContentState = rememberSharedContentState(key = "portal_card"),
              animatedVisibilityScope = animatedVisibilityScope,
              boundsTransform = { _, _ -> boundsSpring },
            ),
        shape = RoundedCornerShape(32.dp),
        color = OceanCardGlass,
        border = BorderStroke(1.dp, OceanRim.copy(alpha = 0.8f)),
        tonalElevation = 0.dp,
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 36.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          Box(contentAlignment = Alignment.Center) {
            Box(
              modifier =
                Modifier
                  .size(132.dp)
                  .graphicsLayer {
                    scaleX = auraScale
                    scaleY = auraScale
                  }
                  .background(
                    brush =
                      Brush.radialGradient(
                        colors =
                          listOf(
                            BiolumCyan.copy(alpha = 0.34f),
                            SeafoamTeal.copy(alpha = 0.12f),
                            OceanAbyss.copy(alpha = 0f),
                          ),
                      ),
                    shape = CircleShape,
                  ),
            )
            Image(
              painter = painterResource(id = R.drawable.ocean_crest_icon_1795668701334),
              contentDescription = stringResource(R.string.ocean_crest_cd),
              contentScale = ContentScale.Crop,
              modifier =
                Modifier
                  .size(104.dp)
                  .sharedElement(
                    state = rememberSharedContentState(key = "ocean_emblem"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> boundsSpring },
                  )
                  .clip(CircleShape)
                  .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(BiolumCyan, SeafoamTeal)),
                    shape = CircleShape,
                  ),
            )
          }

          Spacer(modifier = Modifier.height(28.dp))

          Text(
            text = stringResource(R.string.welcome_eyebrow),
            style = MaterialTheme.typography.labelMedium,
            color = SeafoamTeal,
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("welcome_title_text"),
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(34.dp))

          Button(
            onClick = onEnterHome,
            modifier =
              Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(
                  elevation = 16.dp,
                  shape = RoundedCornerShape(18.dp),
                  ambientColor = BiolumCyan,
                  spotColor = BiolumCyan,
                )
                .testTag("enter_home_button"),
            shape = RoundedCornerShape(18.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = BiolumCyan,
                contentColor = OceanAbyss,
              ),
            contentPadding = PaddingValues(horizontal = 24.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              Text(
                text = stringResource(R.string.enter_home_cta),
                style = MaterialTheme.typography.titleMedium,
              )
              Spacer(modifier = Modifier.width(10.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = OceanAbyss,
                modifier = Modifier.size(20.dp),
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Box(
            modifier =
              Modifier
                .padding(top = 12.dp)
                .size(width = 48.dp, height = 3.dp)
                .background(
                  brush = Brush.horizontalGradient(listOf(BiolumCyan, SeafoamTeal, BiolumGlow)),
                  shape = CircleShape,
                ),
          )
        }
      }
    }
  }
}
