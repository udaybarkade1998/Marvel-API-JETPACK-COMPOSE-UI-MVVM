package com.ud.marvel2022.repository

import androidx.lifecycle.LiveData
import com.ud.marvel2022.database.CharacterInfoDAO
import com.ud.marvel2022.model.CharacterInfoTable

class MarvelRepository(private val characterInfoDAO: CharacterInfoDAO) {

    val readAllData: LiveData<List<CharacterInfoTable>> = characterInfoDAO.getAll()

    suspend fun addData(item : CharacterInfoTable){
        characterInfoDAO.insert(item)
    }
}