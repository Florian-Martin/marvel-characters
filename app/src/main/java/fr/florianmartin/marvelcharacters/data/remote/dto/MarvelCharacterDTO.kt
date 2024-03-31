package fr.florianmartin.marvelcharacters.data.remote.dto

import com.squareup.moshi.Json
import fr.florianmartin.marvelcharacters.data.local.MarvelCharacterEntity
import fr.florianmartin.marvelcharacters.utils.constants.DEFAULT_ID
import fr.florianmartin.marvelcharacters.utils.constants.EMPTY_DESCRIPTION
import fr.florianmartin.marvelcharacters.utils.constants.EMPTY_FIELD
import fr.florianmartin.marvelcharacters.utils.constants.EMPTY_NAME
import fr.florianmartin.marvelcharacters.utils.extenstions.formatDate
import fr.florianmartin.marvelcharacters.utils.extenstions.httpToHttps

data class MarvelCharacterDTO(

    val id: Int = DEFAULT_ID,

    val name: String = EMPTY_NAME,

    val description: String = EMPTY_DESCRIPTION,

    val thumbnail: MarvelCharacterThumbnailDTO? = null,

    @Json(name = "modified")
    val modifiedOn: String = EMPTY_FIELD,

    val comics: MarvelCharacterComicsDTO? = null,

    val urls: List<MarvelCharacterUrlDTO> = emptyList(),
) {
    fun asEntity() = MarvelCharacterEntity(
        marvelId = id,
        name = name,
        description = description.ifEmpty { EMPTY_DESCRIPTION },
        thumbnail = thumbnail?.let {
            "${it.path}.${it.extension}"
        }?.httpToHttps() ?: EMPTY_FIELD,
        modifiedOn = modifiedOn.formatDate() ?: EMPTY_FIELD,
        appearancesInComics = comics?.available ?: 0,
    )
}