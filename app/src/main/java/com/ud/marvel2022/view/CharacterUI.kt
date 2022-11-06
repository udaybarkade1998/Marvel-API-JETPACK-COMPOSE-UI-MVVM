package com.ud.marvel2022.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.ud.marvel2022.R
import com.ud.marvel2022.model.character.ApiResult


// Creating a Vertical Gradient Color
val gradientGrayWhite =
    Brush.verticalGradient(0f to Color.Transparent, 1500f to Color.Black)

@Composable
fun CharacterUI(character: ApiResult?) {
    Surface(color = Color.Black) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            GetCharacterImageData(character = character!!)

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                GetDescription(character = character)
                GetComics(character)
            }
        }
    }
}

@Composable
fun GetCharacterImageData(character: ApiResult) {
    Card(
        modifier = Modifier
            .padding(8.dp, 10.dp)
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            //Marvel character image
            val imageUrl = character.thumbnail.path + "." + character.thumbnail.extension
            Image(
                contentDescription = character.description,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(),
                painter = rememberImagePainter(data = imageUrl,
                    builder = {
                        scale(Scale.FIT)
                        placeholder(R.drawable.ic_launcher_background)
                    })
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {

                //Marvel character name
                Text(
                    text = character.name,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(gradientGrayWhite)
                        .padding(0.dp, 10.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun GetDescription(character: ApiResult) {
    Text(
        text = character.description,
        modifier = Modifier.padding(20.dp, 20.dp),
        fontSize = 18.sp,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Light,
        color = Color.Gray
    )
}

@Composable
fun GetComics(character: ApiResult) {
    Column(modifier = Modifier.padding(20.dp, 20.dp)) {
        val comics = character.comics!!.items

        val comicSize = if (comics.size > 5) 4 else comics.size - 1

        Text(text = "Related Comics ", color = Color.White, fontSize = 20.sp)

        for (i in 0..comicSize) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                backgroundColor = Color.Gray
            ) {

                Text(
                    text = comics[i].name, color = Color.White, textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier.background(gradientGrayWhite)
                )
            }

        }
    }
}