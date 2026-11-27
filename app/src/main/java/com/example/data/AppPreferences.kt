package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vahai_prefs")

data class UserPrefsSnapshot(
  val pinCode: String = DEFAULT_PIN,
  val selectedVibe: String = "Energized",
  val customMotto: String = "Standing strong with faith, pride & island spirit.",
  val unlockCount: Int = 0,
  val goodVibesCount: Int = 7,
  val totalDonatedDollars: Int = 0,
  val donationCount: Int = 0,
) {
  companion object {
    const val DEFAULT_PIN = "1234"
  }
}

class AppPreferences(private val context: Context) {

  private object Keys {
    val PIN_CODE = stringPreferencesKey("pin_code")
    val SELECTED_VIBE = stringPreferencesKey("selected_vibe")
    val CUSTOM_MOTTO = stringPreferencesKey("custom_motto")
    val UNLOCK_COUNT = intPreferencesKey("unlock_count")
    val GOOD_VIBES_COUNT = intPreferencesKey("good_vibes_count")
    val TOTAL_DONATED_DOLLARS = intPreferencesKey("total_donated_dollars")
    val DONATION_COUNT = intPreferencesKey("donation_count")
  }

  val prefsFlow: Flow<UserPrefsSnapshot> =
    context.dataStore.data.map { prefs ->
      UserPrefsSnapshot(
        pinCode = prefs[Keys.PIN_CODE] ?: UserPrefsSnapshot.DEFAULT_PIN,
        selectedVibe = prefs[Keys.SELECTED_VIBE] ?: "Energized",
        customMotto =
          prefs[Keys.CUSTOM_MOTTO] ?: "Standing strong with faith, pride & island spirit.",
        unlockCount = prefs[Keys.UNLOCK_COUNT] ?: 0,
        goodVibesCount = prefs[Keys.GOOD_VIBES_COUNT] ?: 7,
        totalDonatedDollars = prefs[Keys.TOTAL_DONATED_DOLLARS] ?: 0,
        donationCount = prefs[Keys.DONATION_COUNT] ?: 0,
      )
    }

  suspend fun incrementUnlockCount() {
    context.dataStore.edit { prefs ->
      prefs[Keys.UNLOCK_COUNT] = (prefs[Keys.UNLOCK_COUNT] ?: 0) + 1
    }
  }

  suspend fun recordMockDonation(amountDollars: Int) {
    context.dataStore.edit { prefs ->
      prefs[Keys.TOTAL_DONATED_DOLLARS] = (prefs[Keys.TOTAL_DONATED_DOLLARS] ?: 0) + amountDollars
      prefs[Keys.DONATION_COUNT] = (prefs[Keys.DONATION_COUNT] ?: 0) + 1
    }
  }

  suspend fun updatePin(newPin: String) {
    if (newPin.length == 4 && newPin.all { it.isDigit() }) {
      context.dataStore.edit { prefs -> prefs[Keys.PIN_CODE] = newPin }
    }
  }

  suspend fun resetPinToDefault() {
    context.dataStore.edit { prefs -> prefs[Keys.PIN_CODE] = UserPrefsSnapshot.DEFAULT_PIN }
  }

  suspend fun updateVibe(vibe: String) {
    context.dataStore.edit { prefs -> prefs[Keys.SELECTED_VIBE] = vibe }
  }

  suspend fun updateMotto(motto: String) {
    context.dataStore.edit { prefs -> prefs[Keys.CUSTOM_MOTTO] = motto }
  }

  suspend fun incrementGoodVibes() {
    context.dataStore.edit { prefs ->
      prefs[Keys.GOOD_VIBES_COUNT] = (prefs[Keys.GOOD_VIBES_COUNT] ?: 7) + 1
    }
  }
}
