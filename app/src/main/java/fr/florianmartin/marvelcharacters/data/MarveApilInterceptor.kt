package fr.florianmartin.marvelcharacters.data

import fr.florianmartin.marvelcharacters.utils.extenstions.hash
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.security.MessageDigest

const val HASH = "hash"
const val MD5 = "MD5"
const val TIMESTAMP = "ts"
const val API_KEY = "apikey"

class MarveApilInterceptor(
    private val publicKey: String,
    private val privateKey: String
) : Interceptor {

    private val md5: MessageDigest = MessageDigest.getInstance(MD5)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val timestamp = System.currentTimeMillis().toString()

        val hash = "$TIMESTAMP$privateKey$publicKey".hash(md5)

        val url = originalRequest.url().newBuilder()
            .addQueryParameter(TIMESTAMP, timestamp)
            .addQueryParameter(API_KEY, publicKey)
            .addQueryParameter(HASH, hash)
            .build()

        val requestBuilder: Request.Builder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()

        return chain.proceed(request)
    }
}