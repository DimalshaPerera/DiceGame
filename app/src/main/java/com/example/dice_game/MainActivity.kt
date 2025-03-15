
package com.example.dice_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.ui.theme.DiceGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceGameTheme {
                GUI()
            }
        }
    }
}



@Composable
fun GUI() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background using images
        AnimatedImageSequenceBackground()


//        Text(
//            "DICEY",
//            modifier = Modifier
//                .align(Alignment.Center)
//                .offset(y = (100).dp),
//            color = androidx.compose.ui.graphics.Color.White,
//            fontSize = 70.sp,
//            fontWeight = FontWeight.Bold,
//            fontFamily = FontFamily(Font(R.font.noto_sans_kr_bold))
//        )

        AnimatedTitle()

//        Button(
//            onClick = {  },
//            modifier = Modifier
//                .align(Alignment.Center)
//                .offset(y = (180).dp) //
//        ) {
//            Text(text = "New Game", fontSize = 20.sp)
//        }


        Button(
            onClick = { },
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (250).dp)
        ) {
            Text(text = "About", fontSize = 20.sp)
        }


    }
}

@Composable
fun AnimatedImageSequenceBackground() {

    val imageResources = listOf(

          R.drawable.a_out0001,
          R.drawable.a_out0002,
          R.drawable.a_out0003,
        R.drawable.a_out0004,
        R.drawable.a_out0005,
        R.drawable.a_out0006,
        R.drawable.a_out0007,
        R.drawable.a_out0008,
        R.drawable.a_out0009,
        R.drawable.a_out0010,
        R.drawable.a_out0011,
        R.drawable.a_out0012,
        R.drawable.a_out0013,
        R.drawable.a_out0014,
        R.drawable.a_out0015,


    )

    // Create an infinite transition
    val infiniteTransition = rememberInfiniteTransition(label = "animation")

    // Animate the frame index
    val frameIndex by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = imageResources.size - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1500
                0 at 0 using LinearEasing
                1 at 100 using LinearEasing
                2 at 200 using LinearEasing
                3 at 300 using LinearEasing
                4 at 400 using LinearEasing
                5 at 500 using LinearEasing
                6 at 600 using LinearEasing
                7 at 700 using LinearEasing
                8 at 800 using LinearEasing
                9 at 900 using LinearEasing
                10 at 1000 using LinearEasing
                11 at 1100 using LinearEasing
                12 at 1200 using LinearEasing
                13 at 1300 using LinearEasing
                14 at 1400 using LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = "animation"
    )

    // Get the current image resource
    val currentImage = imageResources[frameIndex]

    // Display the current image as full-screen background
    Image(
        painter = painterResource(id = currentImage),
        contentDescription = "Animated Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
    @Composable
    fun AnimatedTitle() {
        val scale by rememberInfiniteTransition(label = "title animation").animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "scale animation"
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                "DICEY",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 100.dp)
                    .scale(scale),
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 70.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.noto_sans_kr_bold))
            )
        }
    }


