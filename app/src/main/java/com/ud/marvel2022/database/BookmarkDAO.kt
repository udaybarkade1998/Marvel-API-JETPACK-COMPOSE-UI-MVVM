package com.ud.marvel2022.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ud.marvel2022.model.roomTable.BookmarksTable

@Dao
interface BookmarkDAO {
    @Query("select * from bookmark")
    fun getAll(): LiveData<List<BookmarksTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BookmarksTable)

    @Delete
    suspend fun delete(item: BookmarksTable)

}