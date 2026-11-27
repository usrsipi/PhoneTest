package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.AppUiState
import com.example.GreetingPeriod
import com.example.HomeTab
import com.example.R
import com.example.VIBE_OPTIONS
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.CartoonInk
import com.example.ui.theme.CartoonPurple
import com.example.ui.theme.HibiscusCoral
import com.example.ui.theme.IceWhite
import com.example.ui.theme.OceanAbyss
import com.example.ui.theme.OceanCardGlass
import com.example.ui.theme.OceanElevated
import com.example.ui.theme.OceanRim
import com.example.ui.theme.OceanTrench
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SeafoamTeal
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
fun HomeTabContent(
  uiState: AppUiState,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  onSelectVibe: (String) -> Unit,
  onSendGoodVibes: () -> Unit,
  onToggleBreathing: () -> Unit,
  onJumpToTab: (HomeTab) -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val statusPulse = rememberInfiniteTransition(label = "home_status_pulse")
  val dotAlpha by
    statusPulse.animateFloat(
      initialValue = 0.45f,
      targetValue = 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "dot_alpha",
    )

  val boundsSpring = spring<Rect>(dampingRatio = 0.80f, stiffness = 320f)

  with(sharedTransitionScope) {
    Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Hero Identity Card for Sipiliano Vahai
      Surface(
        modifier =
          Modifier
            .fillMaxWidth()
            .sharedBounds(
              sharedContentState = rememberSharedContentState(key = "portal_card"),
              animatedVisibilityScope = animatedVisibilityScope,
              boundsTransform = { _, _ -> boundsSpring },
            ),
        shape = RoundedCornerShape(28.dp),
        color = OceanCardGlass,
        border =
          BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(listOf(RoyalGold.copy(alpha = 0.55f), OceanRim, BiolumCyan.copy(alpha = 0.5f))),
          ),
      ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            Image(
              painter = painterResource(id = R.drawable.vahai_portrait),
              contentDescription = stringResource(R.string.portrait_cd),
              contentScale = ContentScale.Crop,
              modifier =
                Modifier
                  .size(74.dp)
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

            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Surface(
                  shape = RoundedCornerShape(50),
                  color = SeafoamTeal.copy(alpha = 0.14f),
                  border = BorderStroke(1.dp, SeafoamTeal.copy(alpha = 0.45f)),
                  modifier = Modifier.testTag("status_badge"),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    Box(
                      modifier =
                        Modifier
                          .size(8.dp)
                          .graphicsLayer { alpha = dotAlpha }
                          .background(SeafoamTeal, CircleShape),
                    )
                    Text(
                      text = stringResource(R.string.status_online),
                      style = MaterialTheme.typography.labelMedium,
                      color = SeafoamTeal,
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(50),
                  color = RoyalGold.copy(alpha = 0.16f),
                  border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.45f)),
                ) {
                  Text(
                    text = uiState.donorRank,
                    style = MaterialTheme.typography.labelMedium,
                    color = RoyalGold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  )
                }
              }

              Text(
                text = stringResource(R.string.user_full_name),
                style = MaterialTheme.typography.displayMedium,
                color = IceWhite,
                modifier = Modifier.testTag("user_name_text"),
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "\"${uiState.customMotto}\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "TODAY'S VIBE",
            style = MaterialTheme.typography.labelMedium,
            color = BiolumCyan,
          )

          Spacer(modifier = Modifier.height(8.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            VIBE_OPTIONS.forEach { vibe ->
              val isSelected = uiState.selectedVibe == vibe
              val chipBg by
                animateColorAsState(
                  targetValue = if (isSelected) BiolumCyan else OceanElevated,
                  label = "vibe_bg",
                )
              val chipFg by
                animateColorAsState(
                  targetValue = if (isSelected) OceanAbyss else IceWhite,
                  label = "vibe_fg",
                )
              Surface(
                shape = RoundedCornerShape(50),
                color = chipBg,
                border = BorderStroke(1.5.dp, if (isSelected) BiolumCyan else OceanRim),
                modifier =
                  Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                      onSelectVibe(vibe)
                    }
                    .testTag("vibe_chip_$vibe"),
              ) {
                Text(
                  text = vibe,
                  style = MaterialTheme.typography.labelLarge,
                  color = chipFg,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                )
              }
            }
          }
        }
      }

      // Cartoon Donation Quick-Launch Banner
      Surface(
        modifier = Modifier.fillMaxWidth().testTag("home_donate_banner"),
        shape = RoundedCornerShape(24.dp),
        color = RoyalGold.copy(alpha = 0.14f),
        border = BorderStroke(2.dp, RoyalGold.copy(alpha = 0.6f)),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Surface(
                shape = CircleShape,
                color = RoyalGold,
                border = BorderStroke(2.dp, CartoonInk),
              ) {
                Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Rounded.CardGiftcard,
                    contentDescription = null,
                    tint = CartoonInk,
                    modifier = Modifier.size(20.dp),
                  )
                }
              }
              Column {
                Text(
                  text = "Cartoon Gift Vault (Demo)",
                  style = MaterialTheme.typography.titleMedium,
                  color = IceWhite,
                )
                Text(
                  text = "$${uiState.communityGoalRaised} of $${uiState.communityGoalTarget} island goal",
                  style = MaterialTheme.typography.bodyMedium,
                  color = RoyalGold,
                )
              }
            }
          }

          val goalProgress by
            animateFloatAsState(
              targetValue =
                (uiState.communityGoalRaised.toFloat() / uiState.communityGoalTarget.toFloat())
                  .coerceIn(0f, 1f),
              animationSpec = tween(700),
              label = "goal_bar",
            )
          LinearProgressIndicator(
            progress = { goalProgress },
            modifier = Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),
            color = RoyalGold,
            trackColor = OceanTrench,
            strokeCap = StrokeCap.Round,
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Button(
              onClick = { onJumpToTab(HomeTab.GIVE) },
              modifier = Modifier.weight(1f).height(48.dp).testTag("jump_to_give_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = CartoonInk),
            ) {
              Text(text = "Donate (Demo)", style = MaterialTheme.typography.labelLarge)
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
            }
            Button(
              onClick = { onJumpToTab(HomeTab.AI) },
              modifier = Modifier.weight(1f).height(48.dp).testTag("jump_to_ai_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BiolumCyan, contentColor = OceanAbyss),
            ) {
              Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Ask Vahai AI", style = MaterialTheme.typography.labelLarge)
            }
          }
        }
      }

      // Live Greeting + Day Progress Card
      LiveGreetingCard(
        greetingPeriod = uiState.greetingPeriod,
        formattedTime = uiState.formattedTime,
        formattedDate = uiState.formattedDate,
        dayProgress = uiState.dayProgress,
      )

      // Interactive Fun Widgets Row: Good Vibes Counter + Calm Tide Breath Orb
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        GoodVibesWidget(
          count = uiState.goodVibesCount,
          onBoost = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSendGoodVibes()
          },
          modifier = Modifier.weight(1f),
        )

        BreatheTideWidget(
          isActive = uiState.isBreathingActive,
          onToggle = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onToggleBreathing()
          },
          modifier = Modifier.weight(1f),
        )
      }
    }
  }
}

