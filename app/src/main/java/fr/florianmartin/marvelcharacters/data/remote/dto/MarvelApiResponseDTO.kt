package fr.florianmartin.marvelcharacters.data.remote.dto

import fr.florianmartin.marvelcharacters.utils.constants.EMPTY_FIELD

data class MarvelApiResponseDTO(

    val etag: String = EMPTY_FIELD,

    val data: MarvelCharactersResultsDTO? = null,
)
