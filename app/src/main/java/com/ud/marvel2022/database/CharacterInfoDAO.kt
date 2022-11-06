package com.ud.marvel2022.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ud.marvel2022.model.roomTable.CharacterInfoTable

@Dao
interface CharacterInfoDAO {

    @Query("select * from marvel_characters order by name")
    fun getAll(): LiveData<List<CharacterInfoTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CharacterInfoTable)

}

