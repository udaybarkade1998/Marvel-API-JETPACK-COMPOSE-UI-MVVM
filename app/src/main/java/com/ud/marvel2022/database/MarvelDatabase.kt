package com.ud.marvel2022.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ud.marvel2022.model.roomTable.BookmarksTable
import com.ud.marvel2022.model.roomTable.CharacterInfoTable


@Database(
    entities = [CharacterInfoTable::class, BookmarksTable::class],
    version = 2,
    exportSchema = false
)
abstract class MarvelDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterInfoDAO
    abstract fun bookmarkDao(): BookmarkDAO

    companion object {
        private var INSTANCE: MarvelDatabase? = null
        fun getInstance(context: Context): MarvelDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MarvelDatabase::class.java,
                        "marvel_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
