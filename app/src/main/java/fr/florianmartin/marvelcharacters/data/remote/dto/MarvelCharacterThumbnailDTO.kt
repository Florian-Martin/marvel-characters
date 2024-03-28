package fr.florianmartin.marvelcharacters.data.remote.dto

import fr.florianmartin.marvelcharacters.utils.constants.EMPTY_FIELD

data class MarvelCharacterThumbnailDTO(

    val path: String = EMPTY_FIELD,

    val extension: String = EMPTY_FIELD,
)