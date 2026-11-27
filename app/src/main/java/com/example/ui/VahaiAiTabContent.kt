package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AiChatUiState
import com.example.AppUiState
import com.example.R
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.CartoonInk
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
fun VahaiAiTabContent(
  uiState: AppUiState,
  onSendPrompt: (String) -> Unit,
  onDismissError: () -> Unit,
  onClearChat: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var inputText by remember { mutableStateOf("") }
  val isBusy =
    uiState.aiChatState is AiChatUiState.Loading || uiState.aiChatState is AiChatUiState.Streaming

  val quickPrompts =
    listOf(
      "Hype Me Up" to "Give Sipiliano Vahai an energetic, uplifting 3-sentence motivational hype boost for today!",
      "Island Blessing" to "Share a warm Polynesian-inspired daily blessing about strength, faith, and calm waters.",
      "Daily Quest" to "Invent a fun, positive 5-minute daily challenge for Sipiliano to do today.",
      "Cartoon Joke" to "Tell a clean, funny island-ocean themed joke that brings instant smiles.",
    )

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    // AI Companion Header Card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = OceanCardGlass,
      border =
        BorderStroke(
          1.5.dp,
          Brush.linearGradient(listOf(RoyalGold.copy(alpha = 0.6f), BiolumCyan.copy(alpha = 0.6f))),
        ),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Image(
            painter = painterResource(id = R.drawable.vahai_portrait),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
              Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, RoyalGold, CircleShape),
          )
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Text(
                text = "Vahai Spirit AI",
                style = MaterialTheme.typography.titleLarge,
                color = IceWhite,
              )
              Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = RoyalGold,
                modifier = Modifier.size(18.dp),
              )
            }
            Text(
              text = "Powered by Gemini • Live Streaming",
              style = MaterialTheme.typography.labelMedium,
              color = SeafoamTeal,
            )
          }
        }

        IconButton(
          onClick = onClearChat,
          modifier = Modifier.size(48.dp).testTag("clear_ai_chat_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.DeleteSweep,
            contentDescription = "Clear chat",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }

    // 1-Tap Fun Quick Prompt Chips
    FlowRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      quickPrompts.forEach { (label, prompt) ->
        Surface(
          shape = RoundedCornerShape(50),
          color = OceanElevated,
          border = BorderStroke(1.5.dp, BiolumCyan.copy(alpha = 0.5f)),
          modifier =
            Modifier
              .clip(RoundedCornerShape(50))
              .clickable(enabled = !isBusy) { onSendPrompt(prompt) }
              .testTag("ai_quick_${label.lowercase().replace(" ", "_")}"),
        ) {
          Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = BiolumCyan,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          )
        }
      }
    }

    // Chat Messages Stream
    Column(
      modifier = Modifier.fillMaxWidth().testTag("ai_chat_list"),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      uiState.aiMessages.forEach { message ->
        AiChatBubble(text = message.text, isUser = message.isUser)
      }

      when (val state = uiState.aiChatState) {
        is AiChatUiState.Loading -> {
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = OceanElevated,
            border = BorderStroke(1.dp, OceanRim),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              CircularProgressIndicator(
                color = RoyalGold,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp),
              )
              Text(
                text = "Vahai AI is thinking...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
        is AiChatUiState.Streaming -> {
          AiChatBubble(text = state.partialText, isUser = false)
        }
        is AiChatUiState.Error -> {
          Surface(
            modifier = Modifier.fillMaxWidth().testTag("ai_error_banner"),
            shape = RoundedCornerShape(18.dp),
            color = HibiscusCoral.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, HibiscusCoral),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.ErrorOutline,
                  contentDescription = null,
                  tint = HibiscusCoral,
                )
                Text(
                  text = state.message,
                  style = MaterialTheme.typography.bodyMedium,
                  color = IceWhite,
                )
              }
              TextButton(onClick = onDismissError) {
                Text(text = "Dismiss", color = RoyalGold)
              }
            }
          }
        }
        AiChatUiState.Idle -> Unit
      }
    }

    // Message Composer Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        placeholder = { Text("Ask Vahai AI anything...") },
        modifier = Modifier.weight(1f).testTag("ai_prompt_input"),
        shape = RoundedCornerShape(20.dp),
        maxLines = 3,
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BiolumCyan,
            unfocusedBorderColor = OceanRim,
            focusedTextColor = IceWhite,
            unfocusedTextColor = IceWhite,
            focusedContainerColor = OceanElevated.copy(alpha = 0.7f),
            unfocusedContainerColor = OceanElevated.copy(alpha = 0.5f),
          ),
      )

      Box(
        modifier =
          Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(if (!isBusy && inputText.isNotBlank()) RoyalGold else OceanElevated, CircleShape)
            .border(2.dp, CartoonInk, CircleShape)
            .clickable(enabled = !isBusy && inputText.isNotBlank()) {
              val prompt = inputText
              inputText = ""
              onSendPrompt(prompt)
            }
            .testTag("ai_send_button"),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.Send,
          contentDescription = "Send message",
          tint = if (!isBusy && inputText.isNotBlank()) OceanAbyss else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(22.dp),
        )
      }
    }
  }
}

@Composable
private fun AiChatBubble(text: String, isUser: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
  ) {
    Surface(
      shape =
        RoundedCornerShape(
          topStart = 20.dp,
          topEnd = 20.dp,
          bottomStart = if (isUser) 20.dp else 6.dp,
          bottomEnd = if (isUser) 6.dp else 20.dp,
        ),
      color = if (isUser) BiolumCyan.copy(alpha = 0.22f) else OceanElevated.copy(alpha = 0.88f),
      border =
        BorderStroke(
          1.dp,
          if (isUser) BiolumCyan.copy(alpha = 0.55f) else RoyalGold.copy(alpha = 0.4f),
        ),
      modifier = Modifier.fillMaxWidth(0.88f),
    ) {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
          text = if (isUser) "Sipiliano" else "Vahai Spirit AI",
          style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
          color = if (isUser) BiolumCyan else RoyalGold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = text,
          style = MaterialTheme.typography.bodyMedium,
          color = IceWhite,
        )
      }
    }
  }
}
