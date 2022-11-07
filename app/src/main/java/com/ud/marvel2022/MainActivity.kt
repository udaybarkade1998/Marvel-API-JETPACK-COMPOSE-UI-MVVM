package com.ud.marvel2022

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ud.marvel2022.model.roomTable.BookmarkData
import com.ud.marvel2022.model.character.CharacterData
import com.ud.marvel2022.ui.theme.Marvel2022Theme
import com.ud.marvel2022.view.CharacterItem
import com.ud.marvel2022.viewmodel.CharacterViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {


    private val characterViewModel by viewModels<CharacterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun App(){
        val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Open))
        Scaffold(
            backgroundColor = Color.Black,
            scaffoldState = scaffoldState,
            topBar = {

                Row {
                    Image(
                        painterResource(R.drawable.logo),
                        contentDescription = "",
                        modifier = Modifier
                            .padding(10.dp)
                            .width(100.dp)
                            .height(50.dp)
                    )
                    SearchView(characterViewModel)
                }
            },

            content = {
                var refreshing by remember { mutableStateOf(false) }
                LaunchedEffect(refreshing) {
                    if (refreshing) {
                        characterViewModel.getCharacterListFromAPI()
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

                            characterViewModel.initData(LocalContext.current.applicationContext)


                            //observing character data changes to update state
                            characterViewModel.readAllData.observe(this) {
                                if (characterViewModel.readAllData.value?.isEmpty() == true)
                                    characterViewModel.getCharacterListFromAPI()
                                else {
                                    if (characterViewModel.searchState)
                                        characterViewModel.searchState = true
                                    else
                                        characterViewModel.getLocalDatabaseData()

                                    if (!characterViewModel.observeNetworkCall.value!!) {
                                        characterViewModel.getCharacterListFromAPI()
                                        characterViewModel.observeNetworkCall.value = true
                                    }
                                }
                            }

                            //observing bookmark data changes to update state
                            characterViewModel.bookmarkAllData.observe(this) {
                                characterViewModel.getBookmarkedRoomData()
                            }

                            //call to show Character List
                            CharacterList(
                                characterViewModel.characterListResponse,
                                characterViewModel,
                                characterViewModel.bookmarkListResponse
                            )
                        }
                    }
                }
            })
    }


    //function display list of characters
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
                            if (it.id == item.id)
                                bookmarked = true
                        }
                        CharacterItem(
                            character = item,
                            context = LocalContext.current,
                            viewModel,
                            bookmarked
                        )
                    }
                }
        }
    }
}

@Composable
fun SearchView(viewModel: CharacterViewModel) {

    var query: String by rememberSaveable { mutableStateOf("") }

    TextField(
        value = query,
        onValueChange = { onQueryChanged ->
            query = onQueryChanged
            if (onQueryChanged.isNotEmpty()) {
                viewModel.getSearchedCharacters(query.trim())
                viewModel.searchState = true
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                tint = MaterialTheme.colors.onBackground,
                contentDescription = "Search icon"
            )
        },
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent, focusedIndicatorColor = Color.Transparent),
        placeholder = { Text(text = stringResource(R.string.hint_search_query)) },
        textStyle = MaterialTheme.typography.subtitle1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier
            .padding(10.dp)
            .background(color = MaterialTheme.colors.background, shape = RoundedCornerShape(20))
            .height(50.dp)
            ,
    )

}
