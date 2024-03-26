package fr.florianmartin.marvelcharacters.utils.extenstions

import java.security.MessageDigest

fun String.hash(messageDigest: MessageDigest): String {
    val digest = messageDigest.digest(this.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}