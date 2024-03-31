package fr.florianmartin.marvelcharacters.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import fr.florianmartin.marvelcharacters.data.local.AppDatabase
import fr.florianmartin.marvelcharacters.data.model.MarvelCharacter
import fr.florianmartin.marvelcharacters.data.remote.service.MarvelApiService


class MarvelCharactersRepository(
    private val appDatabase: AppDatabase,
    private val marvelApiService: MarvelApiService
) {
    suspend fun getCharacters(loadSize: Int, offset: Int? = null): List<MarvelCharacter> {
        return try {
            val response = marvelApiService.getCharacters(
                limit = loadSize,
                offset = offset ?: 0
            )

            if (response.code() == 200) {
                val body = response.body()
                val newEtag = body?.etag
                body?.data?.results?.let {
                    val entities = it.map { characterDTO ->
                        characterDTO.asEntity().apply { this.etag = newEtag }
                    }
                    appDatabase.getMarvelCharactersDao().insertAll(entities)
                    entities.map { entity ->
                        entity.asModel()
                    }
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
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