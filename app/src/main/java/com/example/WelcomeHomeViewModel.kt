package com.example

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class GreetingPeriod(
  @param:StringRes val titleRes: Int,
  @param:StringRes val bodyRes: Int,
) {
  MORNING(R.string.greeting_morning_title, R.string.greeting_morning_body),
  AFTERNOON(R.string.greeting_afternoon_title, R.string.greeting_afternoon_body),
  EVENING(R.string.greeting_evening_title, R.string.greeting_evening_body),
  NIGHT(R.string.greeting_night_title, R.string.greeting_night_body),
}

data class WelcomeHomeUiState(
  val isHomeEntered: Boolean = false,
  val greetingPeriod: GreetingPeriod = GreetingPeriod.EVENING,
  val formattedDateTime: String = "",
)

class WelcomeHomeViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(buildInitialState())
  val uiState: StateFlow<WelcomeHomeUiState> = _uiState.asStateFlow()

  fun enterHome() {
    val (period, clockLabel) = computeGreetingInfo()
    _uiState.update {
      it.copy(
        isHomeEntered = true,
        greetingPeriod = period,
        formattedDateTime = clockLabel,
      )
    }
  }

  fun returnToWelcome() {
    _uiState.update { it.copy(isHomeEntered = false) }
  }

  private fun buildInitialState(): WelcomeHomeUiState {
    val (period, clockLabel) = computeGreetingInfo()
    return WelcomeHomeUiState(
      isHomeEntered = false,
      greetingPeriod = period,
      formattedDateTime = clockLabel,
    )
  }

  private fun computeGreetingInfo(): Pair<GreetingPeriod, String> {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val period =
      when (hour) {
        in 5..11 -> GreetingPeriod.MORNING
        in 12..16 -> GreetingPeriod.AFTERNOON
        in 17..20 -> GreetingPeriod.EVENING
        else -> GreetingPeriod.NIGHT
      }
    val formatter = SimpleDateFormat("EEE • h:mm a", Locale.getDefault())
    return period to formatter.format(Date(calendar.timeInMillis))
  }
}
