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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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
            val materialBlue700 = Color(0xFF1976D2)
            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Open))
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    TopAppBar(
                        title = { Text("Marvel Universe", color = Color.White) },
                        backgroundColor = Color.Black,
                    )


                },
                floatingActionButtonPosition = FabPosition.End,
                floatingActionButton = {
                    FloatingActionButton(onClick = {}) {
                        Text("Search")
                    }
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

                                val characterList = characterViewModel.characterListResponse
                                characterViewModel.setInit(LocalContext.current.applicationContext)
                                characterViewModel.readAllData.observe(this,
                                    Observer {
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
                                )
                                CharacterList(characterViewModel.characterListResponse)
                            }
                        }
                    }
                },
                bottomBar = { BottomAppBar(backgroundColor = Color.Black) { Text("BottomAppBar") } }
            )
        }
    }

    @Composable
    fun TopBar() {
        TopAppBar(
            title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp) },
            backgroundColor = colorResource(id = R.color.black),
            contentColor = Color.White
        )
    }

    @Composable
    fun CharacterList(characterList: CharacterData) {

        val characters = characterList.data?.results

        LazyColumn {

            characterList.data?.let {
                itemsIndexed(items = it.results) { index, item ->
                    CharacterItem(character = item, index, context = LocalContext.current)
                }
            }
        }
    }
}

@Composable
fun SwipeRefreshCompose() {

    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(3000)
            refreshing = false
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = { refreshing = true },
    ) {


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