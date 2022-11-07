package com.ud.marvel2022.repository

import androidx.lifecycle.LiveData
import com.ud.marvel2022.database.BookmarkDAO
import com.ud.marvel2022.database.CharacterInfoDAO
import com.ud.marvel2022.model.roomTable.BookmarksTable
import com.ud.marvel2022.model.roomTable.CharacterInfoTable

class MarvelRepository(private val characterInfoDAO: CharacterInfoDAO,private val bookmarkInfoDAO: BookmarkDAO) {

    //variable used to store all characters data
    var readAllData: LiveData<List<CharacterInfoTable>> = characterInfoDAO.getAll()

    //variable used to store bookmark data
    var bookmarkList : LiveData<List<BookmarksTable>> = bookmarkInfoDAO.getAll()

    //add character to database
    suspend fun addData(item : CharacterInfoTable){
        characterInfoDAO.insert(item)
    }

    //add bookmark to database
    suspend fun addBookmark(item:BookmarksTable){
        bookmarkInfoDAO.insert(item)
    }

    //remove bookmark from database
    suspend fun removeBookmark(item:BookmarksTable){
        bookmarkInfoDAO.delete(item)
    }
}