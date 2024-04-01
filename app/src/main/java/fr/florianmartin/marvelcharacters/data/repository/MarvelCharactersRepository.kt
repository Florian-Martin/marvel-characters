package fr.florianmartin.marvelcharacters.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import fr.florianmartin.marvelcharacters.data.local.AppDatabase
import fr.florianmartin.marvelcharacters.data.local.DataStoreManager
import fr.florianmartin.marvelcharacters.data.model.MarvelCharacter
import fr.florianmartin.marvelcharacters.data.remote.service.MarvelApiService
import fr.florianmartin.marvelcharacters.utils.NetworkUtils
import fr.florianmartin.marvelcharacters.utils.constants.DATABASE_REFRESH_AFTER
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "MarvelCharactersRepository"

class MarvelCharactersRepository(
    private val appDatabase: AppDatabase,
    private val marvelApiService: MarvelApiService,
    private val datastoreManager: DataStoreManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val context: Context
) {
    suspend fun getCharacters(
        limit: Int,
        offset: Int? = null
    ): List<MarvelCharacter> {

        if (!NetworkUtils.isNetworkAvailable(context) && offset == 0) {
            return appDatabase.getMarvelCharactersDao().getCharacters().map { it.asModel() }
        }

        return try {
            val response = marvelApiService.getCharacters(
                etag = datastoreManager.getEtag(),
                limit = limit,
                offset = offset ?: 0
            )

            when (response.code()) {
                200 -> {
                    val body = response.body()
                    val newEtag = body?.etag
                    body?.data?.results?.let {
                        if (offset == 0 && shouldRefreshDatabase()) {
                            withContext(dispatcher) {
                                appDatabase.getMarvelCharactersDao().deleteAll()
                            }
                        }
                        with(datastoreManager) {
                            saveLastApiFetchTimestamp(System.currentTimeMillis())
                            newEtag?.let { etag ->
                                saveEtag(etag)
                            }
                        }

                        val entities = it.map { characterDTO ->
                            characterDTO.asEntity()
                        }
                        appDatabase.getMarvelCharactersDao().insertAll(entities)
                        entities.map { entity ->
                            entity.asModel()
                        }
                    } ?: emptyList()
                }

                304 -> {
                    return appDatabase.getMarvelCharactersDao().getCharacters(limit = limit)
                        .map { it.asModel() }
                }

                401, 403, 405, 409 -> {
                    Log.e("***", response.message())
                    throw HttpException(response)
                }

                else -> throw HttpException(response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "in / out exception")
            throw IOException("An unexpected error occurred")
        }
    }

    private suspend fun shouldRefreshDatabase(): Boolean {
        return datastoreManager.getLastFetchTimestamp()?.let {
            System.currentTimeMillis() - it > DATABASE_REFRESH_AFTER
        } ?: false
    }
}


class CharactersPagingSource(private val repository: MarvelCharactersRepository) :
    PagingSource<Int, MarvelCharacter>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MarvelCharacter> {
        return try {
            val offset = params.key ?: 0
            val data = repository.getCharacters(params.loadSize, offset)
            LoadResult.Page(
                data = data,
                prevKey = if (offset == 0) null else offset - params.loadSize,
                nextKey = if (data.isEmpty()) null else offset + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MarvelCharacter>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
}