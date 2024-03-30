package fr.florianmartin.marvelcharacters.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.florianmartin.marvelcharacters.data.local.MarvelCharacterEntity

@Dao
interface MarvelCharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<MarvelCharacterEntity>)

    @Query(
        "SELECT * FROM character " +
                "ORDER BY name ASC"
    )
    fun pagingSource(): PagingSource<Int, MarvelCharacterEntity>

    @Query("DELETE FROM character")
    suspend fun deleteAll()

}