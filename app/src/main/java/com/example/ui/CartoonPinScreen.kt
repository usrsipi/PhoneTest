package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppUiState
import com.example.R
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.CartoonInk
import com.example.ui.theme.CartoonPurple
import com.example.ui.theme.HibiscusCoral
import com.example.ui.theme.IceWhite
import com.example.ui.theme.OceanCardGlass
import com.example.ui.theme.OceanElevated
import com.example.ui.theme.OceanRim
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SeafoamTeal

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CartoonPinScreen(
  uiState: AppUiState,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  onDigitPressed: (Char) -> Unit,
  onBackspace: () -> Unit,
  onResetPin: () -> Unit,
  onBackToWelcome: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onBackToWelcome)

  val haptic = LocalHapticFeedback.current
  val shakeOffset = remember { Animatable(0f) }

  LaunchedEffect(uiState.pinErrorNonce) {
    if (uiState.pinErrorNonce > 0) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      shakeOffset.animateTo(
        targetValue = 0f,
        animationSpec =
          keyframes {
            durationMillis = 420
            (-22f) at 60
            22f at 130
            (-18f) at 200
            18f at 270
            (-10f) at 340
            0f at 420
          },
      )
    }
  }

  val burstProgress = remember { Animatable(0f) }
  LaunchedEffect(uiState.isPinSuccess) {
    if (uiState.isPinSuccess) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      burstProgress.snapTo(0f)
      burstProgress.animateTo(1f, animationSpec = tween(550, easing = FastOutSlowInEasing))
    }
  }

  val boundsSpring = spring<Rect>(dampingRatio = 0.80f, stiffness = 320f)

  with(sharedTransitionScope) {
    Box(
      modifier =
        modifier
          .fillMaxSize()
          .windowInsetsPadding(WindowInsets.safeDrawing)
          .padding(horizontal = 20.dp, vertical = 16.dp),
      contentAlignment = Alignment.Center,
    ) {
      Surface(
        modifier =
          Modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .sharedBounds(
              sharedContentState = rememberSharedContentState(key = "portal_card"),
              animatedVisibilityScope = animatedVisibilityScope,
              boundsTransform = { _, _ -> boundsSpring },
            ),
        shape = RoundedCornerShape(32.dp),
        color = OceanCardGlass,
        border = BorderStroke(2.dp, OceanRim),
      ) {
        Box(modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier =
              Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Surface(
                shape = CircleShape,
                color = OceanElevated,
                border = BorderStroke(2.dp, CartoonInk),
              ) {
                IconButton(
                  onClick = onBackToWelcome,
                  modifier = Modifier.size(48.dp).testTag("pin_back_button"),
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.pin_back_cd),
                    tint = IceWhite,
                  )
                }
              }

              Image(
                painter = painterResource(id = R.drawable.vahai_portrait),
                contentDescription = stringResource(R.string.portrait_cd),
                contentScale = ContentScale.Crop,
                modifier =
                  Modifier
                    .size(54.dp)
                    .sharedElement(
                      state = rememberSharedContentState(key = "vahai_emblem"),
                      animatedVisibilityScope = animatedVisibilityScope,
                      boundsTransform = { _, _ -> boundsSpring },
                    )
                    .clip(CircleShape)
                    .border(2.dp, RoyalGold, CircleShape),
              )

              Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            CartoonLockMascot(
              filledCount = uiState.enteredPin.length,
              isError = uiState.isPinError,
              isSuccess = uiState.isPinSuccess,
              modifier = Modifier.graphicsLayer { translationX = shakeOffset.value },
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = stringResource(R.string.pin_title),
              style = MaterialTheme.typography.headlineMedium,
              color = IceWhite,
              textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))

            AnimatedContent(
              targetState =
                when {
                  uiState.isPinSuccess -> stringResource(R.string.pin_success_msg)
                  uiState.isPinError -> stringResource(R.string.pin_error_msg)
                  else -> stringResource(R.string.pin_subtitle)
                },
              label = "pin_status_text",
            ) { statusCopy ->
              Text(
                text = statusCopy,
                style = MaterialTheme.typography.bodyMedium,
                color =
                  when {
                    uiState.isPinSuccess -> SeafoamTeal
                    uiState.isPinError -> HibiscusCoral
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                  },
                textAlign = TextAlign.Center,
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4 Cartoon Pop PIN Slots
            Row(
              modifier =
                Modifier
                  .graphicsLayer { translationX = shakeOffset.value }
                  .testTag("pin_dots_row"),
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              for (index in 0..3) {
                val isFilled = index < uiState.enteredPin.length
                val slotScale by
                  animateFloatAsState(
                    targetValue = if (isFilled) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = 0.42f, stiffness = 520f),
                    label = "pin_dot_scale_$index",
                  )
                val slotColor by
                  animateColorAsState(
                    targetValue =
                      when {
                        uiState.isPinSuccess -> SeafoamTeal
                        uiState.isPinError -> HibiscusCoral
                        isFilled -> RoyalGold
                        else -> OceanElevated
                      },
                    label = "pin_dot_color_$index",
                  )

                Box(
                  modifier =
                    Modifier
                      .size(48.dp)
                      .graphicsLayer {
                        scaleX = slotScale
                        scaleY = slotScale
                      }
                      .background(color = CartoonInk, shape = RoundedCornerShape(16.dp))
                      .offset(y = (-3).dp)
                      .background(color = slotColor, shape = RoundedCornerShape(16.dp))
                      .border(2.5.dp, CartoonInk, RoundedCornerShape(16.dp)),
                  contentAlignment = Alignment.Center,
                ) {
                  if (isFilled) {
                    Text(
                      text = if (uiState.isPinSuccess) "★" else "●",
                      color = CartoonInk,
                      fontSize = 18.sp,
                      fontWeight = FontWeight.Bold,
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Friendly starter PIN pill so user can always unlock effortlessly
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              Surface(
                shape = RoundedCornerShape(50),
                color = RoyalGold.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f)),
              ) {
                Text(
                  text =
                    if (uiState.isUsingDefaultPin) stringResource(R.string.pin_hint_default)
                    else stringResource(R.string.pin_hint_custom),
                  style = MaterialTheme.typography.labelMedium,
                  color = RoyalGold,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                )
              }

              if (!uiState.isUsingDefaultPin) {
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(onClick = onResetPin) {
                  Text(
                    text = stringResource(R.string.pin_reset_default),
                    style = MaterialTheme.typography.labelMedium,
                    color = BiolumCyan,
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Cartoon 3D-pop Keypad
            val keyColors = listOf(BiolumCyan, RoyalGold, SeafoamTeal, CartoonPurple, HibiscusCoral)
            val rows =
              listOf(
                listOf('1', '2', '3'),
                listOf('4', '5', '6'),
                listOf('7', '8', '9'),
              )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              rows.forEachIndexed { rowIdx, rowDigits ->
                Row(
                  horizontalArrangement = Arrangement.spacedBy(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  rowDigits.forEachIndexed { colIdx, digit ->
                    val color = keyColors[(rowIdx * 3 + colIdx) % keyColors.size]
                    CartoonDigitKey(
                      label = digit.toString(),
                      faceColor = color,
                      onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDigitPressed(digit)
                      },
                      modifier = Modifier.testTag("pin_key_$digit"),
                    )
                  }
                }
              }

              Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Spacer(modifier = Modifier.size(width = 76.dp, height = 62.dp))
                CartoonDigitKey(
                  label = "0",
                  faceColor = RoyalGold,
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onDigitPressed('0')
                  },
                  modifier = Modifier.testTag("pin_key_0"),
                )
                CartoonActionKey(
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onBackspace()
                  },
                  modifier = Modifier.testTag("pin_key_backspace"),
                )
              }
            }
          }

          if (uiState.isPinSuccess) {
            ConfettiBurstOverlay(progress = burstProgress.value)
          }
        }
      }
    }
  }
}

