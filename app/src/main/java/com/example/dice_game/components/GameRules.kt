
package com.example.dice_game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.R
import com.example.dice_game.ui.theme.*


@Composable
fun GameRules(
    onWinningScoreSet: (Int) -> Unit = {},
    isTargetScoreApplied: Boolean = false,
    onTargetScoreApplied: (Boolean) -> Unit = {},

) {
    // State for target score, validation, and applied status
    val targetScore = remember { mutableStateOf("101") }
    val isValidScore = remember { mutableStateOf(true) }
    val isScoreApplied = remember { mutableStateOf(isTargetScoreApplied) }
    val isHardMode = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                CustomButton(
                    text = if (!isHardMode.value) "EASY" else "HARD",
                    onClick = { isHardMode.value = !isHardMode.value },
                    width = 150,
                    height = 50,
                    isGradient = isHardMode.value,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // Speech bubble background
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

            // Rules column inside the bubble
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 60.dp, end = 60.dp, top = 150.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                // Game rules title
                Text(
                    text = "Game Rules",
                    color = BrownDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )

                // Game rules list
                Text(
                    text = "1. Roll your dice\n" +
                            "2. Choose which to keep\n" +
                            "3. Re-roll up to 2 times per turn\n" +
                            "4. First to reach winning score wins!",
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
            text = "GOOD LUCK!",
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 200.dp, y = 30.dp)
        )

        // Target score input container at previous "Good Luck" text position
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 200.dp, y = 100.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Target Score: ",
                        color = White,
                        fontSize = 16.sp
                    )

                    BasicTextField(
                        value = targetScore.value,
                        onValueChange = { newValue ->
                            // Filter to allow only numeric input
                            val filteredValue = newValue.filter { it.isDigit() }
                            targetScore.value = filteredValue

                            // Validate score is between 30 and 1000
                            val scoreValue = filteredValue.toIntOrNull() ?: 0
                            isValidScore.value = scoreValue in 30..1000

                            // Reset applied state when score changes
                            isScoreApplied.value = false
                            onTargetScoreApplied(false)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        cursorBrush = SolidColor(White),
                        textStyle = androidx.compose.ui.text.TextStyle(color = White, fontSize = 16.sp),
                        modifier = Modifier
                            .width(60.dp)
                            .border(
                                width = 1.dp,
                                color = if (isValidScore.value) White else Color.Red,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(4.dp)
                    )
                }

                // Apply button (only shown for valid scores)
                if (isValidScore.value && targetScore.value.isNotEmpty()) {
                    Text(
                        text = "Apply",
                        color = BrownPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.End)
                            .clickable {
                                val score = targetScore.value.toIntOrNull() ?: 101
                                onWinningScoreSet(score)
                                isScoreApplied.value = true
                                onTargetScoreApplied(true)
                            }
                    )
                }

                // Error message for invalid score
                if (!isValidScore.value) {
                    Text(
                        text = "Score must be between 30 and 1000",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Good luck text in one line under witch
        Text(
            text = "Apply your target score & Click throw",
            color = White,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 30.dp, y = -200.dp)
                .padding(bottom = 20.dp)
        )
    }
}
