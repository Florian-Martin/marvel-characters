package fr.florianmartin.marvelcharacters.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import fr.florianmartin.marvelcharacters.data.local.AppDatabase
import fr.florianmartin.marvelcharacters.data.local.MarvelCharacterEntity
import fr.florianmartin.marvelcharacters.data.remote.service.MarvelApiService

@OptIn(ExperimentalPagingApi::class)
class MarvelCharactersRemoteMediator(
    private val service: MarvelApiService,
    private val appDatabase: AppDatabase
) : RemoteMediator<Int, MarvelCharacterEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MarvelCharacterEntity>
    ): MediatorResult {
        try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    state.pages.sumOf { it.data.size }
//                    val loadedItems = state.pages.sumOf { it.data.size }
//                    if (loadedItems == 0) {
//                        return MediatorResult.Success(endOfPaginationReached = true)
//                    } else {
//                        loadedItems
//                    }
                }
            }

            val apiResponse = service.getCharacters(
                limit = state.config.pageSize,
                offset = offset
            ).body() ?: return MediatorResult.Success(endOfPaginationReached = true)

            val endOfPaginationReached = apiResponse.data?.results?.isEmpty() ?: true
            val characters = apiResponse.data?.results?.map { it.asEntity() } ?: emptyList()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    appDatabase.getMarvelCharactersDao().deleteAll()
                }
                appDatabase.getMarvelCharactersDao().insertAll(characters)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }
}