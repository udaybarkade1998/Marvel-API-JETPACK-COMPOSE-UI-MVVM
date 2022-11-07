package com.ud.marvel2022

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.ud.marvel2022.model.character.ApiResult
import com.ud.marvel2022.ui.theme.Marvel2022Theme
import com.ud.marvel2022.view.CharacterUI

//activity used to show all data related to character
class CharacterInformation : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Marvel2022Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val character = intent.getParcelableExtra("character") as ApiResult?
                    if (character != null) {
                        CharacterUI(character = character)
                    }
                }
            }
        }
    }
}
