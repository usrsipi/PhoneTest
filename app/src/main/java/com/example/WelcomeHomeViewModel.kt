package com.example

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppPreferences
import com.example.data.UserPrefsSnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppStage {
  WELCOME,
  PIN_LOCK,
  HOME,
}

enum class HomeTab {
  HOME,
  INSPIRE,
  PROFILE,
}

enum class GreetingPeriod(
  @param:StringRes val titleRes: Int,
  @param:StringRes val bodyRes: Int,
) {
  MORNING(R.string.greeting_morning_title, R.string.greeting_morning_body),
  AFTERNOON(R.string.greeting_afternoon_title, R.string.greeting_afternoon_body),
  EVENING(R.string.greeting_evening_title, R.string.greeting_evening_body),
  NIGHT(R.string.greeting_night_title, R.string.greeting_night_body),
}

enum class InspireCategory(val label: String) {
  ALL("All"),
  FAITH("Faith"),
  STRENGTH("Strength"),
  ISLAND("Island Spirit"),
}

data class InspireQuote(
  val id: String,
  val text: String,
  val author: String,
  val category: InspireCategory,
)

data class AppUiState(
  val stage: AppStage = AppStage.WELCOME,
  val activeTab: HomeTab = HomeTab.HOME,
  val greetingPeriod: GreetingPeriod = GreetingPeriod.EVENING,
  val formattedTime: String = "",
  val formattedDate: String = "",
  val dayProgress: Float = 0.5f,
  val enteredPin: String = "",
  val savedPin: String = UserPrefsSnapshot.DEFAULT_PIN,
  val isPinError: Boolean = false,
  val isPinSuccess: Boolean = false,
  val pinErrorNonce: Int = 0,
  val selectedVibe: String = "Energized",
  val customMotto: String = "Standing strong with faith, pride & island spirit.",
  val unlockCount: Int = 0,
  val goodVibesCount: Int = 7,
  val favoriteQuoteIds: Set<String> = setOf("q1"),
  val selectedCategory: InspireCategory = InspireCategory.ALL,
  val currentQuoteIndex: Int = 0,
  val isBreathingActive: Boolean = false,
) {
  val isUsingDefaultPin: Boolean
    get() = savedPin == UserPrefsSnapshot.DEFAULT_PIN
}

val VIBE_OPTIONS = listOf("Energized", "Grateful", "Focused", "Chill")

val INSPIRE_QUOTES =
  listOf(
    InspireQuote(
      id = "q1",
      text = "Walk with faith in your heart, strength in your shoulders, and gratitude in every step.",
      author = "Sipiliano Vahai",
      category = InspireCategory.FAITH,
    ),
    InspireQuote(
      id = "q2",
      text = "Mālō e lelei — greet each tide with respect, courage, and an open spirit.",
      author = "Polynesian Wisdom",
      category = InspireCategory.ISLAND,
    ),
    InspireQuote(
      id = "q3",
      text = "Like the deep ocean, stay calm on the surface and unstoppable beneath.",
      author = "Island Proverb",
      category = InspireCategory.STRENGTH,
    ),
    InspireQuote(
      id = "q4",
      text = "With God beside you and purpose ahead of you, no wave is too tall to rise above.",
      author = "Daily Blessing",
      category = InspireCategory.FAITH,
    ),
    InspireQuote(
      id = "q5",
      text = "True leadership is lifting others with warmth while standing rooted in who you are.",
      author = "‘Ofa Atu",
      category = InspireCategory.ISLAND,
    ),
    InspireQuote(
      id = "q6",
      text = "Small consistent wins every day build an unshakeable crown.",
      author = "Daily Focus",
      category = InspireCategory.STRENGTH,
    ),
  )

class WelcomeHomeViewModel(application: Application) : AndroidViewModel(application) {

  private val preferences = AppPreferences(application.applicationContext)
  private val _uiState = MutableStateFlow(AppUiState())
  val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

  init {
    refreshClock()
    viewModelScope.launch {
      preferences.prefsFlow.collect { snapshot ->
        _uiState.update { state ->
          state.copy(
            savedPin = snapshot.pinCode,
            selectedVibe = snapshot.selectedVibe,
            customMotto = snapshot.customMotto,
            unlockCount = snapshot.unlockCount,
            goodVibesCount = snapshot.goodVibesCount,
            favoriteQuoteIds = snapshot.favoriteQuoteIds,
          )
        }
      }
    }
    viewModelScope.launch {
      while (isActive) {
        refreshClock()
        delay(15_000L)
      }
    }
  }

