package fr.florianmartin.marvelcharacters.data.remote.interceptor

import fr.florianmartin.marvelcharacters.utils.constants.API_KEY
import fr.florianmartin.marvelcharacters.utils.constants.HASH
import fr.florianmartin.marvelcharacters.utils.constants.MD5
import fr.florianmartin.marvelcharacters.utils.constants.TIMESTAMP
import fr.florianmartin.marvelcharacters.utils.extenstions.hash
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.security.MessageDigest

class MarveApilInterceptor(
    private val publicKey: String,
    private val privateKey: String
) : Interceptor {

    private val md5: MessageDigest = MessageDigest.getInstance(MD5)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val timestamp = System.currentTimeMillis().toString()

        val hash = "$timestamp$privateKey$publicKey".hash(md5)

        val url = originalRequest.url.newBuilder()
            .addQueryParameter(TIMESTAMP, timestamp)
            .addQueryParameter(API_KEY, publicKey)
            .addQueryParameter(HASH, hash)
            .build()

        val requestBuilder: Request.Builder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()

        return chain.proceed(request)
    }
}