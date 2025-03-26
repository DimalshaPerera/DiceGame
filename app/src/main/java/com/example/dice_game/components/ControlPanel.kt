package com.example.dice_game.components


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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.R
import com.example.dice_game.ui.theme.Poppins

@Composable
fun ControlPanel(
    hasThrown: Boolean,
    remainingPlayerRerolls: Int,
    inRerollMode: Boolean,
    scoringCompleted: Boolean,
    isTieBreaker: Boolean = false,
    onThrow: () -> Unit,
    onScore: () -> Unit,
    onReroll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
            // Game mascot
            Image(
                painter = painterResource(id = R.drawable.froggie),
                contentDescription = "Cute Dice Mascot",
                modifier = Modifier
                    .size(180.dp)
                    .padding(6.dp),
                contentScale = ContentScale.Fit
            )

            // Game controls
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Throw button
                CustomButton(
                    text = "Throw",
                    fontSize = 16,
                    onClick = onThrow,
                    isGradient = true,
                    width = 150,
                    height = 48
                )

                // Score button - only enabled after dice have been thrown, not in reroll mode, and scoring is not completed
                if(!isTieBreaker) {
                    CustomButton(
                        text = "Score",
                        fontSize = 16,
                        onClick = {
                            if (!isTieBreaker && hasThrown && !inRerollMode && !scoringCompleted) {
                                onScore()
                            }
//                        if (hasThrown && !inRerollMode && !scoringCompleted) {
//                            onScore()
//                        }
                        },
                        width = 150,
                        height = 47,
                        // Change the appearance when disabled
                        modifier = Modifier.alpha(if (hasThrown && !inRerollMode && !scoringCompleted) 1f else 0.5f)
                    )
                }

                // Reroll button (only visible when dice have been thrown and not in reroll mode)
//                if (hasThrown && !inRerollMode) {
                if (hasThrown && !inRerollMode && !isTieBreaker) {
                    Button(
                        onClick = onReroll,
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
                        enabled = remainingPlayerRerolls > 0,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = if (remainingPlayerRerolls > 0) "Reroll" else "No Rerolls",
                            color = if (remainingPlayerRerolls > 0) Color.White else Color.Red,
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