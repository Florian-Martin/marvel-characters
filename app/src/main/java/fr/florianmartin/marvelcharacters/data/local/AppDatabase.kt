package fr.florianmartin.marvelcharacters.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.florianmartin.marvelcharacters.data.local.dao.MarvelCharacterDao

@Database(
    entities = [MarvelCharacterEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMarvelCharactersDao(): MarvelCharacterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}