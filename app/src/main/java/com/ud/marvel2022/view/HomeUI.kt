package com.ud.marvel2022.view

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.ud.marvel2022.CharacterInformation
import com.ud.marvel2022.model.character.ApiResult


@Composable
fun CharacterItem(character: ApiResult, index: Int, context: Context) {
    Card(
        modifier = Modifier
            .padding(8.dp, 10.dp)
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        val intent = Intent(context, CharacterInformation::class.java)
                        intent.putExtra("character", character)
                        context.startActivity(intent)
                    }
            ) {

                Box(modifier = Modifier.fillMaxSize()) {


                    //Marvel character image
                    val imageUrl = character.thumbnail.path + "." + character.thumbnail.extension
                    Log.e("PathIN IMAGE", imageUrl)
                    Image(
                        contentDescription = character.description,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize(),
                        painter = rememberImagePainter(data = imageUrl,
                            builder = {
                                scale(Scale.FIT)
                                placeholder(com.ud.marvel2022.R.drawable.ic_launcher_background)
                            })
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomStart
                    ) {

                        // Creating a Vertical Gradient Color
                        val gradientGrayWhite = Brush.verticalGradient(0f to Color.Transparent, 1500f to Color.Black)

                        //Marvel character name
                        Text(
                            text = character.name,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(gradientGrayWhite)
                                .padding(0.dp,10.dp)
                                .fillMaxWidth()
                        )
                    }
                }

            }
        }
    }
}