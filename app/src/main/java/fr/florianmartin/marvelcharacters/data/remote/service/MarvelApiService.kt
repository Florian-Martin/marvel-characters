package fr.florianmartin.marvelcharacters.data.remote.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fr.florianmartin.marvelcharacters.BuildConfig
import fr.florianmartin.marvelcharacters.data.remote.dto.MarvelApiResponseDTO
import fr.florianmartin.marvelcharacters.data.remote.interceptor.MarveApilInterceptor
import fr.florianmartin.marvelcharacters.utils.constants.BASE_URL
import fr.florianmartin.marvelcharacters.utils.constants.DEFAULT_LIMIT
import fr.florianmartin.marvelcharacters.utils.constants.DEFAULT_OFFSET
import fr.florianmartin.marvelcharacters.utils.constants.DEFAULT_ORDER_BY
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

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
    suspend fun getCharacters(
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