package com.example.dice_game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.dice_game.R

@Composable
fun GameScreenBackground(){
    val backgroundImage= painterResource(id= R.drawable.mountains)
    Box(
        modifier = Modifier.fillMaxSize()
    )
    {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),

            contentScale = ContentScale.FillBounds
        )
    }

}