@Composable
private fun CartoonDigitKey(
  label: String,
  faceColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.88f else 1f,
      animationSpec = spring(dampingRatio = 0.45f, stiffness = 550f),
      label = "digit_squish",
    )
  val pressOffsetY by
    animateFloatAsState(
      targetValue = if (isPressed) 4f else 0f,
      animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
      label = "digit_drop",
    )

  Box(
    modifier =
      modifier
        .size(width = 76.dp, height = 62.dp)
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        }
        .clip(RoundedCornerShape(20.dp))
        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    contentAlignment = Alignment.TopCenter,
  ) {
    // Cartoon solid comic shadow ledge
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(top = 5.dp)
          .background(CartoonInk, RoundedCornerShape(20.dp)),
    )
    // Top colorful tactile cap
    Box(
      modifier =
        Modifier
          .fillMaxWidth()
          .height(57.dp)
          .graphicsLayer { translationY = pressOffsetY }
          .background(faceColor, RoundedCornerShape(20.dp))
          .border(2.5.dp, CartoonInk, RoundedCornerShape(20.dp)),
      contentAlignment = Alignment.Center,
    ) {
      // Glossy cartoon highlight
      Box(
        modifier =
          Modifier
            .align(Alignment.TopCenter)
            .padding(top = 5.dp)
            .size(width = 36.dp, height = 5.dp)
            .background(Color.White.copy(alpha = 0.42f), CircleShape),
      )
      Text(
        text = label,
        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold),
        color = CartoonInk,
      )
    }
  }
}

