package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.SportsRugby
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material.icons.rounded.Water
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AiBlessingState
import com.example.AppUiState
import com.example.DONATION_AMOUNTS
import com.example.DonationCause
import com.example.DonationPaymentMethod
import com.example.DonationReceipt
import com.example.DonationStage
import com.example.R
import com.example.ui.theme.BiolumCyan
import com.example.ui.theme.CartoonInk
import com.example.ui.theme.CartoonPurple
import com.example.ui.theme.HibiscusCoral
import com.example.ui.theme.IceWhite
import com.example.ui.theme.OceanAbyss
import com.example.ui.theme.OceanCardGlass
import com.example.ui.theme.OceanElevated
import com.example.ui.theme.OceanRim
import com.example.ui.theme.OceanTrench
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldDeep
import com.example.ui.theme.SeafoamTeal

@Composable
fun DonateTabContent(
  uiState: AppUiState,
  onSelectCause: (DonationCause) -> Unit,
  onSelectAmount: (Int) -> Unit,
  onSelectPayment: (DonationPaymentMethod) -> Unit,
  onUpdateNote: (String) -> Unit,
  onToggleAiBlessing: () -> Unit,
  onSubmitDonation: () -> Unit,
  onStartAnother: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // Transparent Demo Label
    Surface(
      shape = RoundedCornerShape(50),
      color = RoyalGold.copy(alpha = 0.16f),
      border = BorderStroke(1.5.dp, RoyalGold.copy(alpha = 0.55f)),
      modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.Verified,
          contentDescription = null,
          tint = RoyalGold,
          modifier = Modifier.size(16.dp),
        )
        Text(
          text = stringResource(R.string.demo_badge_label),
          style = MaterialTheme.typography.labelMedium,
          color = RoyalGold,
        )
      }
    }

    AnimatedContent(
      targetState = uiState.donationStage,
      label = "donation_stage_switch",
    ) { stage ->
      when (stage) {
        DonationStage.COMPOSING ->
          DonationComposerCard(
            uiState = uiState,
            onSelectCause = onSelectCause,
            onSelectAmount = onSelectAmount,
            onSelectPayment = onSelectPayment,
            onUpdateNote = onUpdateNote,
            onToggleAiBlessing = onToggleAiBlessing,
            onSubmitDonation = onSubmitDonation,
          )
        DonationStage.PROCESSING ->
          DonationProcessingCard(stepText = uiState.processingStepText, amount = uiState.selectedAmount)
        DonationStage.RECEIPT -> {
          val receipt = uiState.activeReceipt
          if (receipt != null) {
            DonationReceiptCelebrationCard(
              receipt = receipt,
              onStartAnother = onStartAnother,
            )
          }
        }
      }
    }

    if (uiState.recentDonations.isNotEmpty()) {
      RecentDonationsSection(receipts = uiState.recentDonations)
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DonationComposerCard(
  uiState: AppUiState,
  onSelectCause: (DonationCause) -> Unit,
  onSelectAmount: (Int) -> Unit,
  onSelectPayment: (DonationPaymentMethod) -> Unit,
  onUpdateNote: (String) -> Unit,
  onToggleAiBlessing: () -> Unit,
  onSubmitDonation: () -> Unit,
) {
  val haptic = LocalHapticFeedback.current
  val fillFraction by
    animateFloatAsState(
      targetValue = (uiState.selectedAmount / 150f).coerceIn(0.15f, 1f),
      animationSpec = spring(dampingRatio = 0.6f, stiffness = 280f),
      label = "jar_fill",
    )

  Surface(
    modifier = Modifier.fillMaxWidth().testTag("donate_composer_card"),
    shape = RoundedCornerShape(28.dp),
    color = OceanCardGlass,
    border = BorderStroke(2.dp, OceanRim),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      // Cartoon Jar Visual + Live Total Banner
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        CartoonCoinJar(fillFraction = fillFraction, modifier = Modifier.size(92.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Island Gift Jar",
            style = MaterialTheme.typography.headlineMedium,
            color = IceWhite,
          )
          Text(
            text = "Pick a cause & drop cartoon coins into Sipiliano's community vault!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = "Pool: $${uiState.communityGoalRaised} / $${uiState.communityGoalTarget}",
            style = MaterialTheme.typography.labelLarge,
            color = SeafoamTeal,
          )
        }
      }

      // Cause Grid
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "1. PICK A CAUSE",
          style = MaterialTheme.typography.labelMedium,
          color = RoyalGold,
        )
        val causes = DonationCause.entries
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          causes.chunked(2).forEach { pair ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              pair.forEach { cause ->
                CauseCartoonTile(
                  cause = cause,
                  isSelected = uiState.selectedCause == cause,
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectCause(cause)
                  },
                  modifier = Modifier.weight(1f),
                )
              }
            }
          }
        }
      }

      // Chunky 3D-Pop Amount Chips + Slider
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "2. CHOOSE AMOUNT",
            style = MaterialTheme.typography.labelMedium,
            color = BiolumCyan,
          )
          Text(
            text = "$${uiState.selectedAmount}",
            style = MaterialTheme.typography.headlineMedium,
            color = RoyalGold,
            modifier = Modifier.testTag("selected_amount_text"),
          )
        }

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          DONATION_AMOUNTS.forEach { amt ->
            val selected = uiState.selectedAmount == amt
            CartoonAmountChip(
              amount = amt,
              isSelected = selected,
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelectAmount(amt)
              },
            )
          }
        }

        Slider(
          value = uiState.selectedAmount.toFloat(),
          onValueChange = { onSelectAmount(it.toInt()) },
          valueRange = 5f..250f,
          colors =
            SliderDefaults.colors(
              thumbColor = RoyalGold,
              activeTrackColor = RoyalGold,
              inactiveTrackColor = OceanElevated,
            ),
        )
      }

      // Mock Payment Method Selector
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "3. DEMO PAYMENT METHOD",
          style = MaterialTheme.typography.labelMedium,
          color = SeafoamTeal,
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          DonationPaymentMethod.entries.forEach { method ->
            val selected = uiState.selectedPaymentMethod == method
            Surface(
              modifier =
                Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(16.dp))
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectPayment(method)
                  },
              shape = RoundedCornerShape(16.dp),
              color = if (selected) BiolumCyan.copy(alpha = 0.2f) else OceanElevated,
              border = BorderStroke(1.5.dp, if (selected) BiolumCyan else OceanRim),
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.CreditCard,
                  contentDescription = null,
                  tint = if (selected) BiolumCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = method.label,
                  style = MaterialTheme.typography.labelLarge,
                  color = IceWhite,
                  maxLines = 1,
                )
                Text(
                  text = method.detail,
                  style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                )
              }
            }
          }
        }
      }

      // Optional Note & AI Blessing Toggle
      OutlinedTextField(
        value = uiState.donorNote,
        onValueChange = onUpdateNote,
        label = { Text("Add a dedication note (optional)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors =
          OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BiolumCyan,
            unfocusedBorderColor = OceanRim,
            focusedTextColor = IceWhite,
            unfocusedTextColor = IceWhite,
            focusedLabelColor = BiolumCyan,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
          ),
      )

      Surface(
        shape = RoundedCornerShape(16.dp),
        color = OceanElevated.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, OceanRim),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.AutoAwesome,
              contentDescription = null,
              tint = RoyalGold,
            )
            Column {
              Text(
                text = "Gemini AI Thank-You Blessing",
                style = MaterialTheme.typography.labelLarge,
                color = IceWhite,
              )
              Text(
                text = "Custom island blessing on receipt",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
          Switch(
            checked = uiState.includeAiBlessing,
            onCheckedChange = { onToggleAiBlessing() },
            colors =
              SwitchDefaults.colors(
                checkedThumbColor = OceanAbyss,
                checkedTrackColor = RoyalGold,
              ),
          )
        }
      }

      // Chunky 3D-Pop Cartoon Submit Button
      CartoonCheckoutButton(
        text = "Send Cartoon Gift • $${uiState.selectedAmount}",
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onSubmitDonation()
        },
        modifier = Modifier.testTag("send_donation_button"),
      )
    }
  }
}

