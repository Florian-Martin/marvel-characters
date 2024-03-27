package fr.florianmartin.marvelcharacters.data.remote.dto

import com.squareup.moshi.Json

data class MarvelCharacterDTO(
    val id: Int = DEFAULT_ID,
    val name: String = EMPTY_NAME,
    val description: String = EMPTY_DESCRIPTION,
    val thumbnail: MarvelCharacterThumbnailDTO? = null,
    @Json(name = "modified")
    val modifiedOn: String = EMPTY_FIELD,
    val comics: MarvelCharacterComicsDTO? = null,
    val urls: List<MarvelCharacterUrlDTO> = emptyList(),
)