@Composable
private fun CartoonActionKey(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.88f else 1f,
      animationSpec = spring(dampingRatio = 0.45f, stiffness = 550f),
      label = "back_squish",
    )

  Box(
    modifier =
      modifier
        .size(width = 76.dp, height = 62.dp)
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        }
        .clip(RoundedCornerShape(20.dp))
        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    contentAlignment = Alignment.TopCenter,
  ) {
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(top = 5.dp)
          .background(CartoonInk, RoundedCornerShape(20.dp)),
    )
    Box(
      modifier =
        Modifier
          .fillMaxWidth()
          .height(57.dp)
          .background(OceanElevated, RoundedCornerShape(20.dp))
          .border(2.5.dp, CartoonInk, RoundedCornerShape(20.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.Backspace,
        contentDescription = stringResource(R.string.pin_backspace_cd),
        tint = IceWhite,
        modifier = Modifier.size(24.dp),
      )
    }
  }
}

@Composable
private fun CartoonLockMascot(
  filledCount: Int,
  isError: Boolean,
  isSuccess: Boolean,
  modifier: Modifier = Modifier,
) {
  val wiggleTransition = rememberInfiniteTransition(label = "mascot_wiggle")
  val tilt by
    wiggleTransition.animateFloat(
      initialValue = -3f,
      targetValue = 3f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "mascot_tilt",
    )

  val bodyScale by
    animateFloatAsState(
      targetValue =
        when {
          isSuccess -> 1.16f
          filledCount > 0 -> 1.05f
          else -> 1f
        },
      animationSpec = spring(dampingRatio = 0.45f, stiffness = 450f),
      label = "mascot_pop",
    )

  val bodyColor by
    animateColorAsState(
      targetValue =
        when {
          isSuccess -> SeafoamTeal
          isError -> HibiscusCoral
          else -> RoyalGold
        },
      label = "mascot_color",
    )

  Canvas(
    modifier =
      modifier
        .size(84.dp)
        .graphicsLayer {
          rotationZ = if (isSuccess) 0f else tilt
          scaleX = bodyScale
          scaleY = bodyScale
        },
  ) {
    val w = size.width
    val h = size.height

    // Shackle arch (pops up when unlocked!)
    val shackleLift = if (isSuccess) -h * 0.12f else 0f
    drawArc(
      color = BiolumCyan,
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = false,
      topLeft = Offset(w * 0.27f, h * 0.10f + shackleLift),
      size = Size(w * 0.46f, h * 0.42f),
      style = Stroke(width = w * 0.11f, cap = StrokeCap.Round),
    )
    drawArc(
      color = CartoonInk,
      startAngle = 180f,
      sweepAngle = 180f,
      useCenter = false,
      topLeft = Offset(w * 0.27f, h * 0.10f + shackleLift),
      size = Size(w * 0.46f, h * 0.42f),
      style = Stroke(width = w * 0.03f, cap = StrokeCap.Round),
    )

    // Cartoon body shadow
    drawRoundRect(
      color = CartoonInk,
      topLeft = Offset(w * 0.15f, h * 0.37f),
      size = Size(w * 0.70f, h * 0.54f),
      cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
    )

    // Cartoon lock face
    drawRoundRect(
      color = bodyColor,
      topLeft = Offset(w * 0.15f, h * 0.33f),
      size = Size(w * 0.70f, h * 0.52f),
      cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
    )
    drawRoundRect(
      color = CartoonInk,
      topLeft = Offset(w * 0.15f, h * 0.33f),
      size = Size(w * 0.70f, h * 0.52f),
      cornerRadius = CornerRadius(w * 0.18f, w * 0.18f),
      style = Stroke(width = 6f),
    )

    // Cute cartoon eyes
    val leftEye = Offset(w * 0.37f, h * 0.54f)
    val rightEye = Offset(w * 0.63f, h * 0.54f)
    if (isError) {
      // Dizzy X eyes
      val r = w * 0.045f
      listOf(leftEye, rightEye).forEach { c ->
        drawLine(CartoonInk, Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r), strokeWidth = 5f, cap = StrokeCap.Round)
        drawLine(CartoonInk, Offset(c.x + r, c.y - r), Offset(c.x - r, c.y + r), strokeWidth = 5f, cap = StrokeCap.Round)
      }
    } else {
      drawCircle(Color.White, radius = w * 0.068f, center = leftEye)
      drawCircle(Color.White, radius = w * 0.068f, center = rightEye)
      val pupilOffsetX = (filledCount.coerceAtMost(4) - 1.5f) * 2f
      drawCircle(CartoonInk, radius = w * 0.036f, center = leftEye + Offset(pupilOffsetX, 0f))
      drawCircle(CartoonInk, radius = w * 0.036f, center = rightEye + Offset(pupilOffsetX, 0f))
    }

    // Mouth expression
    val smilePath = Path()
    if (isError) {
      smilePath.moveTo(w * 0.40f, h * 0.73f)
      smilePath.quadraticTo(w * 0.50f, h * 0.65f, w * 0.60f, h * 0.73f)
    } else {
      val smileDepth = if (isSuccess) h * 0.80f else h * 0.74f
      smilePath.moveTo(w * 0.38f, h * 0.66f)
      smilePath.quadraticTo(w * 0.50f, smileDepth, w * 0.62f, h * 0.66f)
    }
    drawPath(smilePath, color = CartoonInk, style = Stroke(width = 5f, cap = StrokeCap.Round))
  }
}

@Composable
private fun ConfettiBurstOverlay(progress: Float) {
  val colors = listOf(RoyalGold, BiolumCyan, SeafoamTeal, HibiscusCoral, CartoonPurple)
  Canvas(modifier = Modifier.fillMaxSize()) {
    val center = Offset(size.width / 2f, size.height * 0.35f)
    val count = 18
    for (i in 0 until count) {
      val angle = (i * (360f / count)) * (Math.PI / 180.0)
      val distance = (size.minDimension * 0.45f) * progress
      val x = center.x + (kotlin.math.cos(angle) * distance).toFloat()
      val y = center.y + (kotlin.math.sin(angle) * distance).toFloat()
      drawCircle(
        color = colors[i % colors.size].copy(alpha = (1f - progress).coerceIn(0f, 1f)),
        radius = 10f * (1f - progress * 0.5f),
        center = Offset(x, y),
      )
    }
  }
}