@Composable
private fun CauseCartoonTile(
  cause: DonationCause,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val accent =
    when (cause) {
      DonationCause.ISLAND_YOUTH -> RoyalGold
      DonationCause.OCEAN_REEF -> BiolumCyan
      DonationCause.COMMUNITY_BOX -> SeafoamTeal
      DonationCause.FAITH_CHOIR -> HibiscusCoral
    }
  val icon =
    when (cause) {
      DonationCause.ISLAND_YOUTH -> Icons.Rounded.SportsRugby
      DonationCause.OCEAN_REEF -> Icons.Rounded.Water
      DonationCause.COMMUNITY_BOX -> Icons.Rounded.CardGiftcard
      DonationCause.FAITH_CHOIR -> Icons.Rounded.VolunteerActivism
    }
  val scale by
    animateFloatAsState(
      targetValue = if (isSelected) 1.02f else 1f,
      animationSpec = spring(dampingRatio = 0.5f, stiffness = 450f),
      label = "cause_pop",
    )
  val bg by
    animateColorAsState(
      targetValue = if (isSelected) accent.copy(alpha = 0.22f) else OceanElevated,
      label = "cause_bg",
    )

  Surface(
    modifier =
      modifier
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        }
        .clip(RoundedCornerShape(18.dp))
        .clickable(onClick = onClick)
        .testTag("cause_${cause.name}"),
    shape = RoundedCornerShape(18.dp),
    color = bg,
    border = BorderStroke(if (isSelected) 2.5.dp else 1.5.dp, if (isSelected) accent else CartoonInk),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = CircleShape,
          color = accent.copy(alpha = 0.2f),
          border = BorderStroke(1.5.dp, CartoonInk),
        ) {
          Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
          }
        }
        Text(
          text = cause.badge,
          style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
          color = accent,
        )
      }
      Text(
        text = cause.title,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
        color = IceWhite,
        maxLines = 2,
      )
      Text(
        text = cause.subtitle,
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, lineHeight = 14.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
      )
    }
  }
}

