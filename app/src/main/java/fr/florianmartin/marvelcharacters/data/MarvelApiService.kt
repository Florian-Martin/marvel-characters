package fr.florianmartin.marvelcharacters.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.moshi.MoshiConverterFactory
import fr.florianmartin.marvelcharacters.BuildConfig
import okhttp3.OkHttpClient

const val BASE_URL = "https://gateway.marvel.com"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val interceptor =
    MarveApilInterceptor(BuildConfig.API_PUBLIC_KEY, BuildConfig.API_PRIVATE_KEY)

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

    // TODO: Do not forget order by clause 
}

object MarvelApi {
    val retrofitService: MarvelApiService by lazy {
        retrofit.create(MarvelApiService::class.java)
    }
}