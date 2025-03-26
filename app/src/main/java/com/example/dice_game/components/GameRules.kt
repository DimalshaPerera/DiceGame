package com.example.dice_game.components



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.R
import com.example.dice_game.ui.theme.BrownDark
import com.example.dice_game.ui.theme.DarkGray
import com.example.dice_game.ui.theme.White

@Composable
fun GameRules() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .width(900.dp)
                .height(1000.dp)
                .align(Alignment.TopCenter)
                .offset(x = 40.dp, y = (-280).dp)
                .padding(top = 220.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bubble),
                contentDescription = "Speech Bubble",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 60.dp, end = 60.dp, top = 150.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Game Rules",
                    color = BrownDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Text(
                    text = "1. Roll your dice\n" +
                            "2. Choose which to keep\n" +
                            "3. Re-roll up to 2 times per turn\n" +
                            "4. First to score more wins!"
                          ,
                    color = DarkGray,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }

        // Witch character image
        Image(
            painter = painterResource(id = R.drawable.witch),
            contentDescription = "Witch Guide",
            modifier = Modifier
                .size(560.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-100).dp)
                .padding(bottom = 210.dp)
        )


        Text(
            text = "GOOD LUCK! \n\nClick the throw \nbutton to start",
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 200.dp, y = 90.dp)
        )
    }
}