@Composable
private fun LiveGreetingCard(
  greetingPeriod: GreetingPeriod,
  formattedTime: String,
  formattedDate: String,
  dayProgress: Float,
) {
  val greetingIcon =
    when (greetingPeriod) {
      GreetingPeriod.MORNING -> Icons.Rounded.WbTwilight
      GreetingPeriod.AFTERNOON -> Icons.Rounded.WbSunny
      GreetingPeriod.EVENING -> Icons.Rounded.NightsStay
      GreetingPeriod.NIGHT -> Icons.Rounded.AutoAwesome
    }

  val animatedProgress by
    animateFloatAsState(
      targetValue = dayProgress,
      animationSpec = tween(durationMillis = 700),
      label = "day_progress",
    )

  Surface(
    modifier = Modifier.fillMaxWidth().testTag("greeting_card"),
    shape = RoundedCornerShape(24.dp),
    color = OceanElevated.copy(alpha = 0.78f),
    border = BorderStroke(1.dp, OceanRim),
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = BiolumCyan.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, BiolumCyan.copy(alpha = 0.35f)),
          ) {
            Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
              Icon(
                imageVector = greetingIcon,
                contentDescription = null,
                tint = BiolumCyan,
                modifier = Modifier.size(22.dp),
              )
            }
          }
          Column {
            Text(
              text = stringResource(greetingPeriod.titleRes),
              style = MaterialTheme.typography.titleLarge,
              color = IceWhite,
            )
            Text(
              text = formattedDate,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(50),
          color = OceanTrench,
          border = BorderStroke(1.dp, OceanRim),
        ) {
          Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelLarge,
            color = RoyalGold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = stringResource(greetingPeriod.bodyRes),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = "DAY FLOW",
          style = MaterialTheme.typography.labelMedium,
          color = SeafoamTeal,
        )
        Text(
          text = "${(animatedProgress * 100).roundToInt()}%",
          style = MaterialTheme.typography.labelMedium,
          color = IceWhite,
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
        color = SeafoamTeal,
        trackColor = OceanTrench,
        strokeCap = StrokeCap.Round,
      )
    }
  }
}

