package com.example.dice_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.components.AnimatedTitle
import com.example.dice_game.components.CustomButton
import com.example.dice_game.components.GameScreenBackground
import com.example.dice_game.ui.theme.*
import com.example.dice_game.ui.theme.LightTransparentWhite

class GameScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceGameTheme {

                    Game()
                }

            }
        }
    }




@Composable
fun Game(){
   GameScreenBackground()
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated title at the top
        AnimatedTitle(isGameScreen = true)


        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .fillMaxWidth(0.9f)
                .height(70.dp)
                .background(

                    color = LightTransparentWhite ,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Text(
                text = "Human : 0 ",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(13.dp),
                color = DarkGray
            )
            Text(
                text = "Computer : 0 ",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(13.dp),
                color = DarkGray
            )
        }



        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 200.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "YOUR DICE",
                modifier = Modifier
                    .align(Alignment.CenterStart),
                color = White
            )

            Text(
                text = "COMPUTER'S DICE",
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                color = White,



            )
        }
        // Dice display
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 250.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player dice column (left side)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                DiceImage(value = 6)
                DiceImage(value = 3)
                DiceImage(value = 4)
                DiceImage(value = 2)
                DiceImage(value = 5)
            }

            // Computer dice column (right side)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                DiceImage(value = 1)
                DiceImage(value = 4)
                DiceImage(value = 3)
                DiceImage(value = 6)
                DiceImage(value = 2)
            }
        }
        // Bottom control section with buttons and image
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(210.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.froggie),
                    contentDescription = "Cute Dice Mascot",
                    modifier = Modifier
                        .size(180.dp)
                        .padding(6.dp),
                    contentScale = ContentScale.Fit
                )


                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Throw button
//                    Button(
//                        onClick = {  },
//                        modifier = Modifier
//                            .width(150.dp)
//                            .height(40.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFF6200EE) // Purple
//                        )
//                    ) {
//                        Text(
//                            text = "Throw",
//                            color = Color.White
//                        )
//                    }
                    CustomButton(
                        text="Throw",
                        fontSize = 16,
                        onClick = {},
                        isGradient = true,
                        width = 150,
                        height = 48


                    )
                    CustomButton(
                        text="Score",
                        fontSize = 16,
                        onClick = {},
                        width = 150,
                        height = 47



                        )


//                    // Score button
//                    Button(
//                        onClick = { },
//                        modifier = Modifier
//                            .width(150.dp)
//                            .height(40.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFF4CAF50) // Green
//                        )
//                    ) {
//                        Text(
//                            text = "Score",
//                            color = Color.White
//                        )
//                    }

                    // Reroll button
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .width(150.dp)
                            .height(40.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF8BC34A),
                                        Color(0xFF7CB342)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, Float.POSITIVE_INFINITY)
                                ),
                                shape = RoundedCornerShape(50.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "Reroll",
                            color = Color.White,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiceImage(value: Int) {

    val diceImageResId = when (value) {
        1 -> R.drawable.d_1
        2 -> R.drawable.d_2
        3 -> R.drawable.d_3
        4 -> R.drawable.d_4
        5 -> R.drawable.d_5
        6 -> R.drawable.d_6
        else -> R.drawable.d_1
    }

    Image(
        painter = painterResource(id = diceImageResId),
        contentDescription = "Dice showing $value",
        modifier = Modifier.size(70.dp)
    )

}
