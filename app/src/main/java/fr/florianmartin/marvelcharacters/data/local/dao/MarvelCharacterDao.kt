package fr.florianmartin.marvelcharacters.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import fr.florianmartin.marvelcharacters.data.local.MarvelCharacterEntity

@Dao
interface MarvelCharacterDao {
    @Upsert
    suspend fun upsertAll(characters: List<MarvelCharacterEntity>)

    @Query("SELECT * FROM character")
    fun pagingSource(): PagingSource<Int, MarvelCharacterEntity>

    @Query("DELETE FROM character")
    suspend fun clearAll()

}