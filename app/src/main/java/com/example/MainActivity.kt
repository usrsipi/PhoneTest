package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HomeScreen
import com.example.ui.OceanBackdrop
import com.example.ui.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalSharedTransitionApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
    )
    setContent {
      MyApplicationTheme {
        val viewModel: WelcomeHomeViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        OceanBackdrop {
          SharedTransitionLayout {
            AnimatedContent(
              targetState = uiState.isHomeEntered,
              transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 320)) togetherWith
                  fadeOut(animationSpec = tween(durationMillis = 240))
              },
              label = "welcome_to_home_transition",
            ) { isHomeEntered ->
              if (isHomeEntered) {
                HomeScreen(
                  uiState = uiState,
                  sharedTransitionScope = this@SharedTransitionLayout,
                  animatedVisibilityScope = this@AnimatedContent,
                  onReturnToWelcome = viewModel::returnToWelcome,
                )
              } else {
                WelcomeScreen(
                  sharedTransitionScope = this@SharedTransitionLayout,
                  animatedVisibilityScope = this@AnimatedContent,
                  onEnterHome = viewModel::enterHome,
                )
              }
            }
          }
        }
      }
    }
  }
}
