package fr.florianmartin.marvelcharacters.ui.screens.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import fr.florianmartin.marvelcharacters.data.model.MarvelCharacter
import fr.florianmartin.marvelcharacters.data.repository.CharactersPagingSource
import fr.florianmartin.marvelcharacters.data.repository.MarvelCharactersRepository
import kotlinx.coroutines.flow.Flow

class MarvelCharactersViewModel(repository: MarvelCharactersRepository) : ViewModel() {

    val characters: Flow<PagingData<MarvelCharacter>> = Pager(
        config = PagingConfig(
            enablePlaceholders = false,
            pageSize = 10,
            prefetchDistance = 5
        ),
        pagingSourceFactory = { CharactersPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)


    companion object {
        fun providerFactory(repository: MarvelCharactersRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    MarvelCharactersViewModel(repository) as T
            }
    }
}