package com.ud.marvel2022

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.ExposedDropdownMenuDefaults.textFieldColors
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                                        if (characterViewModel.searchState)
                                            characterViewModel.searchState = true
                                        else
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

                                CharacterList(
                                    characterViewModel.characterListResponse,
                                    characterViewModel,
                                    characterViewModel.bookmarkListResponse
                                )
                            }
                        }
                    }
                }
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
    val showClearIcon by rememberSaveable { mutableStateOf(false) }


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