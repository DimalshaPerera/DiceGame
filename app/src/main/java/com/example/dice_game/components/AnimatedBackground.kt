package com.example.dice_game.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.dice_game.R

@Composable
fun AnimatedImageSequenceBackground() {
    // Define the list of image resource IDs (a_out0001, a_out0002, ..., a_out0015)
    val imageResources = (1..15).map {
        // Dynamically retrieve each image resource ID from R.drawable using reflection

        R.drawable::class.java.getField("a_out${it.toString().padStart(4, '0')}").getInt(null)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "animation")
    val frameIndex by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = imageResources.size - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animation"
    )

    Image(
        painter = painterResource(id = imageResources[frameIndex]),
        contentDescription = "Animated Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

//@Composable
//fun AnimatedImageSequenceBackground() {
//    val imageResources = listOf(
//        R.drawable.a_out0001,
//        R.drawable.a_out0002,
//        R.drawable.a_out0003,
//        R.drawable.a_out0004,
//        R.drawable.a_out0005,
//        R.drawable.a_out0006,
//        R.drawable.a_out0007,
//        R.drawable.a_out0008,
//        R.drawable.a_out0009,
//        R.drawable.a_out0010,
//        R.drawable.a_out0011,
//        R.drawable.a_out0012,
//        R.drawable.a_out0013,
//        R.drawable.a_out0014,
//        R.drawable.a_out0015,
//    )
//
//    // Create an infinite transition
//    val infiniteTransition = rememberInfiniteTransition(label = "animation")
//
//    // Animate the frame index
//    val frameIndex by infiniteTransition.animateValue(
//        initialValue = 0,
//        targetValue = imageResources.size - 1,
//        typeConverter = Int.VectorConverter,
//        animationSpec = infiniteRepeatable(
//            animation = keyframes {
//                durationMillis = 1500
//                0 at 0 using LinearEasing
//                1 at 100 using LinearEasing
//                2 at 200 using LinearEasing
//                3 at 300 using LinearEasing
//                4 at 400 using LinearEasing
//                5 at 500 using LinearEasing
//                6 at 600 using LinearEasing
//                7 at 700 using LinearEasing
//                8 at 800 using LinearEasing
//                9 at 900 using LinearEasing
//                10 at 1000 using LinearEasing
//                11 at 1100 using LinearEasing
//                12 at 1200 using LinearEasing
//                13 at 1300 using LinearEasing
//                14 at 1400 using LinearEasing
//            },
//            repeatMode = RepeatMode.Restart
//        ), label = "animation"
//    )
//
//    // Get the current image resource
//    val currentImage = imageResources[frameIndex]
//
//    // Display the current image as full-screen background
//    Image(
//        painter = painterResource(id = currentImage),
//        contentDescription = "Animated Background",
//        modifier = Modifier.fillMaxSize(),
//        contentScale = ContentScale.Crop
//    )
//}
