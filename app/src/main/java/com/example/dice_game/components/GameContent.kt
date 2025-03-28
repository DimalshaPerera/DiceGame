package com.example.dice_game.components



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.data.GameState

import com.example.dice_game.ui.theme.DarkGray
import com.example.dice_game.ui.theme.LightTransparentWhite
import com.example.dice_game.ui.theme.White

@Composable
fun GameContent(
    playerDice: List<Int>,
    computerDice: List<Int>,
    playerRerolls: Int,
    computerRerolls: Int,
    computerDiceThrown: Boolean,
    inRerollMode: Boolean,
    playerScore: Int,
    computerScore: Int,
    selectedDice: List<Boolean>,
    onDiceSelected: (Int) -> Unit
) {
    // Score display
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp)
            .padding(horizontal = 16.dp)
            .height(70.dp)
            .background(
                color = LightTransparentWhite,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            text = "Human : $playerScore ",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(13.dp),
            color = DarkGray
        )
        Text(
            text = "Computer : $computerScore ",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(13.dp),
            color = DarkGray
        )
    }

    // Dice section headers
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 200.dp)
            .padding(horizontal = 16.dp)
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

    // Dice displays
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .padding(top = 250.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Player dice column
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            playerDice.forEachIndexed { index, value ->
                SelectableDiceImage(
                    value = value,
                    isSelected = selectedDice[index],
                    onClick = { onDiceSelected(index) },
                    selectable = inRerollMode  // Only allow selection when in reroll mode
                )
            }
        }

        // Computer dice column
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            if (computerDiceThrown) {
                computerDice.forEach { value ->
                    DiceImage(value = value)
                }
            } else {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                color = LightTransparentWhite,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
        }
    }

    // Reroll information
    Column(
        modifier = Modifier
            .padding(top = 670.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when {
            inRerollMode -> {
                Text(
                    text = "Tap dice to keep them, then press the throw",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            playerRerolls > 0 && !inRerollMode -> {
                Text(
                    text = "Press 'Reroll' to select dice to keep",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        Text(
            text = if (playerRerolls > 0) "$playerRerolls rerolls remaining" else "No rerolls left",
            color = if (playerRerolls > 0) Color.White else Color.Red,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}