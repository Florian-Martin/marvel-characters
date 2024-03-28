package fr.florianmartin.marvelcharacters.ui.theme.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import fr.florianmartin.marvelcharacters.data.repository.MarvelCharactersRepository

class MarvelCharactersViewModel(repository: MarvelCharactersRepository) : ViewModel() {

    val characters = repository.characters.cachedIn(viewModelScope)

    companion object {
        fun provideFactory(
            repository: MarvelCharactersRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MarvelCharactersViewModel(repository) as T
        }
    }
}