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
import com.example.ui.CartoonPinScreen
import com.example.ui.HomeScreen
import com.example.ui.OceanBackdrop
import com.example.ui.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalSharedTransitionApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Configure Firebase App Check with launch intent before any UI or ViewModels
    configureAppCheck(this, intent)

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
              targetState = uiState.stage,
              transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith
                  fadeOut(animationSpec = tween(durationMillis = 220))
              },
              label = "app_stage_transition",
            ) { stage ->
              when (stage) {
                AppStage.WELCOME ->
                  WelcomeScreen(
                    formattedTime = uiState.formattedTime,
                    formattedDate = uiState.formattedDate,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                    onEnterClick = viewModel::openPinScreen,
                  )
                AppStage.PIN_LOCK ->
                  CartoonPinScreen(
                    uiState = uiState,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                    onDigitPressed = viewModel::onPinDigitPressed,
                    onBackspace = viewModel::onPinBackspace,
                    onResetPin = viewModel::resetPinToDefault,
                    onBackToWelcome = viewModel::returnToWelcome,
                  )
                AppStage.HOME ->
                  HomeScreen(
                    uiState = uiState,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                    onSelectTab = viewModel::selectTab,
                    onSelectVibe = viewModel::selectVibe,
                    onSendGoodVibes = viewModel::sendGoodVibes,
                    onToggleBreathing = viewModel::toggleBreathing,
                    onSelectCause = viewModel::selectDonationCause,
                    onSelectAmount = viewModel::selectDonationAmount,
                    onSelectPayment = viewModel::selectPaymentMethod,
                    onUpdateDonorNote = viewModel::updateDonorNote,
                    onToggleAiBlessing = viewModel::toggleIncludeAiBlessing,
                    onSubmitDonation = viewModel::submitCartoonDonation,
                    onStartAnotherDonation = viewModel::startAnotherDonation,
                    onSendAiPrompt = viewModel::sendAiPrompt,
                    onDismissAiError = viewModel::dismissAiError,
                    onClearAiChat = viewModel::clearAiChat,
                    onUpdateMotto = viewModel::updateMotto,
                    onUpdatePin = viewModel::updateCustomPin,
                    onResetPin = viewModel::resetPinToDefault,
                    onLockToPin = viewModel::lockToPinScreen,
                    onReturnToWelcome = viewModel::returnToWelcome,
                  )
              }
            }
          }
        }
      }
    }
  }
}
