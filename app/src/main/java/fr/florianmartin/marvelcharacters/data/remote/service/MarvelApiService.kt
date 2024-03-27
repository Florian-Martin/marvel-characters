package fr.florianmartin.marvelcharacters.data.remote.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import fr.florianmartin.marvelcharacters.BuildConfig
import fr.florianmartin.marvelcharacters.data.remote.dto.MarvelApiResponseDTO
import fr.florianmartin.marvelcharacters.data.remote.interceptor.MarveApilInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://gateway.marvel.com"
const val DEFAULT_ORDER_BY = "name"
const val DEFAULT_LIMIT = 10
const val DEFAULT_OFFSET = 10

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val interceptor = MarveApilInterceptor(
    publicKey = BuildConfig.API_PUBLIC_KEY,
    privateKey = BuildConfig.API_PRIVATE_KEY
)

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    )
    .build()

interface MarvelApiService {
    @GET("/v1/public/characters")
    fun getCharacters(
        @Query("orderBy") orderBy: String = DEFAULT_ORDER_BY,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("offset") offset: Int = DEFAULT_OFFSET
    ): Response<MarvelApiResponseDTO>
}

object MarvelApi {
    val retrofitService: MarvelApiService by lazy {
        retrofit.create(MarvelApiService::class.java)
    }
}