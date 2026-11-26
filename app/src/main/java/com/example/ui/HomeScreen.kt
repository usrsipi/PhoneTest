package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.GreetingPeriod
import com.example.R
import com.example.WelcomeHomeUiState
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.OceanCardGlass
import com.example.ui.theme.OceanElevated
import com.example.ui.theme.OceanRim
import com.example.ui.theme.OceanTrench
import com.example.ui.theme.SeafoamTeal

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
  uiState: WelcomeHomeUiState,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  onReturnToWelcome: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onReturnToWelcome)

  var revealContent by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) { revealContent = true }

  val statusPulse = rememberInfiniteTransition(label = "status_dot")
  val dotAlpha by
    statusPulse.animateFloat(
      initialValue = 0.5f,
      targetValue = 1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "dot_alpha",
    )

  val boundsSpring = spring<Rect>(dampingRatio = 0.82f, stiffness = 320f)

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.Transparent,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      Column(modifier = Modifier.fillMaxWidth().background(OceanTrench.copy(alpha = 0.94f))) {
        HorizontalDivider(color = OceanRim.copy(alpha = 0.5f), thickness = 1.dp)
        NavigationBar(
          containerColor = Color.Transparent,
          tonalElevation = 0.dp,
          windowInsets = WindowInsets.navigationBars,
        ) {
          NavigationBarItem(
            selected = true,
            onClick = {},
            modifier = Modifier.testTag("home_tab"),
            icon = {
              Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = stringResource(R.string.home_tab_label),
              )
            },
            label = {
              Text(
                text = stringResource(R.string.home_tab_label),
                style = MaterialTheme.typography.labelLarge,
              )
            },
            colors =
              NavigationBarItemDefaults.colors(
                selectedIconColor = BiolumCyan,
                selectedTextColor = BiolumCyan,
                indicatorColor = BiolumCyan.copy(alpha = 0.16f),
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
              ),
          )
        }
      }
    },
  ) { innerPadding ->
    with(sharedTransitionScope) {
      Column(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Column(modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                text = stringResource(R.string.home_tab_label).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = BiolumCyan,
              )
              Text(
                text = stringResource(R.string.home_header_label),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
              )
            }

            Surface(
              shape = CircleShape,
              color = OceanElevated.copy(alpha = 0.8f),
              border = BorderStroke(1.dp, OceanRim),
            ) {
              IconButton(
                onClick = onReturnToWelcome,
                modifier = Modifier.size(48.dp).testTag("replay_welcome_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.RestartAlt,
                  contentDescription = stringResource(R.string.replay_welcome_cd),
                  tint = BiolumCyan,
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Hero Identity Card sharing bounds and emblem with the Welcome Screen
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
            border = BorderStroke(1.dp, OceanRim),
            tonalElevation = 0.dp,
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
              ) {
                Image(
                  painter = painterResource(id = R.drawable.ocean_crest_icon_1795668701334),
                  contentDescription = stringResource(R.string.ocean_crest_cd),
                  contentScale = ContentScale.Crop,
                  modifier =
                    Modifier
                      .size(64.dp)
                      .sharedElement(
                        state = rememberSharedContentState(key = "ocean_emblem"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ -> boundsSpring },
                      )
                      .clip(CircleShape)
                      .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(listOf(BiolumCyan, SeafoamTeal)),
                        shape = CircleShape,
                      ),
                )

                Surface(
                  shape = RoundedCornerShape(50),
                  color = SeafoamTeal.copy(alpha = 0.14f),
                  border = BorderStroke(1.dp, SeafoamTeal.copy(alpha = 0.45f)),
                  modifier = Modifier.testTag("status_badge"),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    Box(
                      modifier =
                        Modifier
                          .size(8.dp)
                          .graphicsLayer { alpha = dotAlpha }
                          .background(color = SeafoamTeal, shape = CircleShape),
                    )
                    Text(
                      text = stringResource(R.string.status_online),
                      style = MaterialTheme.typography.labelMedium,
                      color = SeafoamTeal,
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(22.dp))

              Text(
                text = stringResource(R.string.user_full_name),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.testTag("user_name_text"),
              )

              Spacer(modifier = Modifier.height(10.dp))

              Box(
                modifier =
                  Modifier
                    .size(width = 64.dp, height = 3.dp)
                    .background(
                      brush = Brush.horizontalGradient(listOf(BiolumCyan, SeafoamTeal)),
                      shape = CircleShape,
                    ),
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Time-Based Greeting Card with staggered spring entrance
          AnimatedVisibility(
            visible = revealContent,
            enter =
              fadeIn(animationSpec = tween(durationMillis = 280, delayMillis = 80)) +
                slideInVertically(
                  animationSpec = spring(dampingRatio = 0.85f, stiffness = 360f),
                  initialOffsetY = { it / 4 },
                ),
          ) {
            GreetingCard(
              greetingPeriod = uiState.greetingPeriod,
              formattedDateTime = uiState.formattedDateTime,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun GreetingCard(
  greetingPeriod: GreetingPeriod,
  formattedDateTime: String,
  modifier: Modifier = Modifier,
) {
  val greetingIcon =
    when (greetingPeriod) {
      GreetingPeriod.MORNING -> Icons.Rounded.WbTwilight
      GreetingPeriod.AFTERNOON -> Icons.Rounded.WbSunny
      GreetingPeriod.EVENING -> Icons.Rounded.NightsStay
      GreetingPeriod.NIGHT -> Icons.Rounded.AutoAwesome
    }

  Surface(
    modifier = modifier.fillMaxWidth().testTag("greeting_card"),
    shape = RoundedCornerShape(24.dp),
    color = OceanElevated.copy(alpha = 0.72f),
    border = BorderStroke(1.dp, OceanRim.copy(alpha = 0.85f)),
    tonalElevation = 0.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(22.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = BiolumCyan.copy(alpha = 0.14f),
          border = BorderStroke(1.dp, BiolumCyan.copy(alpha = 0.3f)),
        ) {
          Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = greetingIcon,
              contentDescription = null,
              tint = BiolumCyan,
              modifier = Modifier.size(22.dp),
            )
          }
        }

        if (formattedDateTime.isNotEmpty()) {
          Surface(
            shape = RoundedCornerShape(50),
            color = OceanTrench,
            border = BorderStroke(1.dp, OceanRim.copy(alpha = 0.6f)),
          ) {
            Text(
              text = formattedDateTime,
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = stringResource(greetingPeriod.titleRes),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = stringResource(greetingPeriod.bodyRes),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
