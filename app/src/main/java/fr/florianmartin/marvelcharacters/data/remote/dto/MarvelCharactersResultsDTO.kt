package fr.florianmartin.marvelcharacters.data.remote.dto

data class MarvelCharactersResultsDTO(
    val results: List<MarvelCharacterDTO> = emptyList()
)