  fun openPinScreen() {
    refreshClock()
    _uiState.update {
      it.copy(
        stage = AppStage.PIN_LOCK,
        enteredPin = "",
        isPinError = false,
        isPinSuccess = false,
      )
    }
  }

  fun returnToWelcome() {
    _uiState.update {
      it.copy(
        stage = AppStage.WELCOME,
        enteredPin = "",
        isPinError = false,
        isPinSuccess = false,
        isBreathingActive = false,
      )
    }
  }

  fun lockToPinScreen() {
    _uiState.update {
      it.copy(
        stage = AppStage.PIN_LOCK,
        enteredPin = "",
        isPinError = false,
        isPinSuccess = false,
        isBreathingActive = false,
      )
    }
  }

  fun onPinDigitPressed(digit: Char) {
    val current = _uiState.value
    if (current.isPinSuccess || current.enteredPin.length >= 4) return

    val updatedPin = current.enteredPin + digit
    _uiState.update { it.copy(enteredPin = updatedPin, isPinError = false) }

    if (updatedPin.length == 4) {
      if (updatedPin == current.savedPin) {
        _uiState.update { it.copy(isPinSuccess = true, isPinError = false) }
        viewModelScope.launch {
          preferences.incrementUnlockCount()
          delay(650L)
          refreshClock()
          _uiState.update {
            it.copy(
              stage = AppStage.HOME,
              activeTab = HomeTab.HOME,
              enteredPin = "",
              isPinSuccess = false,
            )
          }
        }
      } else {
        _uiState.update {
          it.copy(
            isPinError = true,
            pinErrorNonce = it.pinErrorNonce + 1,
          )
        }
        viewModelScope.launch {
          delay(550L)
          _uiState.update { it.copy(enteredPin = "") }
        }
      }
    }
  }

  fun onPinBackspace() {
    val current = _uiState.value
    if (current.isPinSuccess || current.enteredPin.isEmpty()) return
    _uiState.update {
      it.copy(enteredPin = it.enteredPin.dropLast(1), isPinError = false)
    }
  }

  fun resetPinToDefault() {
    viewModelScope.launch {
      preferences.resetPinToDefault()
      _uiState.update { it.copy(enteredPin = "", isPinError = false) }
    }
  }

  fun selectTab(tab: HomeTab) {
    _uiState.update { it.copy(activeTab = tab) }
  }

  fun selectVibe(vibe: String) {
    viewModelScope.launch { preferences.updateVibe(vibe) }
  }

  fun sendGoodVibes() {
    viewModelScope.launch { preferences.incrementGoodVibes() }
  }

  fun toggleBreathing() {
    _uiState.update { it.copy(isBreathingActive = !it.isBreathingActive) }
  }

  fun selectInspireCategory(category: InspireCategory) {
    _uiState.update { it.copy(selectedCategory = category, currentQuoteIndex = 0) }
  }

  fun nextQuote() {
    val filtered = filteredQuotes(_uiState.value.selectedCategory)
    if (filtered.isEmpty()) return
    _uiState.update {
      it.copy(currentQuoteIndex = (it.currentQuoteIndex + 1) % filtered.size)
    }
  }

  fun toggleFavoriteQuote(quoteId: String) {
    viewModelScope.launch { preferences.toggleFavoriteQuote(quoteId) }
  }

  fun updateMotto(newMotto: String) {
    val trimmed = newMotto.trim()
    if (trimmed.isNotEmpty()) {
      viewModelScope.launch { preferences.updateMotto(trimmed) }
    }
  }

  fun updateCustomPin(newPin: String): Boolean {
    if (newPin.length == 4 && newPin.all { it.isDigit() }) {
      viewModelScope.launch { preferences.updatePin(newPin) }
      return true
    }
    return false
  }

  fun filteredQuotes(category: InspireCategory): List<InspireQuote> {
    return if (category == InspireCategory.ALL) INSPIRE_QUOTES
    else INSPIRE_QUOTES.filter { it.category == category }
  }

  private fun refreshClock() {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val period =
      when (hour) {
        in 5..11 -> GreetingPeriod.MORNING
        in 12..16 -> GreetingPeriod.AFTERNOON
        in 17..20 -> GreetingPeriod.EVENING
        else -> GreetingPeriod.NIGHT
      }
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    val dateFormatter = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val now = Date(calendar.timeInMillis)
    val progress = ((hour * 60 + minute) / 1440f).coerceIn(0.02f, 1f)

    _uiState.update {
      it.copy(
        greetingPeriod = period,
        formattedTime = timeFormatter.format(now),
        formattedDate = dateFormatter.format(now),
        dayProgress = progress,
      )
    }
  }
}
