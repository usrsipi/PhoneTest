package com.example.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "VahaiAiRepository"
private const val MODEL_NAME = "gemini-3.8-flash"

class VahaiAiRepository {

  private val model by lazy {
    Firebase.ai.generativeModel(
      modelName = MODEL_NAME,
      systemInstruction =
        content {
          text(
            "You are Vahai Spirit AI, the lively, encouraging, Polynesian-inspired personal companion inside Sipiliano Vahai's Android app. Keep replies warm, uplifting, crisp, and fun (usually 2 to 5 sentences unless asked for more). You may weave in friendly Tongan greetings like 'Mālō e lelei' or '‘Ofa atu' naturally.",
          )
        },
    )
  }

  fun streamChatReply(
    history: List<Content>,
    userMessage: String,
  ): Flow<String> = flow {
    val chat = model.startChat(history = history)
    val buffer = StringBuilder()
    withTimeout(60_000L) {
      chat.sendMessageStream(userMessage).collect { chunk ->
        val piece = chunk.text.orEmpty()
        if (piece.isNotEmpty()) {
          buffer.append(piece)
          emit(buffer.toString())
        }
      }
    }
    if (buffer.isBlank()) {
      throw IllegalStateException("Gemini returned an empty response.")
    }
  }

  suspend fun generateDonationBlessing(
    causeTitle: String,
    amountDollars: Int,
    donorNote: String,
  ): Result<String> {
    return try {
      val noteClause =
        if (donorNote.isNotBlank()) " Their personal note says: \"$donorNote\"." else ""
      val prompt =
        "Write a cheerful, heartfelt 2-sentence cartoon-style thank-you blessing for Sipiliano Vahai's demo gift of $$amountDollars toward '$causeTitle'.$noteClause Make it feel celebratory and warm."
      val response = withTimeout(45_000L) { model.generateContent(prompt) }
      val text = response.text?.trim()
      if (!text.isNullOrBlank()) {
        Result.success(text)
      } else {
        Result.failure(IllegalStateException("Gemini returned an empty blessing."))
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "Donation blessing generation timed out", e)
      Result.failure(Exception("AI blessing timed out. Please try again."))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Donation blessing generation failed", e)
      Result.failure(Exception(e.localizedMessage ?: "AI blessing failed."))
    }
  }
}
