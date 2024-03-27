package fr.florianmartin.marvelcharacters.data.remote.dto

const val EMPTY_DESCRIPTION = "This character has no description"
const val EMPTY_NAME = "This character has no name :o"
const val EMPTY_FIELD = ""
const val DEFAULT_ID = -1

data class MarvelApiResponseDTO(
    val etag: String = EMPTY_FIELD,
    val data: MarvelCharactersResultsDTO? = null,
)
