package com.example

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppPreferences
import com.example.data.UserPrefsSnapshot
import com.example.data.VahaiAiRepository
import com.google.firebase.ai.type.content
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "WelcomeHomeViewModel"

enum class AppStage {
  WELCOME,
  PIN_LOCK,
  HOME,
}

enum class HomeTab {
  HOME,
  GIVE,
  AI,
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

enum class DonationCause(
  val title: String,
  val subtitle: String,
  val badge: String,
) {
  ISLAND_YOUTH(
    title = "Island Youth & Sports",
    subtitle = "Gear & training for young Pacific athletes",
    badge = "YOUTH",
  ),
  OCEAN_REEF(
    title = "Ocean & Reef Care",
    subtitle = "Protecting coral reefs & clean island tides",
    badge = "OCEAN",
  ),
  COMMUNITY_BOX(
    title = "Community Blessing Box",
    subtitle = "Meals, school supplies & family uplift",
    badge = "COMMUNITY",
  ),
  FAITH_CHOIR(
    title = "Faith & Choir Fund",
    subtitle = "Music instruments & youth fellowship",
    badge = "FAITH",
  ),
}

enum class DonationPaymentMethod(val label: String, val detail: String) {
  VAHAI_PAY("Vahai Pay", "Instant Demo Tap"),
  ISLAND_CARD("Island Card", "•••• 4242"),
  OCEAN_WALLET("Ocean Wallet", "Demo Vault"),
}

enum class DonationStage {
  COMPOSING,
  PROCESSING,
  RECEIPT,
}

sealed interface AiBlessingState {
  data object Idle : AiBlessingState
  data object Loading : AiBlessingState
  data class Success(val text: String) : AiBlessingState
  data class Error(val message: String) : AiBlessingState
}

data class DonationReceipt(
  val txId: String,
  val cause: DonationCause,
  val amountDollars: Int,
  val paymentMethod: DonationPaymentMethod,
  val donorNote: String,
  val timestampLabel: String,
  val aiBlessingState: AiBlessingState = AiBlessingState.Idle,
)

data class AiChatMessage(
  val id: String,
  val isUser: Boolean,
  val text: String,
)

sealed interface AiChatUiState {
  data object Idle : AiChatUiState
  data object Loading : AiChatUiState
  data class Streaming(val partialText: String) : AiChatUiState
  data class Error(val message: String) : AiChatUiState
}

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
  val totalDonatedDollars: Int = 0,
  val donationCount: Int = 0,
  val isBreathingActive: Boolean = false,
  // Cartoon Donation State
  val donationStage: DonationStage = DonationStage.COMPOSING,
  val processingStepText: String = "Opening Demo Vault...",
  val selectedCause: DonationCause = DonationCause.ISLAND_YOUTH,
  val selectedAmount: Int = 25,
  val selectedPaymentMethod: DonationPaymentMethod = DonationPaymentMethod.VAHAI_PAY,
  val donorNote: String = "",
  val includeAiBlessing: Boolean = true,
  val activeReceipt: DonationReceipt? = null,
  val recentDonations: List<DonationReceipt> = emptyList(),
  // Gemini AI Chat State
  val aiMessages: List<AiChatMessage> =
    listOf(
      AiChatMessage(
        id = "welcome_ai",
        isUser = false,
        text =
          "Mālō e lelei, Sipiliano! I'm Vahai Spirit AI — ask me anything, get a daily hype boost, or craft an island blessing!",
      ),
    ),
  val aiChatState: AiChatUiState = AiChatUiState.Idle,
) {
  val isUsingDefaultPin: Boolean
    get() = savedPin == UserPrefsSnapshot.DEFAULT_PIN

  val communityGoalRaised: Int
    get() = 1240 + totalDonatedDollars

  val communityGoalTarget: Int
    get() = 2000

  val donorRank: String
    get() =
      when {
        totalDonatedDollars >= 250 -> "Royal Patron"
        totalDonatedDollars >= 100 -> "Crown Champion"
        totalDonatedDollars >= 25 -> "Island Supporter"
        else -> "Rising Wave"
      }
}

val VIBE_OPTIONS = listOf("Energized", "Grateful", "Focused", "Blessed")
val DONATION_AMOUNTS = listOf(5, 15, 25, 50, 100)

class WelcomeHomeViewModel(application: Application) : AndroidViewModel(application) {

  private val preferences = AppPreferences(application.applicationContext)
  private val aiRepository = VahaiAiRepository()

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
            totalDonatedDollars = snapshot.totalDonatedDollars,
            donationCount = snapshot.donationCount,
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

  // --- Cartoon Donation Actions ---

  fun selectDonationCause(cause: DonationCause) {
    _uiState.update { it.copy(selectedCause = cause) }
  }

  fun selectDonationAmount(amount: Int) {
    _uiState.update { it.copy(selectedAmount = amount.coerceIn(1, 500)) }
  }

  fun selectPaymentMethod(method: DonationPaymentMethod) {
    _uiState.update { it.copy(selectedPaymentMethod = method) }
  }

  fun updateDonorNote(note: String) {
    _uiState.update { it.copy(donorNote = note.take(100)) }
  }

