package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Lock
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.AppUiState
import com.example.HomeTab
import com.example.InspireCategory
import com.example.InspireQuote
import com.example.R
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.OceanElevated
import com.example.ui.theme.OceanRim
import com.example.ui.theme.OceanTrench
import com.example.ui.theme.RoyalGold

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
  uiState: AppUiState,
  quotes: List<InspireQuote>,
  sharedTransitionScope: SharedTransitionScope,
  animatedVisibilityScope: AnimatedVisibilityScope,
  onSelectTab: (HomeTab) -> Unit,
  onSelectVibe: (String) -> Unit,
  onSendGoodVibes: () -> Unit,
  onToggleBreathing: () -> Unit,
  onSelectCategory: (InspireCategory) -> Unit,
  onNextQuote: () -> Unit,
  onToggleFavoriteQuote: (String) -> Unit,
  onUpdateMotto: (String) -> Unit,
  onUpdatePin: (String) -> Boolean,
  onResetPin: () -> Unit,
  onLockToPin: () -> Unit,
  onReturnToWelcome: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler {
    if (uiState.activeTab != HomeTab.HOME) {
      onSelectTab(HomeTab.HOME)
    } else {
      onReturnToWelcome()
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color.Transparent,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      Column(modifier = Modifier.fillMaxWidth().background(OceanTrench.copy(alpha = 0.95f))) {
        HorizontalDivider(color = OceanRim.copy(alpha = 0.55f), thickness = 1.dp)
        NavigationBar(
          containerColor = Color.Transparent,
          tonalElevation = 0.dp,
          windowInsets = WindowInsets.navigationBars,
        ) {
          NavigationBarItem(
            selected = uiState.activeTab == HomeTab.HOME,
            onClick = { onSelectTab(HomeTab.HOME) },
            modifier = Modifier.testTag("home_tab"),
            icon = {
              Icon(
                imageVector = if (uiState.activeTab == HomeTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                contentDescription = stringResource(R.string.home_tab_label),
              )
            },
            label = {
              Text(text = stringResource(R.string.home_tab_label), style = MaterialTheme.typography.labelLarge)
            },
            colors = navBarColors(),
          )
          NavigationBarItem(
            selected = uiState.activeTab == HomeTab.INSPIRE,
            onClick = { onSelectTab(HomeTab.INSPIRE) },
            modifier = Modifier.testTag("inspire_tab"),
            icon = {
              Icon(
                imageVector =
                  if (uiState.activeTab == HomeTab.INSPIRE) Icons.Filled.AutoAwesome
                  else Icons.Outlined.AutoAwesome,
                contentDescription = stringResource(R.string.inspire_tab_label),
              )
            },
            label = {
              Text(text = stringResource(R.string.inspire_tab_label), style = MaterialTheme.typography.labelLarge)
            },
            colors = navBarColors(),
          )
          NavigationBarItem(
            selected = uiState.activeTab == HomeTab.PROFILE,
            onClick = { onSelectTab(HomeTab.PROFILE) },
            modifier = Modifier.testTag("profile_tab"),
            icon = {
              Icon(
                imageVector = if (uiState.activeTab == HomeTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = stringResource(R.string.profile_tab_label),
              )
            },
            label = {
              Text(text = stringResource(R.string.profile_tab_label), style = MaterialTheme.typography.labelLarge)
            },
            colors = navBarColors(),
          )
        }
      }
    },
  ) { innerPadding ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .windowInsetsPadding(WindowInsets.statusBars)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(modifier = Modifier.widthIn(max = 580.dp).fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Text(
              text =
                when (uiState.activeTab) {
                  HomeTab.HOME -> stringResource(R.string.home_tab_label).uppercase()
                  HomeTab.INSPIRE -> stringResource(R.string.inspire_tab_label).uppercase()
                  HomeTab.PROFILE -> stringResource(R.string.profile_tab_label).uppercase()
                },
              style = MaterialTheme.typography.labelMedium,
              color = RoyalGold,
            )
            Text(
              text = stringResource(R.string.home_header_label),
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onBackground,
            )
          }

          Surface(
            shape = CircleShape,
            color = OceanElevated.copy(alpha = 0.85f),
            border = BorderStroke(1.dp, OceanRim),
          ) {
            IconButton(
              onClick = onReturnToWelcome,
              modifier = Modifier.size(48.dp).testTag("lock_app_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = stringResource(R.string.replay_welcome_cd),
                tint = BiolumCyan,
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        AnimatedContent(
          targetState = uiState.activeTab,
          transitionSpec = {
            fadeIn(animationSpec = tween(240)) togetherWith fadeOut(animationSpec = tween(180))
          },
          label = "home_tab_switcher",
        ) { tab ->
          when (tab) {
            HomeTab.HOME ->
              HomeTabContent(
                uiState = uiState,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onSelectVibe = onSelectVibe,
                onSendGoodVibes = onSendGoodVibes,
                onToggleBreathing = onToggleBreathing,
              )
            HomeTab.INSPIRE ->
              InspireTabContent(
                uiState = uiState,
                quotes = quotes,
                onSelectCategory = onSelectCategory,
                onNextQuote = onNextQuote,
                onToggleFavorite = onToggleFavoriteQuote,
              )
            HomeTab.PROFILE ->
              ProfileTabContent(
                uiState = uiState,
                onUpdateMotto = onUpdateMotto,
                onUpdatePin = onUpdatePin,
                onResetPin = onResetPin,
                onLockApp = onLockToPin,
              )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun navBarColors() =
  NavigationBarItemDefaults.colors(
    selectedIconColor = BiolumCyan,
    selectedTextColor = BiolumCyan,
    indicatorColor = BiolumCyan.copy(alpha = 0.16f),
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
  )
