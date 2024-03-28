package fr.florianmartin.marvelcharacters.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import fr.florianmartin.marvelcharacters.data.local.AppDatabase
import fr.florianmartin.marvelcharacters.data.model.MarvelCharacter
import fr.florianmartin.marvelcharacters.data.remote.MarvelCharactersRemoteMediator
import fr.florianmartin.marvelcharacters.data.remote.service.MarvelApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MarvelCharactersRepository(
    private val appDatabase: AppDatabase,
    marvelApiService: MarvelApiService
) {
    @OptIn(ExperimentalPagingApi::class)
    val characters: Flow<PagingData<MarvelCharacter>> = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = 10),
        remoteMediator = MarvelCharactersRemoteMediator(marvelApiService, appDatabase)
    ) {
        appDatabase.getMarvelCharactersDao().pagingSource()
    }.flow.map { pagingData ->
        pagingData.map { it.asModel() }
    }
}