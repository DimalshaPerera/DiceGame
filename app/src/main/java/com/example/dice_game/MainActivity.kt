package com.example.dice_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.ui.theme.DiceGameTheme

// Define font families outside of composable functions
val poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val notoSansKr = FontFamily(
    Font(R.font.noto_sans_kr_bold, FontWeight.Bold)
)

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

        AnimatedTitle()

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp,
                pressedElevation = 100.dp
            ),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (200).dp)
                .height(60.dp)
                .width(230.dp)
                .fillMaxWidth(0.6f) // Width as percentage of parent
        ) {
            Text(
                text = "New Game",
                fontSize = 20.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
        }

        Button(
            onClick = { },
            contentPadding = PaddingValues(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 2.dp
            ),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (280).dp)
                .height(60.dp)
                .width(230.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8B4513), // Brown color at 0% (from your image)
                            Color(0xFF6B3410)  // Darker brown at 100% (from your image)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    ),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Text(
                text = "About",
                fontSize = 20.sp,
                fontFamily = poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
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
            color = Color.White,
            fontSize = 70.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = notoSansKr
        )
    }
}