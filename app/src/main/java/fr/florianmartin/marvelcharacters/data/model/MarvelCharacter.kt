package fr.florianmartin.marvelcharacters.data.model

data class MarvelCharacter(

    val id: Int,

    val name: String,

    val description: String,

    val thumbnail: String,

    val modifiedOn: String,

    val appearancesInComics: Int,
)