  fun toggleIncludeAiBlessing() {
    _uiState.update { it.copy(includeAiBlessing = !it.includeAiBlessing) }
  }

  fun submitCartoonDonation() {
    val current = _uiState.value
    if (current.donationStage == DonationStage.PROCESSING) return

    val cause = current.selectedCause
    val amount = current.selectedAmount
    val payment = current.selectedPaymentMethod
    val note = current.donorNote.trim()
    val wantAi = current.includeAiBlessing

    _uiState.update {
      it.copy(
        donationStage = DonationStage.PROCESSING,
        processingStepText = "Opening Demo Vault...",
      )
    }

    viewModelScope.launch {
      delay(420L)
      _uiState.update { it.copy(processingStepText = "Verifying ${payment.label}...") }
      delay(480L)
      _uiState.update { it.copy(processingStepText = "Sealing Island Gift...") }
      delay(450L)

      val txCode = "VAH-${Random.nextInt(100000, 999999)}"
      val timeNow = SimpleDateFormat("MMM d • h:mm a", Locale.getDefault()).format(Date())
      val receipt =
        DonationReceipt(
          txId = txCode,
          cause = cause,
          amountDollars = amount,
          paymentMethod = payment,
          donorNote = note,
          timestampLabel = timeNow,
          aiBlessingState = if (wantAi) AiBlessingState.Loading else AiBlessingState.Idle,
        )

      preferences.recordMockDonation(amount)
      preferences.incrementGoodVibes()

      _uiState.update { state ->
        state.copy(
          donationStage = DonationStage.RECEIPT,
          activeReceipt = receipt,
          recentDonations = (listOf(receipt) + state.recentDonations).take(5),
          donorNote = "",
        )
      }

      if (wantAi) {
        val result = aiRepository.generateDonationBlessing(cause.title, amount, note)
        val blessingState =
          result.fold(
            onSuccess = { AiBlessingState.Success(it) },
            onFailure = { AiBlessingState.Error(it.message ?: "AI blessing unavailable.") },
          )
        _uiState.update { state ->
          val updatedActive =
            if (state.activeReceipt?.txId == txCode) {
              state.activeReceipt.copy(aiBlessingState = blessingState)
            } else state.activeReceipt
          val updatedHistory =
            state.recentDonations.map { item ->
              if (item.txId == txCode) item.copy(aiBlessingState = blessingState) else item
            }
          state.copy(activeReceipt = updatedActive, recentDonations = updatedHistory)
        }
      }
    }
  }

  fun startAnotherDonation() {
    _uiState.update {
      it.copy(
        donationStage = DonationStage.COMPOSING,
        activeReceipt = null,
      )
    }
  }

  // --- Gemini AI Chat Actions ---

  fun sendAiPrompt(promptText: String) {
    val cleanPrompt = promptText.trim()
    if (cleanPrompt.isEmpty()) return
    val currentState = _uiState.value
    if (currentState.aiChatState is AiChatUiState.Loading ||
      currentState.aiChatState is AiChatUiState.Streaming
    ) {
      return
    }

    val userMsg =
      AiChatMessage(
        id = "u_${System.currentTimeMillis()}",
        isUser = true,
        text = cleanPrompt,
      )

    val historyContent =
      currentState.aiMessages.drop(1).map { msg ->
        content(if (msg.isUser) "user" else "model") { text(msg.text) }
      }

    _uiState.update {
      it.copy(
        aiMessages = it.aiMessages + userMsg,
        aiChatState = AiChatUiState.Loading,
      )
    }

    viewModelScope.launch {
      var latestText = ""
      try {
        aiRepository.streamChatReply(historyContent, cleanPrompt).collect { partial ->
          latestText = partial
          _uiState.update { it.copy(aiChatState = AiChatUiState.Streaming(partial)) }
        }
        val aiMsg =
          AiChatMessage(
            id = "a_${System.currentTimeMillis()}",
            isUser = false,
            text = latestText,
          )
        _uiState.update {
          it.copy(
            aiMessages = it.aiMessages + aiMsg,
            aiChatState = AiChatUiState.Idle,
          )
        }
      } catch (e: TimeoutCancellationException) {
        Log.e(TAG, "AI chat timed out", e)
        _uiState.update {
          it.copy(aiChatState = AiChatUiState.Error("Gemini timed out after 60s. Please try again."))
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e(TAG, "AI chat failed", e)
        _uiState.update {
          it.copy(aiChatState = AiChatUiState.Error(e.localizedMessage ?: "AI request failed."))
        }
      }
    }
  }

  fun dismissAiError() {
    _uiState.update { it.copy(aiChatState = AiChatUiState.Idle) }
  }

  fun clearAiChat() {
    _uiState.update {
      it.copy(
        aiMessages =
          listOf(
            AiChatMessage(
              id = "welcome_ai",
              isUser = false,
              text =
                "Mālō e lelei, Sipiliano! Fresh tide, fresh chat — what would you like to create or explore?",
            ),
          ),
        aiChatState = AiChatUiState.Idle,
      )
    }
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
