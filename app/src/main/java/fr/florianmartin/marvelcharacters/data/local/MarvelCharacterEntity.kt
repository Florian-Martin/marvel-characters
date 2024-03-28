package fr.florianmartin.marvelcharacters.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.florianmartin.marvelcharacters.data.model.MarvelCharacter

@Entity(tableName = "character")
data class MarvelCharacterEntity(

    @PrimaryKey
    val id: Int = 0,

    val name: String,

    val description: String,

    val thumbnail: String,

    @ColumnInfo(name = "modified_on")
    val modifiedOn: String,

    @ColumnInfo(name = "appearances_in_comics")
    val appearancesInComics: Int,
) {
    fun asModel() = MarvelCharacter(
        id = id,
        name = name,
        description = description,
        thumbnail = thumbnail,
        modifiedOn = modifiedOn,
        appearancesInComics = appearancesInComics
    )
}
