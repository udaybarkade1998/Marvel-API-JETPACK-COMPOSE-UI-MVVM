package com.ud.marvel2022.repository

import androidx.lifecycle.LiveData
import com.ud.marvel2022.database.BookmarkDAO
import com.ud.marvel2022.database.CharacterInfoDAO
import com.ud.marvel2022.model.roomTable.BookmarksTable
import com.ud.marvel2022.model.roomTable.CharacterInfoTable

class MarvelRepository(private val characterInfoDAO: CharacterInfoDAO,private val bookmarkInfoDAO: BookmarkDAO) {

    var readAllData: LiveData<List<CharacterInfoTable>> = characterInfoDAO.getAll()
    var bookmarkList : LiveData<List<BookmarksTable>> = bookmarkInfoDAO.getAll()

    suspend fun addData(item : CharacterInfoTable){
        characterInfoDAO.insert(item)
    }

    suspend fun addBookmark(item:BookmarksTable){
        bookmarkInfoDAO.insert(item)
    }

    suspend fun removeBookmark(item:BookmarksTable){
        bookmarkInfoDAO.delete(item)
    }
}