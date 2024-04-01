package fr.florianmartin.marvelcharacters.utils.extensions

import android.util.Log
import java.security.MessageDigest
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

interface Logger {
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

object LoggerImpl : Logger {
    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}

fun String.hash(messageDigest: MessageDigest): String {
    val digest = messageDigest.digest(this.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}

fun String.formatDate(logger: Logger): String? {
    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
    val newFormat = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss", Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
    return try {
        originalFormat.parse(this)?.let { newFormat.format(it) }
    } catch (e: ParseException) {
        logger.e("DateConversion", "Failed to parse date: $this")
        null
    } catch (e: Exception) {
        logger.e("DateConversion", "Unexpected error during date conversion")
        null
    }
}

fun String.parseEmoji(logger: Logger): String? {
    return try {
        val codePoint = this.removePrefix("U+").toInt(16)
        String(Character.toChars(codePoint))
    } catch (e: ParseException) {
        logger.e("EmojiParsing", "Failed to parse emoji: $this")
        null
    } catch (e: Exception) {
        logger.e("EmojiParsing", "Unexpected error during emoji parsing")
        null
    }
}

fun String.httpToHttps(): String = this.replace("http://", "https://")