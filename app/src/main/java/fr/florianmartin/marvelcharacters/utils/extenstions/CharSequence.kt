package fr.florianmartin.marvelcharacters.utils.extenstions

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale

fun String.hash(messageDigest: MessageDigest): String {
    val digest = messageDigest.digest(this.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}

fun String.formatDate(): String? {
    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
    val newFormat = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss", Locale.getDefault())
    return try {
        originalFormat.parse(this)?.let { newFormat.format(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.parseEmoji(): String? {
    return try {
        val codePoint = this.removePrefix("U+").toInt(16)
        String(Character.toChars(codePoint))
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.httpToHttps(): String = this.replace("http://", "https://")