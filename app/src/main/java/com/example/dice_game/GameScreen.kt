

package com.example.dice_game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.components.AnimatedTitle
import com.example.dice_game.components.CustomButton
import com.example.dice_game.components.GameRules
import com.example.dice_game.components.GameScreenBackground
import com.example.dice_game.data.GameState
import com.example.dice_game.ui.theme.DarkGray
import com.example.dice_game.ui.theme.DiceGameTheme
import com.example.dice_game.ui.theme.LightTransparentWhite
import com.example.dice_game.ui.theme.Poppins
import com.example.dice_game.ui.theme.White

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
fun Game() {
    // Game state
    val gameState = remember { mutableStateOf(GameState()) }
    val remainingPlayerRerolls = remember { mutableStateOf(2) }
    val remainingComputerRerolls = remember { mutableStateOf(2) }
    val computerDiceThrown = remember { mutableStateOf(false) }
    val inRerollMode = remember { mutableStateOf(false) }
    // Add a state to track the current turn
    val currentTurn = remember { mutableStateOf(1) }

    // Handler for delayed actions
    val handler = remember { Handler(Looper.getMainLooper()) }

    // Background
    GameScreenBackground()

    // Main game content
    Box(modifier = Modifier.fillMaxSize()) {
        // Score display
        Text(
            text = "H:0/C:0",
            modifier = Modifier.padding(20.dp),
            color = White,
            fontSize = 20.sp
        )

        AnimatedTitle(isGameScreen = true)

        // Game content area
        if (gameState.value.hasThrown) {
            GameContent(
                gameState = gameState.value,
                playerRerolls = remainingPlayerRerolls.value,
                computerRerolls = remainingComputerRerolls.value,
                computerDiceThrown = computerDiceThrown.value,
                inRerollMode = inRerollMode.value,
                onDiceSelected = { index ->
                    // Only allow dice selection when in reroll mode
                    if (inRerollMode.value) {
                        val updatedSelectedDice = gameState.value.selectedDice.toMutableList()
                        updatedSelectedDice[index] = !updatedSelectedDice[index]
                        gameState.value = gameState.value.copy(selectedDice = updatedSelectedDice)
                    }
                }
            )
        } else {
            GameRules()
        }

        // Bottom control panel
        ControlPanel(
            hasThrown = gameState.value.hasThrown,
            remainingPlayerRerolls = remainingPlayerRerolls.value,
            inRerollMode = inRerollMode.value,
            onThrow = {
                if (!gameState.value.hasThrown) {
                    // First throw - Reset game state for new game and start a new turn
                    computerDiceThrown.value = false
                    remainingPlayerRerolls.value = 2
                    remainingComputerRerolls.value = 2
                    inRerollMode.value = false
                    currentTurn.value = 1

                    // Generate new dice values
                    val updatedPlayerDice = List(5) { (1..6).random() }
                    val generatedComputerDice = List(5) { (1..6).random() }

                    // Initial throw - no dice are selected
                    val initialSelectedState = List(5) { false }

                    // Update game state
                    gameState.value = gameState.value.copy(
                        playerDice = updatedPlayerDice,
                        computerDice = generatedComputerDice,
                        hasThrown = true,
                        selectedDice = initialSelectedState
                    )

                    // Show computer dice after delay
                    handler.postDelayed({
                        computerDiceThrown.value = true

                        // Computer makes reroll decision after another delay
                        handler.postDelayed({
                            computerTurn(
                                gameState = gameState,
                                remainingRerolls = remainingComputerRerolls,
                                handler = handler
                            )
                        }, 300)
                    }, 300)
                } else if (inRerollMode.value) {
                    // We're in reroll mode - reroll only unselected dice
                    val currentDice = gameState.value.playerDice
                    val selectedDice = gameState.value.selectedDice

                    // Generate new values for unselected dice, keep selected dice unchanged
                    val newDice = currentDice.mapIndexed { index, value ->
                        if (selectedDice[index]) {
                            // Keep this die as it's selected
                            value
                        } else {
                            // Reroll this die with a new random value
                            (1..6).random()
                        }
                    }

                    // Update game state with new dice values
                    gameState.value = gameState.value.copy(
                        playerDice = newDice,
                        // Reset selection state after reroll
                        selectedDice = List(5) { false }
                    )

                    // Exit reroll mode
                    inRerollMode.value = false

                    // Computer turn after delay
                    handler.postDelayed({
                        computerTurn(
                            gameState = gameState,
                            remainingRerolls = remainingComputerRerolls,
                            handler = handler
                        )
                    }, 500)
                } else {
                    // If dice have already been thrown and not in reroll mode, start a new turn
                    // Reset rerolls for new turn
                    remainingPlayerRerolls.value = 2
                    remainingComputerRerolls.value = 2
                    currentTurn.value += 1

                    // Generate completely new dice values for player
                    val updatedPlayerDice = List(5) { (1..6).random() }
                    val updatedComputerDice = List(5) { (1..6).random() }

                    // Reset selection state
                    val initialSelectedState = List(5) { false }

                    // Update game state
                    gameState.value = gameState.value.copy(
                        playerDice = updatedPlayerDice,
                        computerDice = updatedComputerDice,
                        selectedDice = initialSelectedState
                    )

                    // Show computer dice after delay
                    handler.postDelayed({
                        computerDiceThrown.value = true

                        // Computer turn after delay
                        handler.postDelayed({
                            computerTurn(
                                gameState = gameState,
                                remainingRerolls = remainingComputerRerolls,
                                handler = handler
                            )
                        }, 300)
                    }, 300)
                }
            },
            onScore = {
                // Score calculation logic will be implemented here
            },
            onReroll = {
                if (remainingPlayerRerolls.value > 0 && !inRerollMode.value) {
                    // Enter reroll mode - allow player to select dice
                    inRerollMode.value = true

                    // Reset selection state to prepare for new selections
                    gameState.value = gameState.value.copy(
                        selectedDice = List(5) { false }
                    )

                    // Decrement remaining rerolls immediately when entering reroll mode
                    remainingPlayerRerolls.value--
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun ControlPanel(
    hasThrown: Boolean,
    remainingPlayerRerolls: Int,
    inRerollMode: Boolean,
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
                // Throw button - keep label consistent
                CustomButton(
                    text = if (inRerollMode) "Reroll Selected" else "Throw",
                    fontSize = 16,
                    onClick = onThrow,
                    isGradient = true,
                    width = 150,
                    height = 48
                )

                // Score button
                CustomButton(
                    text = "Score",
                    fontSize = 16,
                    onClick = onScore,
                    width = 150,
                    height = 47
                )

                // Reroll button (only visible when dice have been thrown and not in reroll mode)
                if (hasThrown && !inRerollMode) {
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
                            text = "Reroll",
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
/**
 * Handles the computer's turn logic
 */
private fun computerTurn(
    gameState: androidx.compose.runtime.MutableState<GameState>,
    remainingRerolls: androidx.compose.runtime.MutableState<Int>,
    handler: Handler
) {
    Log.d("DiceGame", "Computer turn started. Remaining rerolls: ${remainingRerolls.value}")

    // Only allow computer to reroll if it has remaining rerolls
    if (remainingRerolls.value > 0) {
        // Random decision whether to reroll (50% chance)
        val willReroll = (0..1).random() == 1
        Log.d("DiceGame", "Computer decided to reroll: $willReroll")

        if (willReroll) {
            val oldDice = gameState.value.computerDice
            val newDice = List(5) { (1..6).random() }
            Log.d("DiceGame", "Computer rerolled. Old dice: $oldDice, New dice: $newDice")

            // Update computer dice with new random values
            gameState.value = gameState.value.copy(
                computerDice = newDice
            )

            // Decrement computer rerolls
            remainingRerolls.value--
            Log.d("DiceGame", "Computer rerolls left: ${remainingRerolls.value}")

            // Check if computer wants to do a second reroll after a delay
            if (remainingRerolls.value > 0) {
                handler.postDelayed({
                    val willRerollAgain = (0..1).random() == 1
                    if (willRerollAgain) {
                        gameState.value = gameState.value.copy(
                            computerDice = List(5) { (1..6).random() }
                        )
                        remainingRerolls.value--
                    }
                }, 300) // 1 second delay before second reroll decision
            } else {
                Log.d("DiceGame", "Computer has no rerolls left")
            }
        }
    }
}


@Composable
fun GameContent(
    gameState: GameState,
    playerRerolls: Int,
    computerRerolls: Int,
    computerDiceThrown: Boolean,
    inRerollMode: Boolean,  // Added parameter
    onDiceSelected: (Int) -> Unit
) {
    // Score display
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 100.dp)
            .height(70.dp)
            .background(
                color = LightTransparentWhite,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            text = "Human : ${gameState.playerScore} ",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(13.dp),
            color = DarkGray
        )
        Text(
            text = "Computer : ${gameState.computerScore} ",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(13.dp),
            color = DarkGray
        )
    }

    // Dice section headers
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 200.dp)
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
            .fillMaxWidth(0.9f)
            .padding(top = 250.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Player dice column
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            gameState.playerDice.forEachIndexed { index, value ->
                SelectableDiceImage(
                    value = value,
                    isSelected = gameState.selectedDice[index],
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
                gameState.computerDice.forEach { value ->
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

    // Reroll information and help text
    Column(
        modifier = Modifier
            .padding(top = 670.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show selection instructions only when in reroll mode
        if (inRerollMode) {
            Text(
                text = "Tap dice to keep them, then press 'Reroll Selected'",
                color = Color.White,
                fontSize = 14.sp
            )
        } else if (playerRerolls > 0 && !inRerollMode) {
            Text(
                text = "Press 'Reroll' to select dice to keep",
                color = Color.White,
                fontSize = 14.sp
            )
        }

        Text(
            text = if (playerRerolls > 0) "$playerRerolls rerolls remaining" else "No rerolls left",
            color = if (playerRerolls > 0) Color.White else Color.Red,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
@Composable
fun SelectableDiceImage(
    value: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectable: Boolean = true  // Added parameter with default value
) {
    val diceImageResId = when (value) {
        1 -> R.drawable.d_1
        2 -> R.drawable.d_2
        3 -> R.drawable.d_3
        4 -> R.drawable.d_4
        5 -> R.drawable.d_5
        6 -> R.drawable.d_6
        else -> R.drawable.d_1
    }

    val selectionColor = Color(0xFF4CAF50) // Green color for selection
    val borderWidth = if (isSelected) 3.dp else 0.dp

    // Determine if dice should be clickable based on selectable parameter
    val clickModifier = if (selectable) {
        Modifier.clickable { onClick() }
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = borderWidth,
                color = selectionColor,
                shape = RoundedCornerShape(8.dp)
            )
            .then(clickModifier)  // Apply clickable conditionally
    ) {
        Image(
            painter = painterResource(id = diceImageResId),
            contentDescription = "Dice showing $value",
            modifier = Modifier.size(70.dp)
        )


        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x1A4CAF50)) // Transparent green overlay
            )
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