package com.ud.marvel2022.viewmodel

import android.content.Context

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.marvel2022.database.BookmarkDAO
import com.ud.marvel2022.database.CharacterInfoDAO
import com.ud.marvel2022.database.MarvelDatabase
import com.ud.marvel2022.model.roomTable.BookmarkData
import com.ud.marvel2022.model.character.*
import com.ud.marvel2022.model.roomTable.BookmarksTable
import com.ud.marvel2022.model.roomTable.CharacterInfoTable
import com.ud.marvel2022.network.MarvelAPI
import com.ud.marvel2022.repository.MarvelRepository
import kotlinx.coroutines.launch
import java.sql.Date

class CharacterViewModel : ViewModel() {

    //state for network call status
    var observeNetworkCall: MutableLiveData<Boolean> = MutableLiveData()

    //state for search call status
    var searchState : Boolean by mutableStateOf(false)

    //state for character list fetch from database
    var characterListResponse: CharacterData by mutableStateOf(
        CharacterData(
            "",
            "",
            0,
            "",
            null,
            "",
            ""
        )
    )

    //state for bookmark list fetch from database
    var bookmarkListResponse: BookmarkData by mutableStateOf(
        BookmarkData(null)
    )

    //Room database variables
    //Character list
    lateinit var readAllData: LiveData<List<CharacterInfoTable>>

    //Bookmark list
    lateinit var bookmarkAllData: LiveData<List<BookmarksTable>>

    //repository to access all database functionality
    lateinit var repository: MarvelRepository

    //DAO for bookmarks
    private lateinit var bookmarkDAO: BookmarkDAO

    //DAO for characters
    private lateinit var characterDAO: CharacterInfoDAO

    var errorMessage: String by mutableStateOf("")

    init {
        observeNetworkCall.value = false
    }

    //initialize all data using context from activity
    fun initData(context: Context) {
        characterDAO = MarvelDatabase.getInstance(context).characterDao()
        bookmarkDAO = MarvelDatabase.getInstance(context).bookmarkDao()
        repository = MarvelRepository(characterDAO, bookmarkDAO)
        readAllData = repository.readAllData
        bookmarkAllData = repository.bookmarkList
    }

    //insert character to database
    private fun insertCharacterIntoDatabase(character: CharacterInfoTable) {
        viewModelScope.launch {
            repository.addData(character)
        }
    }
    
    //add bookmarks to database
    fun addBookmark(id: Int) {
        viewModelScope.launch { repository.addBookmark(BookmarksTable(id)) }
    }

    //remove bookmark from database
    fun removeBookmark(id: Int) {
        viewModelScope.launch { repository.removeBookmark(BookmarksTable(id)) }
    }
    
    //fetch character list from API
    fun getCharacterListFromAPI() {
        viewModelScope.launch {
            val marvelAPI = MarvelAPI.getInstance()
            try {
                val characterList = marvelAPI.getCharacters(100, null, ts = "tsor")
                processCharacterDataFromAPItoDatabase(characterList)
            } catch (e: java.lang.Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    //function to store API Data to database
    private fun processCharacterDataFromAPItoDatabase(characterList: CharacterData) {
        characterListResponse = characterList
        characterList.data!!.results.forEach { it ->

            val thumbnail = it.thumbnail.path + "$$" + it.thumbnail.extension
            var comics = ""

            it.comics!!.items.forEach { item ->
                comics += "${item.name}$$"
            }

            val characterRoomInfo = CharacterInfoTable(
                name = it.name,
                description = it.description,
                id = it.id,
                thumbnail = thumbnail,
                comics = comics
            )
            insertCharacterIntoDatabase(characterRoomInfo)
        }
    }

    //get data from database and save to Live state
    fun getLocalDatabaseData() {
        viewModelScope.launch {

            val apiResult = mutableListOf<ApiResult>()
            lateinit var tmpcharResp: CharacterData
            lateinit var tmpData: Data
            lateinit var comicItemList: MutableList<Item>

            if (readAllData.value != null) {
                readAllData.value!!.forEach {


                    comicItemList = mutableListOf()

                    it.comics.split("$$").forEach {
                        comicItemList.add(Item(name = it, ""))
                    }
                    comicItemList.removeLast()

                    apiResult.add(
                        ApiResult(
                            id = it.id,
                            name = it.name,
                            comics = Comics(0, "", items = comicItemList, 0),
                            thumbnail = Thumbnail(
                                path = it.thumbnail.split("$$")[0],
                                extension = it.thumbnail.split("$$")[1]
                            ),
                            description = it.description
                        )
                    )
                }

                tmpData = Data(0, 10, 0, apiResult, 10)
                tmpcharResp = CharacterData("", "", 0, "", tmpData, "", "")

                characterListResponse = tmpcharResp
            }


        }

    }

    //get bookmark list and save to state
    fun getBookmarkedRoomData() {
        viewModelScope.launch {

            bookmarkAllData = repository.bookmarkList
            if (bookmarkAllData.value != null) {

                lateinit var tmpBookmark: BookmarkData
                val tmpBookTable = mutableListOf<BookmarksTable>()

                bookmarkAllData.value!!.forEach {
                    tmpBookTable.add(BookmarksTable(it.id))
                }
                tmpBookmark = BookmarkData(tmpBookTable)
                bookmarkListResponse = tmpBookmark
            }

        }
    }

    //search feature applied on LIVE API i.e nameStartWith
    fun getSearchedCharacters(query: String) {
        viewModelScope.launch {
            val marvelAPI = MarvelAPI.getInstance()
            try {

                val characterList = marvelAPI.getCharactersByName(query, 100, null, ts = "${System.currentTimeMillis()/1000}")
                processCharacterDataFromAPItoDatabase(characterList)
            } catch (e: java.lang.Exception) {
                errorMessage = e.message.toString()
            }
        }
    }
}