@Composable
private fun CartoonAmountChip(
  amount: Int,
  isSelected: Boolean,
  onClick: () -> Unit,
) {
  val bg by
    animateColorAsState(
      targetValue = if (isSelected) RoyalGold else OceanElevated,
      label = "amt_bg",
    )
  Box(
    modifier =
      Modifier
        .clip(RoundedCornerShape(14.dp))
        .background(CartoonInk, RoundedCornerShape(14.dp))
        .padding(bottom = 3.dp)
        .background(bg, RoundedCornerShape(14.dp))
        .border(2.dp, CartoonInk, RoundedCornerShape(14.dp))
        .clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .testTag("amount_chip_$amount"),
  ) {
    Text(
      text = "$$amount",
      style = MaterialTheme.typography.titleMedium,
      color = if (isSelected) CartoonInk else IceWhite,
    )
  }
}

@Composable
private fun CartoonCheckoutButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by
    animateFloatAsState(
      targetValue = if (isPressed) 0.94f else 1f,
      animationSpec = spring(dampingRatio = 0.45f, stiffness = 520f),
      label = "checkout_squish",
    )

  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .height(62.dp)
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        }
        .clip(RoundedCornerShape(20.dp))
        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
    contentAlignment = Alignment.TopCenter,
  ) {
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(top = 5.dp)
          .background(CartoonInk, RoundedCornerShape(20.dp)),
    )
    Box(
      modifier =
        Modifier
          .fillMaxWidth()
          .height(57.dp)
          .background(
            brush = Brush.horizontalGradient(listOf(RoyalGold, Color(0xFFFDE047))),
            shape = RoundedCornerShape(20.dp),
          )
          .border(2.5.dp, CartoonInk, RoundedCornerShape(20.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.Favorite,
          contentDescription = null,
          tint = HibiscusCoral,
          modifier = Modifier.size(20.dp),
        )
        Text(
          text = text,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = CartoonInk,
        )
      }
    }
  }
}

