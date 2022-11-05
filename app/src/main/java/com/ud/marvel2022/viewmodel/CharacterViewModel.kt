package com.ud.marvel2022.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.marvel2022.database.MarvelDatabase
import com.ud.marvel2022.model.CharacterInfoTable
import com.ud.marvel2022.model.character.*
import com.ud.marvel2022.repository.MarvelRepository
import com.ud.marvel2022.network.MarvelAPI
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
    var errorMessage: String by mutableStateOf("")

    lateinit var readAllData: LiveData<List<CharacterInfoTable>>
    lateinit var repository: MarvelRepository

    init {
        observeNetworkCall.value = false
        Log.e("BOOLEAN", "" + observeNetworkCall.value)
    }

    private fun insertIntoDatabase(character: CharacterInfoTable) {
        viewModelScope.launch {
            repository.addData(character)
        }
    }

    fun setInit(context: Context) {
        val characters = MarvelDatabase.getInstance(context).characterDao()
        Log.e("ELEMENT", "characters")
        repository = MarvelRepository(characters)
        Log.e("ELEMENT", "repo")
        readAllData = repository.readAllData

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
                    Log.e("Count", "abed")
                }
            } catch (e: java.lang.Exception) {
                errorMessage = e.message.toString()
            }
        }
    }

    fun getRoomData() {

        viewModelScope.launch {
            readAllData = repository.readAllData

            lateinit var tmpcharResp: CharacterData

            lateinit var tmpData: Data

            val apiResult = mutableListOf<ApiResult>()

            lateinit var comicItemList: MutableList<Item>

            if (readAllData.value != null) {
                readAllData.value!!.forEach {

                    Log.e("PATH", it.thumbnail.split("$$")[0] + "." + it.thumbnail.split("$$")[1])

                    comicItemList = mutableListOf()

                    var comicsList = it.comics.split("$$").forEach {
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
}

