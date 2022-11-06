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
import com.ud.marvel2022.model.BookmarkData
import com.ud.marvel2022.model.character.*
import com.ud.marvel2022.model.roomTable.BookmarksTable
import com.ud.marvel2022.model.roomTable.CharacterInfoTable
import com.ud.marvel2022.network.MarvelAPI
import com.ud.marvel2022.repository.MarvelRepository
import kotlinx.coroutines.launch

class CharacterViewModel : ViewModel() {

    var observeNetworkCall: MutableLiveData<Boolean> = MutableLiveData()

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

    var bookmarkListResponse: BookmarkData by mutableStateOf(
        BookmarkData(null)
    )


    var errorMessage: String by mutableStateOf("")

    lateinit var readAllData: LiveData<List<CharacterInfoTable>>
    lateinit var bookmarkAllData: LiveData<List<BookmarksTable>>

    lateinit var repository: MarvelRepository
    private lateinit var bookmars: BookmarkDAO
    private lateinit var characters: CharacterInfoDAO

    init {
        observeNetworkCall.value = false
    }

    private fun insertIntoDatabase(character: CharacterInfoTable) {
        viewModelScope.launch {
            repository.addData(character)
        }
    }

    fun setInit(context: Context) {
        characters = MarvelDatabase.getInstance(context).characterDao()
        bookmars = MarvelDatabase.getInstance(context).bookmarkDao()

        repository = MarvelRepository(characters, bookmars)

        readAllData = repository.readAllData
        bookmarkAllData = repository.bookmarkList


    }

    fun addBookmark(id: Int) {
        viewModelScope.launch { repository.addBookmark(BookmarksTable(id)) }
    }

    fun removeBookmark(id: Int) {
        viewModelScope.launch { repository.removeBookmark(BookmarksTable(id)) }
    }


    fun getCharacterList() {
        viewModelScope.launch {
            val marvelAPI = MarvelAPI.getInstance()

            try {
                val characterList = marvelAPI.getCharacters(100, null, ts = "tsor")

                // val characterList = marvelAPI.getCharactersByName("hulk", 1, null, ts = "tsor")
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
                    insertIntoDatabase(characterRoomInfo)
                }
            } catch (e: java.lang.Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    fun getRoomData() {

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
}