@Composable
private fun DonationProcessingCard(stepText: String, amount: Int) {
  val spinTransition = rememberInfiniteTransition(label = "coin_spin")
  val scaleXSpin by
    spinTransition.animateFloat(
      initialValue = 1f,
      targetValue = -1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 550, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "coin_flip",
    )

  Surface(
    modifier = Modifier.fillMaxWidth().testTag("donate_processing_card"),
    shape = RoundedCornerShape(28.dp),
    color = OceanCardGlass,
    border = BorderStroke(2.dp, RoyalGold),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      Box(
        modifier =
          Modifier
            .size(84.dp)
            .graphicsLayer { scaleX = kotlin.math.abs(scaleXSpin).coerceAtLeast(0.18f) }
            .background(RoyalGold, CircleShape)
            .border(3.dp, CartoonInk, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "$$amount",
          style = MaterialTheme.typography.headlineMedium,
          color = CartoonInk,
        )
      }

      CircularProgressIndicator(color = BiolumCyan, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))

      Text(
        text = stepText,
        style = MaterialTheme.typography.titleLarge,
        color = IceWhite,
        textAlign = TextAlign.Center,
      )
      Text(
        text = "Processing cartoon transaction...",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun DonationReceiptCelebrationCard(
  receipt: DonationReceipt,
  onStartAnother: () -> Unit,
) {
  val burst = remember { Animatable(0f) }
  val stampScale = remember { Animatable(1.8f) }
  LaunchedEffect(receipt.txId) {
    burst.snapTo(0f)
    burst.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
  }
  LaunchedEffect(receipt.txId) {
    stampScale.snapTo(1.7f)
    stampScale.animateTo(1f, animationSpec = spring(dampingRatio = 0.42f, stiffness = 420f))
  }

  Surface(
    modifier = Modifier.fillMaxWidth().testTag("donate_receipt_card"),
    shape = RoundedCornerShape(28.dp),
    color = OceanCardGlass,
    border = BorderStroke(2.dp, SeafoamTeal),
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      // Celebratory confetti canvas behind receipt
      Canvas(modifier = Modifier.matchParentSize()) {
        val colors = listOf(RoyalGold, BiolumCyan, SeafoamTeal, HibiscusCoral, CartoonPurple)
        val center = Offset(size.width / 2f, 110f)
        for (i in 0 until 22) {
          val angle = (i * (360f / 22f)) * (Math.PI / 180.0)
          val dist = (size.minDimension * 0.55f) * burst.value
          val x = center.x + (kotlin.math.cos(angle) * dist).toFloat()
          val y = center.y + (kotlin.math.sin(angle) * dist).toFloat()
          drawCircle(
            color = colors[i % colors.size].copy(alpha = (1f - burst.value).coerceIn(0f, 1f)),
            radius = 9f,
            center = Offset(x, y),
          )
        }
      }

      Column(
        modifier = Modifier.fillMaxWidth().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Surface(
          shape = CircleShape,
          color = SeafoamTeal,
          border = BorderStroke(2.5.dp, CartoonInk),
          modifier =
            Modifier
              .size(68.dp)
              .graphicsLayer {
                scaleX = stampScale.value
                scaleY = stampScale.value
              },
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Rounded.CheckCircle,
              contentDescription = null,
              tint = CartoonInk,
              modifier = Modifier.size(38.dp),
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(50),
          color = SeafoamTeal.copy(alpha = 0.16f),
          border = BorderStroke(1.dp, SeafoamTeal),
        ) {
          Text(
            text = "TRANSACTION APPROVED • DEMO",
            style = MaterialTheme.typography.labelMedium,
            color = SeafoamTeal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
          )
        }

        Text(
          text = "$${receipt.amountDollars} Gift Sent!",
          style = MaterialTheme.typography.displayMedium,
          color = RoyalGold,
        )

        // Itemized Receipt Details
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          color = OceanTrench,
          border = BorderStroke(1.dp, OceanRim),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            ReceiptRow("Transaction ID", "#${receipt.txId}")
            ReceiptRow("From", "Sipiliano Vahai")
            ReceiptRow("Cause", receipt.cause.title)
            ReceiptRow("Method", "${receipt.paymentMethod.label} (${receipt.paymentMethod.detail})")
            ReceiptRow("Time", receipt.timestampLabel)
            if (receipt.donorNote.isNotBlank()) {
              HorizontalDivider(color = OceanRim.copy(alpha = 0.5f))
              Text(
                text = "Note: \"${receipt.donorNote}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = IceWhite,
              )
            }
          }
        }

        // Gemini AI Blessing Section
        if (receipt.aiBlessingState !is AiBlessingState.Idle) {
          Surface(
            modifier = Modifier.fillMaxWidth().testTag("ai_blessing_box"),
            shape = RoundedCornerShape(20.dp),
            color = RoyalGold.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.45f)),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.AutoAwesome,
                  contentDescription = null,
                  tint = RoyalGold,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = "Vahai AI Blessing",
                  style = MaterialTheme.typography.labelLarge,
                  color = RoyalGold,
                )
              }
              when (val state = receipt.aiBlessingState) {
                is AiBlessingState.Loading -> {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                  ) {
                    CircularProgressIndicator(
                      color = RoyalGold,
                      strokeWidth = 2.dp,
                      modifier = Modifier.size(18.dp),
                    )
                    Text(
                      text = "Gemini is crafting your island blessing...",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                  }
                }
                is AiBlessingState.Success -> {
                  Text(
                    text = state.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = IceWhite,
                  )
                }
                is AiBlessingState.Error -> {
                  Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HibiscusCoral,
                  )
                }
                AiBlessingState.Idle -> Unit
              }
            }
          }
        }

        Button(
          onClick = onStartAnother,
          modifier = Modifier.fillMaxWidth().height(54.dp).testTag("donate_again_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BiolumCyan, contentColor = OceanAbyss),
        ) {
          Text(text = "Send Another Cartoon Gift", style = MaterialTheme.typography.titleMedium)
        }
      }
    }
  }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = value,
      style = MaterialTheme.typography.labelLarge,
      color = IceWhite,
      textAlign = TextAlign.End,
    )
  }
}

