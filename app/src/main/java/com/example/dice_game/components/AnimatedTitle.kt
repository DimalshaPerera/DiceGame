//package com.example.dice_game.components
//
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.offset
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.dice_game.ui.theme.NotoSansKr
//
//@Composable
//fun AnimatedTitle() {
//    val scale by rememberInfiniteTransition(label = "title animation").animateFloat(
//        initialValue = 1f,
//        targetValue = 1.1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(800, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ), label = "scale animation"
//    )
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Text(
//            "DICEY",
//            modifier = Modifier
//                .align(Alignment.Center)
//                .offset(y = 100.dp)
//                .scale(scale),
//            color = Color.White,
//            fontSize = 70.sp,
//            fontWeight = FontWeight.Bold,
//            fontFamily = NotoSansKr
//        )
//    }
//}
package com.example.dice_game.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.ui.theme.NotoSansKr

@Composable
fun AnimatedTitle(
    isGameScreen: Boolean = false
) {
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
                .align(if (isGameScreen) Alignment.TopCenter else Alignment.Center)
                .padding(top = if (isGameScreen) 20.dp else 0.dp)
                .offset(y = if (isGameScreen) 0.dp else 100.dp)
                .scale(scale),
            color = Color.White,
            fontSize = if (isGameScreen) 40.sp else 70.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = NotoSansKr
        )
    }
}