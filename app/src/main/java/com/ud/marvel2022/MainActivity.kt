package com.ud.marvel2022

import android.annotation.SuppressLint
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ud.marvel2022.model.BookmarkData
import com.ud.marvel2022.model.character.CharacterData
import com.ud.marvel2022.ui.theme.Marvel2022Theme
import com.ud.marvel2022.view.CharacterItem
import com.ud.marvel2022.viewmodel.CharacterViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {


    private val characterViewModel by viewModels<CharacterViewModel>()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Open))
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    TopAppBar(
                        title = { Text("Marvel Universe", color = Color.White) },
                        backgroundColor = Color.Black,
                    )


                },

                content = {
                    var refreshing by remember { mutableStateOf(false) }
                    LaunchedEffect(refreshing) {
                        if (refreshing) {
                            characterViewModel.getCharacterList()
                            delay(1000)
                            refreshing = false
                        }
                    }

                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing = refreshing),
                        onRefresh = {
                            refreshing = true
                        },
                    ) {
                        Marvel2022Theme {

                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color.Black
                            ) {

                                characterViewModel.setInit(LocalContext.current.applicationContext)


                                characterViewModel.readAllData.observe(this) {
                                    if (characterViewModel.readAllData.value?.isEmpty() == true)
                                        characterViewModel.getCharacterList()
                                    else {
                                        characterViewModel.getRoomData()
                                        if (!characterViewModel.observeNetworkCall.value!!) {
                                            characterViewModel.getCharacterList()
                                            characterViewModel.observeNetworkCall.value = true
                                        }
                                    }
                                }

                                characterViewModel.bookmarkAllData.observe(this) {
                                    characterViewModel.getBookmarkedRoomData()
                                }

                                CharacterList(characterViewModel.characterListResponse,
                                    characterViewModel,
                                    characterViewModel.bookmarkListResponse)
                            }
                        }
                    }
                },
                bottomBar = { BottomAppBar(backgroundColor = Color.Black) { Text("BottomAppBar") } }
            )
        }
    }


    @Composable
    fun CharacterList(
        characterList: CharacterData,
        viewModel: CharacterViewModel,
        bookmarkAllData: BookmarkData
    ) {
        LazyColumn {

                characterList.data?.let { it ->
                    itemsIndexed(items = it.results) { index, item ->

                        var bookmarked = false
                        bookmarkAllData.bookmarks!!.forEach {
                            if(it.id == item.id)
                                bookmarked = true
                        }
                        CharacterItem(character = item,  context = LocalContext.current,viewModel,bookmarked)
                    }
            }


        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Marvel2022Theme {
        Greeting(name = "Uday")
    }
}