@Composable
private fun RecentDonationsSection(receipts: List<DonationReceipt>) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(22.dp),
    color = OceanElevated.copy(alpha = 0.72f),
    border = BorderStroke(1.dp, OceanRim),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(imageVector = Icons.Rounded.ReceiptLong, contentDescription = null, tint = BiolumCyan)
        Text(
          text = "Recent Demo Gifts",
          style = MaterialTheme.typography.titleMedium,
          color = IceWhite,
        )
      }
      receipts.forEach { item ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = item.cause.title, style = MaterialTheme.typography.labelLarge, color = IceWhite)
            Text(
              text = "#${item.txId} • ${item.timestampLabel}",
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          Surface(
            shape = RoundedCornerShape(50),
            color = SeafoamTeal.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, SeafoamTeal.copy(alpha = 0.5f)),
          ) {
            Text(
              text = "$${item.amountDollars}",
              style = MaterialTheme.typography.labelLarge,
              color = SeafoamTeal,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CartoonCoinJar(
  fillFraction: Float,
  modifier: Modifier = Modifier,
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    // Jar Lid
    drawRoundRect(
      color = RoyalGoldDeep,
      topLeft = Offset(w * 0.24f, h * 0.08f),
      size = Size(w * 0.52f, h * 0.14f),
      cornerRadius = CornerRadius(12f, 12f),
    )
    drawRoundRect(
      color = CartoonInk,
      topLeft = Offset(w * 0.24f, h * 0.08f),
      size = Size(w * 0.52f, h * 0.14f),
      cornerRadius = CornerRadius(12f, 12f),
      style = Stroke(width = 5f),
    )

    // Jar Body
    val bodyTop = h * 0.22f
    val bodyHeight = h * 0.70f
    drawRoundRect(
      color = OceanTrench,
      topLeft = Offset(w * 0.14f, bodyTop),
      size = Size(w * 0.72f, bodyHeight),
      cornerRadius = CornerRadius(26f, 26f),
    )

    // Golden Treasure Fill inside jar
    val innerHeight = (bodyHeight - 14f) * fillFraction
    val fillTop = bodyTop + bodyHeight - 7f - innerHeight
    drawRoundRect(
      brush = Brush.verticalGradient(listOf(RoyalGold, RoyalGoldDeep)),
      topLeft = Offset(w * 0.18f, fillTop),
      size = Size(w * 0.64f, innerHeight.coerceAtLeast(10f)),
      cornerRadius = CornerRadius(18f, 18f),
    )

    // Cartoon Jar Outline + Glass Glint
    drawRoundRect(
      color = CartoonInk,
      topLeft = Offset(w * 0.14f, bodyTop),
      size = Size(w * 0.72f, bodyHeight),
      cornerRadius = CornerRadius(26f, 26f),
      style = Stroke(width = 6f),
    )
    drawLine(
      color = IceWhite.copy(alpha = 0.45f),
      start = Offset(w * 0.25f, bodyTop + 18f),
      end = Offset(w * 0.25f, bodyTop + bodyHeight - 24f),
      strokeWidth = 5f,
    )
  }
}
