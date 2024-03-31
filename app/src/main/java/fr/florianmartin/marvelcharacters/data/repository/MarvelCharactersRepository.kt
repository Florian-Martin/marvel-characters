package fr.florianmartin.marvelcharacters.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import fr.florianmartin.marvelcharacters.data.local.AppDatabase
import fr.florianmartin.marvelcharacters.data.model.MarvelCharacter
import fr.florianmartin.marvelcharacters.data.remote.service.MarvelApiService
import fr.florianmartin.marvelcharacters.utils.NetworkUtils
import java.io.IOException

private const val TAG = "MarvelCharactersRepository"

class MarvelCharactersRepository(
    private val appDatabase: AppDatabase,
    private val marvelApiService: MarvelApiService,
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
                limit = limit,
                offset = offset ?: 0
            )

            when (response.code()) {
                200 -> {
                    val body = response.body()
                    body?.data?.results?.let {
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
                    emptyList()
                }

                else -> throw retrofit2.HttpException(response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "in / out exception")
            throw IOException("An unexpected error occurred")
        }
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