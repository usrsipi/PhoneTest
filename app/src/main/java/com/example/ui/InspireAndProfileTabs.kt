package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.AppUiState
import com.example.R
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.HibiscusCoral
import com.example.ui.theme.IceWhite
import com.example.ui.theme.OceanAbyss
import com.example.ui.theme.OceanCardGlass
import com.example.ui.theme.OceanElevated
import com.example.ui.theme.OceanRim
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.SeafoamTeal

@Composable
fun ProfileTabContent(
  uiState: AppUiState,
  onUpdateMotto: (String) -> Unit,
  onUpdatePin: (String) -> Boolean,
  onResetPin: () -> Unit,
  onLockApp: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var mottoInput by remember(uiState.customMotto) { mutableStateOf(uiState.customMotto) }
  var newPinInput by remember { mutableStateOf("") }
  var pinFeedback by remember { mutableStateOf<String?>(null) }
  var mottoSaved by remember { mutableStateOf(false) }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // Portrait Showcase & Impact Stats Card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(28.dp),
      color = OceanCardGlass,
      border =
        BorderStroke(
          1.5.dp,
          Brush.linearGradient(listOf(RoyalGold.copy(alpha = 0.65f), BiolumCyan.copy(alpha = 0.55f))),
        ),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Image(
          painter = painterResource(id = R.drawable.vahai_portrait),
          contentDescription = stringResource(R.string.portrait_cd),
          contentScale = ContentScale.Crop,
          modifier =
            Modifier
              .size(164.dp)
              .clip(RoundedCornerShape(28.dp))
              .border(
                width = 2.5.dp,
                brush = Brush.linearGradient(listOf(RoyalGold, BiolumCyan, SeafoamTeal)),
                shape = RoundedCornerShape(28.dp),
              ),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = stringResource(R.string.user_full_name),
          style = MaterialTheme.typography.headlineMedium,
          color = IceWhite,
        )
        Text(
          text = "${uiState.donorRank} • Vibe: ${uiState.selectedVibe}",
          style = MaterialTheme.typography.bodyMedium,
          color = SeafoamTeal,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          ProfileStatChip(
            label = "Demo Given",
            value = "$${uiState.totalDonatedDollars}",
            modifier = Modifier.weight(1f),
          )
          ProfileStatChip(
            label = "Gifts",
            value = "${uiState.donationCount}",
            modifier = Modifier.weight(1f),
          )
          ProfileStatChip(
            label = "Unlocks",
            value = "${uiState.unlockCount}",
            modifier = Modifier.weight(1f),
          )
        }
      }
    }

    // Personal Motto Editor
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = OceanElevated.copy(alpha = 0.78f),
      border = BorderStroke(1.dp, OceanRim),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Text(
          text = "Personal Motto",
          style = MaterialTheme.typography.titleMedium,
          color = IceWhite,
        )
        OutlinedTextField(
          value = mottoInput,
          onValueChange = {
            mottoInput = it
            mottoSaved = false
          },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = BiolumCyan,
              unfocusedBorderColor = OceanRim,
              focusedTextColor = IceWhite,
              unfocusedTextColor = IceWhite,
            ),
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (mottoSaved) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = SeafoamTeal,
                modifier = Modifier.size(18.dp),
              )
              Text(text = "Saved!", style = MaterialTheme.typography.labelLarge, color = SeafoamTeal)
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }
          Button(
            onClick = {
              onUpdateMotto(mottoInput)
              mottoSaved = true
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BiolumCyan, contentColor = OceanAbyss),
          ) {
            Text(text = "Save Motto", style = MaterialTheme.typography.labelLarge)
          }
        }
      }
    }

    // Cartoon PIN Customizer
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = OceanElevated.copy(alpha = 0.78f),
      border = BorderStroke(1.dp, OceanRim),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Text(
          text = "Customize Cartoon PIN",
          style = MaterialTheme.typography.titleMedium,
          color = IceWhite,
        )
        OutlinedTextField(
          value = newPinInput,
          onValueChange = { input ->
            if (input.length <= 4 && input.all { it.isDigit() }) {
              newPinInput = input
              pinFeedback = null
            }
          },
          label = { Text("Enter new 4-digit PIN") },
          singleLine = true,
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          modifier = Modifier.fillMaxWidth().testTag("custom_pin_input"),
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RoyalGold,
              unfocusedBorderColor = OceanRim,
              focusedTextColor = IceWhite,
              unfocusedTextColor = IceWhite,
              focusedLabelColor = RoyalGold,
              unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        pinFeedback?.let { feedback ->
          Text(text = feedback, style = MaterialTheme.typography.labelMedium, color = SeafoamTeal)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedButton(
            onClick = {
              onResetPin()
              newPinInput = ""
              pinFeedback = "Reset to starter PIN 1234"
            },
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, OceanRim),
          ) {
            Text(text = "Reset 1234", color = IceWhite)
          }
          Button(
            onClick = {
              if (onUpdatePin(newPinInput)) {
                pinFeedback = "New 4-digit PIN saved!"
                newPinInput = ""
              } else {
                pinFeedback = "Please enter 4 digits"
              }
            },
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = OceanAbyss),
          ) {
            Text(text = "Update PIN", style = MaterialTheme.typography.labelLarge)
          }
        }
      }
    }

    Button(
      onClick = onLockApp,
      modifier = Modifier.fillMaxWidth().height(54.dp).testTag("test_pin_lock_button"),
      shape = RoundedCornerShape(18.dp),
      colors = ButtonDefaults.buttonColors(containerColor = HibiscusCoral, contentColor = OceanAbyss),
    ) {
      Icon(imageVector = Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(text = "Test Cartoon PIN Lock Now", style = MaterialTheme.typography.titleMedium)
    }
  }
}

@Composable
private fun ProfileStatChip(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    color = OceanElevated,
    border = BorderStroke(1.dp, OceanRim),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(text = value, style = MaterialTheme.typography.titleLarge, color = RoyalGold)
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