@Composable
private fun GoodVibesWidget(
  count: Int,
  onBoost: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier =
      modifier
        .clip(RoundedCornerShape(24.dp))
        .clickable(onClick = onBoost)
        .testTag("good_vibes_button"),
    shape = RoundedCornerShape(24.dp),
    color = HibiscusCoral.copy(alpha = 0.14f),
    border = BorderStroke(1.5.dp, HibiscusCoral.copy(alpha = 0.5f)),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Rounded.Favorite,
          contentDescription = null,
          tint = HibiscusCoral,
          modifier = Modifier.size(22.dp),
        )
        Icon(
          imageVector = Icons.Rounded.Bolt,
          contentDescription = null,
          tint = RoyalGold,
          modifier = Modifier.size(18.dp),
        )
      }
      AnimatedContent(targetState = count, label = "vibe_count") { value ->
        Text(
          text = "$value",
          style = MaterialTheme.typography.headlineMedium,
          color = IceWhite,
        )
      }
      Text(
        text = "Good Vibes\nTap to boost!",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun BreatheTideWidget(
  isActive: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val breatheTransition = rememberInfiniteTransition(label = "breathe_tide")
  val ringScale by
    breatheTransition.animateFloat(
      initialValue = 0.85f,
      targetValue = 1.25f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "ring_scale",
    )

  Surface(
    modifier =
      modifier
        .clip(RoundedCornerShape(24.dp))
        .clickable(onClick = onToggle)
        .testTag("breathe_tide_button"),
    shape = RoundedCornerShape(24.dp),
    color = CartoonPurple.copy(alpha = 0.14f),
    border = BorderStroke(1.5.dp, CartoonPurple.copy(alpha = 0.5f)),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Box(
        modifier = Modifier.size(34.dp),
        contentAlignment = Alignment.Center,
      ) {
        if (isActive) {
          Box(
            modifier =
              Modifier
                .size(30.dp)
                .graphicsLayer {
                  scaleX = ringScale
                  scaleY = ringScale
                }
                .background(CartoonPurple.copy(alpha = 0.35f), CircleShape),
          )
        }
        Icon(
          imageVector = Icons.Rounded.Air,
          contentDescription = null,
          tint = if (isActive) SeafoamTeal else CartoonPurple,
          modifier = Modifier.size(22.dp),
        )
      }

      Text(
        text = if (isActive) "Breathing" else "Calm Tide",
        style = MaterialTheme.typography.titleLarge,
        color = IceWhite,
      )
      Text(
        text = if (isActive) "Inhale... Exhale...\nTap to pause" else "4s Ocean Breath\nTap to start",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
