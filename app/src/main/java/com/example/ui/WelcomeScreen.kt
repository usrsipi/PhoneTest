package com.example.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.OceanAbyss
import com.example.ui.theme.OceanCardGlass
import com.example.ui.theme.OceanRim
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SeafoamTeal

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WelcomeScreen(
  formattedTime: String,
  formattedDate: String,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  onEnterClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val pulseTransition = rememberInfiniteTransition(label = "welcome_pulse")
  val auraScale by
    pulseTransition.animateFloat(
      initialValue = 0.94f,
      targetValue = 1.08f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "aura_scale",
    )
  val floatOffset by
    pulseTransition.animateFloat(
      initialValue = -5f,
      targetValue = 5f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "float_offset",
    )

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val buttonScale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.95f else 1f,
      animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
      label = "cta_squish",
    )

  val boundsSpring = spring<Rect>(dampingRatio = 0.80f, stiffness = 320f)

  with(sharedTransitionScope) {
    Box(
      modifier =
        modifier
          .fillMaxSize()
          .windowInsetsPadding(WindowInsets.safeDrawing)
          .padding(horizontal = 24.dp, vertical = 20.dp),
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
        border =
          BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(listOf(RoyalGold.copy(alpha = 0.45f), OceanRim, BiolumCyan.copy(alpha = 0.45f))),
          ),
        tonalElevation = 0.dp,
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp, vertical = 30.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          if (formattedTime.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(50),
              color = OceanAbyss.copy(alpha = 0.7f),
              border = BorderStroke(1.dp, OceanRim),
            ) {
              Text(
                text = "$formattedDate • $formattedTime",
                style = MaterialTheme.typography.labelMedium,
                color = BiolumCyan,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
              )
            }
            Spacer(modifier = Modifier.height(22.dp))
          }

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.graphicsLayer { translationY = floatOffset },
          ) {
            Box(
              modifier =
                Modifier
                  .size(164.dp)
                  .graphicsLayer {
                    scaleX = auraScale
                    scaleY = auraScale
                  }
                  .background(
                    brush =
                      Brush.radialGradient(
                        colors =
                          listOf(
                            RoyalGold.copy(alpha = 0.34f),
                            BiolumCyan.copy(alpha = 0.22f),
                            OceanAbyss.copy(alpha = 0f),
                          ),
                      ),
                    shape = CircleShape,
                  ),
            )
            Image(
              painter = painterResource(id = R.drawable.vahai_portrait),
              contentDescription = stringResource(R.string.portrait_cd),
              contentScale = ContentScale.Crop,
              modifier =
                Modifier
                  .size(134.dp)
                  .sharedElement(
                    state = rememberSharedContentState(key = "vahai_emblem"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ -> boundsSpring },
                  )
                  .clip(CircleShape)
                  .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(listOf(RoyalGold, BiolumCyan, SeafoamTeal)),
                    shape = CircleShape,
                  ),
            )
          }

          Spacer(modifier = Modifier.height(24.dp))

          Text(
            text = stringResource(R.string.welcome_eyebrow),
            style = MaterialTheme.typography.labelMedium,
            color = RoyalGold,
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("welcome_title_text"),
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
          )

          Spacer(modifier = Modifier.height(28.dp))

          Button(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onEnterClick()
            },
            interactionSource = interactionSource,
            modifier =
              Modifier
                .fillMaxWidth()
                .height(60.dp)
                .graphicsLayer {
                  scaleX = buttonScale
                  scaleY = buttonScale
                }
                .shadow(
                  elevation = 18.dp,
                  shape = RoundedCornerShape(20.dp),
                  ambientColor = BiolumCyan,
                  spotColor = RoyalGold,
                )
                .testTag("enter_pin_button"),
            shape = RoundedCornerShape(20.dp),
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
              Icon(
                imageVector = Icons.Rounded.LockOpen,
                contentDescription = null,
                tint = OceanAbyss,
                modifier = Modifier.size(20.dp),
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = stringResource(R.string.enter_home_cta),
                style = MaterialTheme.typography.titleMedium,
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = OceanAbyss,
                modifier = Modifier.size(20.dp),
              )
            }
          }
        }
      }
    }
  }
}
