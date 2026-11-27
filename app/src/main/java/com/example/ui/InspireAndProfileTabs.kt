package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.AppUiState
import com.example.InspireCategory
import com.example.InspireQuote
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InspireTabContent(
  uiState: AppUiState,
  quotes: List<InspireQuote>,
  onSelectCategory: (InspireCategory) -> Unit,
  onNextQuote: () -> Unit,
  onToggleFavorite: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val currentQuote =
    quotes.getOrElse(uiState.currentQuoteIndex.coerceIn(0, (quotes.size - 1).coerceAtLeast(0))) {
      quotes.first()
    }
  val isFavorite = currentQuote.id in uiState.favoriteQuoteIds

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      InspireCategory.entries.forEach { cat ->
        val selected = uiState.selectedCategory == cat
        val bg by
          animateColorAsState(
            targetValue = if (selected) RoyalGold else OceanElevated,
            label = "cat_bg",
          )
        val fg by
          animateColorAsState(
            targetValue = if (selected) OceanAbyss else IceWhite,
            label = "cat_fg",
          )
        Surface(
          shape = RoundedCornerShape(50),
          color = bg,
          border = BorderStroke(1.dp, if (selected) RoyalGold else OceanRim),
          modifier =
            Modifier
              .clip(RoundedCornerShape(50))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelectCategory(cat)
              },
        ) {
          Text(
            text = cat.label,
            style = MaterialTheme.typography.labelLarge,
            color = fg,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          )
        }
      }
    }

    Surface(
      modifier = Modifier.fillMaxWidth().testTag("inspire_quote_card"),
      shape = RoundedCornerShape(28.dp),
      color = OceanCardGlass,
      border =
        BorderStroke(
          1.2.dp,
          Brush.linearGradient(listOf(RoyalGold.copy(alpha = 0.55f), OceanRim, BiolumCyan.copy(alpha = 0.5f))),
        ),
    ) {
      Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            shape = RoundedCornerShape(50),
            color = RoyalGold.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.45f)),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = RoyalGold,
                modifier = Modifier.size(16.dp),
              )
              Text(
                text = currentQuote.category.label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = RoyalGold,
              )
            }
          }

          IconButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onToggleFavorite(currentQuote.id)
            },
            modifier = Modifier.size(48.dp).testTag("favorite_quote_button"),
          ) {
            Icon(
              imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
              contentDescription = "Save quote",
              tint = if (isFavorite) HibiscusCoral else MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedContent(targetState = currentQuote, label = "quote_crossfade") { quote ->
          Column {
            Text(
              text = "\"${quote.text}\"",
              style = MaterialTheme.typography.headlineMedium,
              color = IceWhite,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "— ${quote.author}",
              style = MaterialTheme.typography.titleMedium,
              color = SeafoamTeal,
            )
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onNextQuote()
          },
          modifier = Modifier.fillMaxWidth().height(52.dp).testTag("next_quote_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BiolumCyan, contentColor = OceanAbyss),
        ) {
          Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Next Blessing", style = MaterialTheme.typography.titleMedium)
        }
      }
    }

    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      color = OceanElevated.copy(alpha = 0.65f),
      border = BorderStroke(1.dp, OceanRim),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "Saved Blessings",
            style = MaterialTheme.typography.titleMedium,
            color = IceWhite,
          )
          Text(
            text = "${uiState.favoriteQuoteIds.size} of ${quotes.size} in this view",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        Icon(
          imageVector = Icons.Rounded.Favorite,
          contentDescription = null,
          tint = HibiscusCoral,
          modifier = Modifier.size(24.dp),
        )
      }
    }
  }
}

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
    // Full Portrait Showcase Card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(28.dp),
      color = OceanCardGlass,
      border =
        BorderStroke(
          1.2.dp,
          Brush.linearGradient(listOf(RoyalGold.copy(alpha = 0.6f), BiolumCyan.copy(alpha = 0.5f))),
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
              .size(168.dp)
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
          text = "Current Vibe: ${uiState.selectedVibe} • ${uiState.unlockCount} Unlocks",
          style = MaterialTheme.typography.bodyMedium,
          color = SeafoamTeal,
        )
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
