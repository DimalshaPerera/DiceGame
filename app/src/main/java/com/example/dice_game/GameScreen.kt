//
//
//package com.example.dice_game
//
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.dice_game.components.AnimatedTitle
//import com.example.dice_game.components.CustomButton
//import com.example.dice_game.components.GameRules
//import com.example.dice_game.components.GameScreenBackground
//import com.example.dice_game.data.GameState
//import com.example.dice_game.ui.theme.DarkGray
//import com.example.dice_game.ui.theme.DiceGameTheme
//import com.example.dice_game.ui.theme.LightTransparentWhite
//import com.example.dice_game.ui.theme.Poppins
//import com.example.dice_game.ui.theme.White
//
//class GameScreen : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            DiceGameTheme {
//                Game()
//            }
//        }
//    }
//}
//
//@Composable
//fun Game() {
//    // Game state
//    val gameState = remember { mutableStateOf(GameState()) }
//    val remainingPlayerRerolls = remember { mutableStateOf(2) }
//    val remainingComputerRerolls = remember { mutableStateOf(2) }
//    val computerDiceThrown = remember { mutableStateOf(false) }
//    val inRerollMode = remember { mutableStateOf(false) }
//    // Add a state to track the current turn
//    val currentTurn = remember { mutableStateOf(1) }
//    // Add a state to track if scoring is completed for the current turn
//    val scoringCompleted = remember { mutableStateOf(false) }
//
//    // Handler for delayed actions
//    val handler = remember { Handler(Looper.getMainLooper()) }
//
//    // Background
//    GameScreenBackground()
//
//    // Main game content
//    Box(modifier = Modifier.fillMaxSize()) {
//        // Score display
//        Text(
//            text = "H:0/C:0",
//            modifier = Modifier.padding(20.dp),
//            color = White,
//            fontSize = 20.sp
//        )
//
//        AnimatedTitle(isGameScreen = true)
//
//        // Game content area
//        if (gameState.value.hasThrown) {
//            GameContent(
//                gameState = gameState.value,
//                playerRerolls = remainingPlayerRerolls.value,
//                computerRerolls = remainingComputerRerolls.value,
//                computerDiceThrown = computerDiceThrown.value,
//                inRerollMode = inRerollMode.value,
//                onDiceSelected = { index ->
//                    // Only allow dice selection when in reroll mode
//                    if (inRerollMode.value) {
//                        val updatedSelectedDice = gameState.value.selectedDice.toMutableList()
//                        updatedSelectedDice[index] = !updatedSelectedDice[index]
//                        gameState.value = gameState.value.copy(selectedDice = updatedSelectedDice)
//                    }
//                }
//            )
//        } else {
//            GameRules()
//        }
//
//        // Bottom control panel
//        ControlPanel(
//            hasThrown = gameState.value.hasThrown,
//            remainingPlayerRerolls = remainingPlayerRerolls.value,
//            inRerollMode = inRerollMode.value,
//            scoringCompleted = scoringCompleted.value,
//            onThrow = {
//                if (!gameState.value.hasThrown) {
//                    // First throw - Reset game state for new game and start a new turn
//                    computerDiceThrown.value = false
//                    remainingPlayerRerolls.value = 2
//                    remainingComputerRerolls.value = 2
//                    inRerollMode.value = false
//                    currentTurn.value = 1
//                    scoringCompleted.value = false
//
//                    // Generate new dice values
//                    val updatedPlayerDice = List(5) { (1..6).random() }
//                    val generatedComputerDice = List(5) { (1..6).random() }
//
//                    // Initial throw - no dice are selected
//                    val initialSelectedState = List(5) { false }
//
//                    // Update game state
//                    gameState.value = gameState.value.copy(
//                        playerDice = updatedPlayerDice,
//                        computerDice = generatedComputerDice,
//                        hasThrown = true,
//                        selectedDice = initialSelectedState
//                    )
//
//
//                    // Show computer dice after delay
//                    handler.postDelayed({
//                        computerDiceThrown.value = true
//
//                        // Computer makes reroll decision after another delay
//                        handler.postDelayed({
//                            computerTurn(
//                                gameState = gameState,
//                                remainingRerolls = remainingComputerRerolls,
//                                handler = handler
//                            )
//                        }, 300)
//                    }, 300)
//                } else if (inRerollMode.value) {
//                    // We're in reroll mode - reroll only unselected dice
//                    val currentDice = gameState.value.playerDice
//                    val selectedDice = gameState.value.selectedDice
//
//                    // Generate new values for unselected dice, keep selected dice unchanged
//                    val newDice = currentDice.mapIndexed { index, value ->
//                        if (selectedDice[index]) {
//                            // Keep this die as it's selected
//                            value
//                        } else {
//                            // Reroll this die with a new random value
//                            (1..6).random()
//                        }
//                    }
//
//                    // Update game state with new dice values
//                    gameState.value = gameState.value.copy(
//                        playerDice = newDice,
//                        // Reset selection state after reroll
//                        selectedDice = List(5) { false }
//                    )
//
//                    // Exit reroll mode
//                    inRerollMode.value = false
//
//                    // Auto-calculate score if no rerolls left, but with a longer delay to show the final roll
//                    if (remainingPlayerRerolls.value == 0) {
//                        handler.postDelayed({
//                            calculateScore(
//                                gameState = gameState,
//                                remainingPlayerRerolls = remainingPlayerRerolls,
//                                remainingComputerRerolls = remainingComputerRerolls,
//                                computerDiceThrown = computerDiceThrown,
//                                inRerollMode = inRerollMode,
//                                currentTurn = currentTurn,
//                                handler = handler,
//                                scoringCompleted = scoringCompleted
//                            )
//                        }, 2000) // Increased delay to 2 seconds for better visibility
//                    } else {
//                        // Computer turn after delay if we're not auto-scoring yet
//                        handler.postDelayed({
//                            computerTurn(
//                                gameState = gameState,
//                                remainingRerolls = remainingComputerRerolls,
//                                handler = handler
//                            )
//                        }, 500)
//                    }
//                } else {
//                    // Starting a new turn after scoring - now we generate new dice
//                    // Reset scoring completed state for new turn
//                    scoringCompleted.value = false
//
//
//                    // Generate completely new dice values for player and computer
//                    val updatedPlayerDice = List(5) { (1..6).random() }
//                    val updatedComputerDice = List(5) { (1..6).random() }
//
//                    // Reset selection state
//                    val initialSelectedState = List(5) { false }
//
//                    // Update game state
//                    gameState.value = gameState.value.copy(
//                        playerDice = updatedPlayerDice,
//                        computerDice = updatedComputerDice,
//                        selectedDice = initialSelectedState
//                    )
//                    remainingPlayerRerolls.value = 2
//                    remainingComputerRerolls.value = 2
//                    // Show computer dice after delay
//                    handler.postDelayed({
//                        computerDiceThrown.value = true
//
//                        // Computer turn after delay
//                        handler.postDelayed({
//                            computerTurn(
//                                gameState = gameState,
//                                remainingRerolls = remainingComputerRerolls,
//                                handler = handler
//                            )
//                        }, 300)
//                    }, 500)
//                }
//            },
//
//            onScore = {
//                calculateScore(
//                    gameState = gameState,
//                    remainingPlayerRerolls = remainingPlayerRerolls,
//                    remainingComputerRerolls = remainingComputerRerolls,
//                    computerDiceThrown = computerDiceThrown,
//                    inRerollMode = inRerollMode,
//                    currentTurn = currentTurn,
//                    handler = handler,
//                    scoringCompleted = scoringCompleted
//                )
//            },
//            onReroll = {
//                if (remainingPlayerRerolls.value > 0 && !inRerollMode.value) {
//                    // Enter reroll mode - allow player to select dice
//                    inRerollMode.value = true
//
//                    // Reset selection state to prepare for new selections
//                    gameState.value = gameState.value.copy(
//                        selectedDice = List(5) { false }
//                    )
//
//                    // Decrement remaining rerolls immediately when entering reroll mode
//                    remainingPlayerRerolls.value--
//                    if (remainingPlayerRerolls.value == 0) {
//                        handler.postDelayed({
//                            // Only auto-calculate if player used all rerolls and completed their turn
//                            if (!inRerollMode.value) {
//                                calculateScore(
//                                    gameState = gameState,
//                                    remainingPlayerRerolls = remainingPlayerRerolls,
//                                    remainingComputerRerolls = remainingComputerRerolls,
//                                    computerDiceThrown = computerDiceThrown,
//                                    inRerollMode = inRerollMode,
//                                    currentTurn = currentTurn,
//                                    handler = handler,
//                                    scoringCompleted = scoringCompleted
//                                )
//                            }
//                        }, 1500) // Give player time to make selections and press throw
//                    }
//                }
//            },
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )
//    }
//}
//
//private fun calculateScore(
//    gameState: MutableState<GameState>,
//    remainingPlayerRerolls: MutableState<Int>,
//    remainingComputerRerolls: MutableState<Int>,
//    computerDiceThrown: MutableState<Boolean>,
//    inRerollMode: MutableState<Boolean>,
//    currentTurn: MutableState<Int>,
//    handler: Handler,
//    scoringCompleted: MutableState<Boolean>
//) {
//    // Calculate scores simply by summing up the dice values
//    val playerScore = gameState.value.playerDice.sum()
//    val computerScore = gameState.value.computerDice.sum()
//
//    // Update game state with new scores
//    gameState.value = gameState.value.copy(
//        playerScore = gameState.value.playerScore + playerScore,
//        computerScore = gameState.value.computerScore + computerScore
//    )
//
//    // Reset computer dice thrown state
//    computerDiceThrown.value = false
//
//    // Exit reroll mode if active
//    inRerollMode.value = false
//
//    // Increment turn counter
//    currentTurn.value += 1
//
//    // Mark scoring as completed for this turn
//    scoringCompleted.value = true
//
//    // DO NOT reset the reroll counts here - they should remain at 0
//    // This will keep the reroll button disabled
//    // They will be reset in the onThrow function when the player starts a new turn
//}
//
//@Composable
//fun ControlPanel(
//    hasThrown: Boolean,
//    remainingPlayerRerolls: Int,
//    inRerollMode: Boolean,
//    scoringCompleted: Boolean,
//    onThrow: () -> Unit,
//    onScore: () -> Unit,
//    onReroll: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(210.dp)
//            .background(
//                color = Color.White,
//                shape = RoundedCornerShape(
//                    topStart = 24.dp,
//                    topEnd = 24.dp,
//                    bottomStart = 0.dp,
//                    bottomEnd = 0.dp
//                )
//            )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Game mascot
//            Image(
//                painter = painterResource(id = R.drawable.froggie),
//                contentDescription = "Cute Dice Mascot",
//                modifier = Modifier
//                    .size(180.dp)
//                    .padding(6.dp),
//                contentScale = ContentScale.Fit
//            )
//
//            // Game controls
//            Column(
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//                horizontalAlignment = Alignment.End
//            ) {
//                // Throw button - keep label consistent
//                CustomButton(
//                    text = if (inRerollMode) "Throw" else "Throw",
//                    fontSize = 16,
//                    onClick = onThrow,
//                    isGradient = true,
//                    width = 150,
//                    height = 48
//                )
//
//                // Score button - only enabled after dice have been thrown, not in reroll mode, and scoring is not completed
//                CustomButton(
//                    text = "Score",
//                    fontSize = 16,
//                    onClick = {
//                        // Only execute onScore if dice have been thrown, not in reroll mode, and scoring is not completed
//                        if (hasThrown && !inRerollMode && !scoringCompleted) {
//                            onScore()
//                        }
//                    },
//                    width = 150,
//                    height = 47,
//                    // Change the appearance when disabled
//                    modifier = Modifier.alpha(if (hasThrown && !inRerollMode && !scoringCompleted) 1f else 0.5f)
//                )
//
//                // Reroll button (only visible when dice have been thrown and not in reroll mode)
//                if (hasThrown && !inRerollMode) {
//                    Button(
//                        onClick = onReroll,
//                        modifier = Modifier
//                            .width(150.dp)
//                            .height(40.dp)
//                            .background(
//                                brush = Brush.linearGradient(
//                                    colors = listOf(
//                                        Color(0xFF8BC34A),
//                                        Color(0xFF7CB342)
//                                    ),
//                                    start = Offset(0f, 0f),
//                                    end = Offset(0f, Float.POSITIVE_INFINITY)
//                                ),
//                                shape = RoundedCornerShape(50.dp)
//                            ),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.Transparent
//                        ),
//                        enabled = remainingPlayerRerolls > 0,
//                        shape = RoundedCornerShape(50.dp)
//                    ) {
//                        Text(
//                            text = if (remainingPlayerRerolls > 0) "Rerolls" else "No Rerolls",
//                            color = if (remainingPlayerRerolls > 0) Color.White else Color.Red,
//                            fontFamily = Poppins,
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = 16.sp
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//private fun computerTurn(
//    gameState: MutableState<GameState>,
//    remainingRerolls: MutableState<Int>,
//    handler: Handler
//) {
//    Log.d("DiceGame", "Computer turn started. Remaining rerolls: ${remainingRerolls.value}")
//    Log.d("DiceGame", "Current computer dice: ${gameState.value.computerDice}")
//
//    // Only allow computer to reroll if it has remaining rerolls
//    if (remainingRerolls.value > 0) {
//        // Random decision whether to reroll (50% chance)
//        val willReroll = (0..1).random() == 1
//        Log.d("DiceGame", "Computer decided to reroll: $willReroll")
//
//        if (willReroll) {
//            val currentDice = gameState.value.computerDice
//
//            // Decide which dice to keep (true) and which to reroll (false)
//            val diceToKeep = List(5) { (0..1).random() == 1 }
//
//            // Log each die selection decision
//            diceToKeep.forEachIndexed { index, keep ->
//                Log.d("DiceGame", "Die #${index+1}: Value ${currentDice[index]} - ${if (keep) "KEEPING" else "REROLLING"}")
//            }
//
//            // Generate new values only for dice that weren't selected to keep
//            val newDice = currentDice.mapIndexed { index, value ->
//                if (diceToKeep[index]) {
//                    // Keep this die
//                    value
//                } else {
//                    // Reroll this die
//                    val newValue = (1..6).random()
//                    Log.d("DiceGame", "Die #${index+1}: Rerolled from $value to $newValue")
//                    newValue
//                }
//            }
//
//            Log.d("DiceGame", "Computer's reroll summary:")
//            Log.d("DiceGame", "- Original dice: $currentDice")
//            Log.d("DiceGame", "- Dice kept: ${currentDice.filterIndexed { index, _ -> diceToKeep[index] }}")
//            Log.d("DiceGame", "- Dice rerolled: ${currentDice.filterIndexed { index, _ -> !diceToKeep[index] }}")
//            Log.d("DiceGame", "- New dice set: $newDice")
//
//            // Update computer dice with new values
//            gameState.value = gameState.value.copy(
//                computerDice = newDice
//            )
//
//            // Decrement computer rerolls
//            remainingRerolls.value--
//            Log.d("DiceGame", "Computer rerolls left: ${remainingRerolls.value}")
//
//            // Check if computer wants to do a second reroll after a delay
//            if (remainingRerolls.value > 0) {
//                handler.postDelayed({
//                    // Decide if computer wants to reroll again
//                    val willRerollAgain = (0..1).random() == 1
//                    Log.d("DiceGame", "Computer second reroll decision: $willRerollAgain")
//
//                    if (willRerollAgain) {
//                        val currentDice = gameState.value.computerDice
//
//                        // Decide which dice to keep for second reroll
//                        val diceToKeep = List(5) { (0..1).random() == 1 }
//
//                        // Log second reroll decisions
//                        diceToKeep.forEachIndexed { index, keep ->
//                            Log.d("DiceGame", "Second reroll - Die #${index+1}: Value ${currentDice[index]} - ${if (keep) "KEEPING" else "REROLLING"}")
//                        }
//
//                        // Generate new values for second reroll
//                        val newDice = currentDice.mapIndexed { index, value ->
//                            if (diceToKeep[index]) {
//                                // Keep this die
//                                value
//                            } else {
//                                // Reroll this die
//                                val newValue = (1..6).random()
//                                Log.d("DiceGame", "Second reroll - Die #${index+1}: Rerolled from $value to $newValue")
//                                newValue
//                            }
//                        }
//
//                        Log.d("DiceGame", "Computer's second reroll summary:")
//                        Log.d("DiceGame", "- Original dice: $currentDice")
//                        Log.d("DiceGame", "- Dice kept: ${currentDice.filterIndexed { index, _ -> diceToKeep[index] }}")
//                        Log.d("DiceGame", "- Dice rerolled: ${currentDice.filterIndexed { index, _ -> !diceToKeep[index] }}")
//                        Log.d("DiceGame", "- New dice set: $newDice")
//
//                        // Update computer dice with new values
//                        gameState.value = gameState.value.copy(
//                            computerDice = newDice
//                        )
//                    } else {
//                        Log.d("DiceGame", "Computer decided not to use second reroll")
//                    }
//
//                    // Decrement remaining rerolls regardless of decision
//                    remainingRerolls.value--
//                    Log.d("DiceGame", "Computer rerolls left after second decision: ${remainingRerolls.value}")
//
//                }, 1000) // 1 second delay before second reroll decision
//            }
//        } else {
//            // Computer decided not to reroll
//            Log.d("DiceGame", "Computer decided to keep all dice: ${gameState.value.computerDice}")
//            // No need to update the game state since we're keeping the current dice
//        }
//    } else {
//        Log.d("DiceGame", "Computer has no rerolls available, keeping current dice: ${gameState.value.computerDice}")
//    }
//}
//
//@Composable
//fun GameContent(
//    gameState: GameState,
//    playerRerolls: Int,
//    computerRerolls: Int,
//    computerDiceThrown: Boolean,
//    inRerollMode: Boolean,
//    onDiceSelected: (Int) -> Unit
//) {
//    // Score display
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 100.dp)
//            .padding(horizontal = 16.dp)
//            .height(70.dp)
//            .background(
//                color = LightTransparentWhite,
//                shape = RoundedCornerShape(16.dp)
//            )
//    ) {
//        Text(
//            text = "Human : ${gameState.playerScore} ",
//            modifier = Modifier
//                .align(Alignment.CenterStart)
//                .padding(13.dp),
//            color = DarkGray
//        )
//        Text(
//            text = "Computer : ${gameState.computerScore} ",
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .padding(13.dp),
//            color = DarkGray
//        )
//    }
//
//    // Dice section headers
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 200.dp)
//            .padding(horizontal = 16.dp)
//    ) {
//        Text(
//            text = "YOUR DICE",
//            modifier = Modifier
//                .align(Alignment.CenterStart),
//            color = White
//        )
//
//        Text(
//            text = "COMPUTER'S DICE",
//            modifier = Modifier
//                .align(Alignment.CenterEnd),
//            color = White,
//        )
//    }
//
//    // Dice displays
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 25.dp)
//            .padding(top = 250.dp),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        // Player dice column
//        Column(
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalAlignment = Alignment.Start
//        ) {
//            gameState.playerDice.forEachIndexed { index, value ->
//                SelectableDiceImage(
//                    value = value,
//                    isSelected = gameState.selectedDice[index],
//                    onClick = { onDiceSelected(index) },
//                    selectable = inRerollMode  // Only allow selection when in reroll mode
//                )
//            }
//        }
//
//        // Computer dice column
//        Column(
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalAlignment = Alignment.End
//        ) {
//            if (computerDiceThrown) {
//                gameState.computerDice.forEach { value ->
//                    DiceImage(value = value)
//                }
//            } else {
//                repeat(5) {
//                    Box(
//                        modifier = Modifier
//                            .size(70.dp)
//                            .background(
//                                color = LightTransparentWhite,
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                    )
//                }
//            }
//        }
//    }
//
//    // Reroll information and help text
//    Column(
//        modifier = Modifier
//            .padding(top = 670.dp)
//            .fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Show selection instructions only when in reroll mode
//        if (inRerollMode) {
//            Text(
//                text = "Tap dice to keep them, then press the throw",
//                color = Color.White,
//                fontSize = 14.sp
//            )
//        } else if (playerRerolls > 0 && !inRerollMode) {
//            Text(
//                text = "Press 'Reroll' to select dice to keep",
//                color = Color.White,
//                fontSize = 14.sp
//            )
//        }
//
//        Text(
//            text = if (playerRerolls > 0) "$playerRerolls rerolls remaining" else "No rerolls left",
//            color = if (playerRerolls > 0) Color.White else Color.Red,
//            fontSize = 14.sp,
//            modifier = Modifier.padding(top = 4.dp)
//        )
//    }
//}
//
//@Composable
//fun SelectableDiceImage(
//    value: Int,
//    isSelected: Boolean,
//    onClick: () -> Unit,
//    selectable: Boolean = true
//) {
//    val diceImageResId = when (value) {
//        1 -> R.drawable.d_1
//        2 -> R.drawable.d_2
//        3 -> R.drawable.d_3
//        4 -> R.drawable.d_4
//        5 -> R.drawable.d_5
//        6 -> R.drawable.d_6
//        else -> R.drawable.d_1
//    }
//
//    val selectionColor = Color(0xFF4CAF50) // Green color for selection
//    val borderWidth = if (isSelected) 3.dp else 0.dp
//
//    // Determine if dice should be clickable based on selectable parameter
//    val clickModifier = if (selectable) {
//        Modifier.clickable { onClick() }
//    } else {
//        Modifier
//    }
//
//    Box(
//        modifier = Modifier
//            .size(70.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .border(
//                width = borderWidth,
//                color = selectionColor,
//                shape = RoundedCornerShape(8.dp)
//            )
//            .then(clickModifier)  // Apply clickable conditionally
//    ) {
//        Image(
//            painter = painterResource(id = diceImageResId),
//            contentDescription = "Dice showing $value",
//            modifier = Modifier.size(70.dp)
//        )
//
//        if (isSelected) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color(0x1A4CAF50)) // Transparent green overlay
//            )
//        }
//    }
//}
//
//@Composable
//fun DiceImage(value: Int) {
//    val diceImageResId = when (value) {
//        1 -> R.drawable.d_1
//        2 -> R.drawable.d_2
//        3 -> R.drawable.d_3
//        4 -> R.drawable.d_4
//        5 -> R.drawable.d_5
//        6 -> R.drawable.d_6
//        else -> R.drawable.d_1
//    }
//
//    Image(
//        painter = painterResource(id = diceImageResId),
//        contentDescription = "Dice showing $value",
//        modifier = Modifier.size(70.dp)
//    )
//}



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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.dice_game.components.ControlPanel
import com.example.dice_game.components.CustomButton
import com.example.dice_game.components.DiceImage
import com.example.dice_game.components.GameRules
import com.example.dice_game.components.GameScreenBackground
import com.example.dice_game.components.SelectableDiceImage
import com.example.dice_game.data.GameContext
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
    val gameContext = remember {
        GameContext(
            playerRerolls = mutableStateOf(2),
            computerRerolls = mutableStateOf(2),
            computerDiceThrown = mutableStateOf(false),
            inRerollMode = mutableStateOf(false),
            currentTurn = mutableStateOf(1),
            scoringCompleted = mutableStateOf(false)
        )
    }

    // Handler for delayed actions
    val handler = remember { Handler(Looper.getMainLooper()) }

    // Background
    GameScreenBackground()

    // Main game content
    Box(modifier = Modifier.fillMaxSize()) {
        // Score display
        Text(
            text = "H:${gameState.value.playerScore}/C:${gameState.value.computerScore}",
            modifier = Modifier.padding(20.dp),
            color = White,
            fontSize = 20.sp
        )

        AnimatedTitle(isGameScreen = true)

        // Game content area
        if (gameState.value.hasThrown) {
            GameContent(
                gameState = gameState.value,
                playerRerolls = gameContext.playerRerolls.value,
                computerRerolls = gameContext.computerRerolls.value,
                computerDiceThrown = gameContext.computerDiceThrown.value,
                inRerollMode = gameContext.inRerollMode.value,
                onDiceSelected = { index ->
                    // Only allow dice selection when in reroll mode
                    if (gameContext.inRerollMode.value) {
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
            remainingPlayerRerolls = gameContext.playerRerolls.value,
            inRerollMode = gameContext.inRerollMode.value,
            scoringCompleted = gameContext.scoringCompleted.value,
            onThrow = {
                handleThrowAction(gameState, gameContext, handler)
            },
            onScore = {
                calculateScore(gameState, gameContext, handler)
            },
            onReroll = {
                if (gameContext.playerRerolls.value > 0 && !gameContext.inRerollMode.value) {
                    // Enter reroll mode - allow player to select dice
                    gameContext.inRerollMode.value = true

                    // Reset selection state to prepare for new selections
                    gameState.value = gameState.value.copy(
                        selectedDice = List(5) { false }
                    )

                    // Decrement remaining rerolls immediately when entering reroll mode
                    gameContext.playerRerolls.value--
                    if (gameContext.playerRerolls.value == 0) {
                        handler.postDelayed({
                            // Only auto-calculate if player used all rerolls and completed their turn
                            if (!gameContext.inRerollMode.value) {
                                calculateScore(gameState, gameContext, handler)
                            }
                        }, 1500) // Give player time to make selections and press throw
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
private fun handleThrowAction(
    gameState: MutableState<GameState>,
    gameContext: GameContext,
    handler: Handler
) {
    if (!gameState.value.hasThrown) {
        // First throw - Initialize new game
        initializeNewGame(gameState, gameContext)

        // Show computer dice after delay
        scheduleComputerActions(gameState, gameContext, handler)
    } else if (gameContext.inRerollMode.value) {
        // We're in reroll mode - reroll only unselected dice
        rerollPlayerDice(gameState, gameContext)

        // Only proceed with computer turn if player has used all rerolls
        if (gameContext.playerRerolls.value == 0) {
            handler.postDelayed({
                calculateScore(gameState, gameContext, handler)
            }, 2000) // Increased delay to 2 seconds for better visibility
        } else {
            // Do nothing if player still has rerolls left
            // This prevents automatic computer turn
        }
    } else {
        // Starting a new turn after scoring - now we generate new dice
        startNewTurn(gameState, gameContext, handler)
    }
}
//
//private fun handleThrowAction(
//    gameState: MutableState<GameState>,
//    gameContext: GameContext,
//    handler: Handler
//) {
//    if (!gameState.value.hasThrown) {
//        // First throw - Initialize new game
//        initializeNewGame(gameState, gameContext)
//
//        // Show computer dice after delay
//        scheduleComputerActions(gameState, gameContext, handler)
//    } else if (gameContext.inRerollMode.value) {
//        // We're in reroll mode - reroll only unselected dice
//        rerollPlayerDice(gameState, gameContext)
//
//        // Auto-calculate score if no rerolls left, but with a longer delay to show the final roll
//        if (gameContext.playerRerolls.value == 0) {
//            handler.postDelayed({
//                calculateScore(gameState, gameContext, handler)
//            }, 2000) // Increased delay to 2 seconds for better visibility
//        } else {
//            // Computer turn after delay if we're not auto-scoring yet
//            handler.postDelayed({
//                computerTurn(gameState, gameContext.computerRerolls, handler)
//            }, 500)
//        }
//    } else {
//        // Starting a new turn after scoring - now we generate new dice
//        startNewTurn(gameState, gameContext, handler)
//    }
//}

private fun initializeNewGame(
    gameState: MutableState<GameState>,
    gameContext: GameContext
) {
    gameContext.computerDiceThrown.value = false
    gameContext.playerRerolls.value = 2
    gameContext.computerRerolls.value = 2
    gameContext.inRerollMode.value = false
    gameContext.currentTurn.value = 1
    gameContext.scoringCompleted.value = false

    // Generate new dice values
    val updatedPlayerDice = generateDice()
    val generatedComputerDice = generateDice()

    // Initial throw - no dice are selected
    val initialSelectedState = List(5) { false }

    // Update game state
    gameState.value = gameState.value.copy(
        playerDice = updatedPlayerDice,
        computerDice = generatedComputerDice,
        hasThrown = true,
        selectedDice = initialSelectedState
    )
}

private fun scheduleComputerActions(
    gameState: MutableState<GameState>,
    gameContext: GameContext,
    handler: Handler
) {
    // Show computer dice after delay
    handler.postDelayed({
        gameContext.computerDiceThrown.value = true

        // Computer makes reroll decision after another delay
        handler.postDelayed({
            computerTurn(gameState, gameContext.computerRerolls, handler)
        }, 300)
    }, 300)
}

private fun rerollPlayerDice(
    gameState: MutableState<GameState>,
    gameContext: GameContext
) {
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
    gameContext.inRerollMode.value = false
}

private fun startNewTurn(
    gameState: MutableState<GameState>,
    gameContext: GameContext,
    handler: Handler
) {
    // Reset scoring completed state for new turn
    gameContext.scoringCompleted.value = false

    // Generate completely new dice values for player and computer
    val updatedPlayerDice = generateDice()
    val updatedComputerDice = generateDice()

    // Reset selection state
    val initialSelectedState = List(5) { false }

    // Update game state
    gameState.value = gameState.value.copy(
        playerDice = updatedPlayerDice,
        computerDice = updatedComputerDice,
        selectedDice = initialSelectedState
    )
    gameContext.playerRerolls.value = 2
    gameContext.computerRerolls.value = 2

    // Show computer dice after delay
    handler.postDelayed({
        gameContext.computerDiceThrown.value = true

        // Computer turn after delay
        handler.postDelayed({
            computerTurn(gameState, gameContext.computerRerolls, handler)
        }, 300)
    }, 500)
}

private fun generateDice(): List<Int> = List(5) { (1..6).random() }
private fun calculateScore(
    gameState: MutableState<GameState>,
    gameContext: GameContext,
    handler: Handler
) {
    if (gameContext.computerRerolls.value == 0){
        Log.d("dicegame","no rerolls left so we are calculating")
    }
    // If the computer still has rerolls, let it decide again
    if (gameContext.computerRerolls.value > 0) {
        Log.d("DiceGame", "Before scoring, computer reconsidering rerolls...")

        handler.postDelayed({
            computerTurn(gameState, gameContext.computerRerolls, handler)

            // After rerolls are done (or if no rerolls were used), finalize the score
            finalizeScore(gameState, gameContext)
        }, 1000) // Small delay before rechecking
    } else {
        finalizeScore(gameState, gameContext)
    }
}

private fun finalizeScore(
    gameState: MutableState<GameState>,
    gameContext: GameContext
) {
    val playerScore = gameState.value.playerDice.sum()
    val computerScore = gameState.value.computerDice.sum()

    // Update scores
    gameState.value = gameState.value.copy(
        playerScore = gameState.value.playerScore + playerScore,
        computerScore = gameState.value.computerScore + computerScore
    )


    // Reset game state for next turn
    gameContext.computerDiceThrown.value = false
    gameContext.inRerollMode.value = false
    gameContext.currentTurn.value += 1
    gameContext.scoringCompleted.value = true
}




private fun computerTurn(
    gameState: MutableState<GameState>,
    remainingRerolls: MutableState<Int>,
    handler: Handler
) {
    Log.d("DiceGame", "Computer turn started. Remaining rerolls: ${remainingRerolls.value}")
    Log.d("DiceGame", "Current computer dice: ${gameState.value.computerDice}")

    // Only allow computer to reroll if it has remaining rerolls
    if (remainingRerolls.value > 0) {
        // Random decision whether to reroll (50% chance)
        val willReroll = (0..1).random() == 1
        Log.d("DiceGame", "Computer decided to reroll: $willReroll")

        if (willReroll) {
            performComputerReroll(gameState, remainingRerolls)

            // Check if computer wants to do a second reroll after a delay
            if (remainingRerolls.value > 0) {
                handler.postDelayed({
                    considerSecondReroll(gameState, remainingRerolls)
                }, 1000) // 1 second delay before second reroll decision
            }
        } else {
            // Computer decided not to reroll
            Log.d("DiceGame", "Computer decided to keep all dice: ${gameState.value.computerDice}")
            // No need to update the game state since we're keeping the current dice
        }
    } else {
        Log.d("DiceGame", "Computer has no rerolls available, keeping current dice: ${gameState.value.computerDice}")
    }
}

private fun performComputerReroll(
    gameState: MutableState<GameState>,
    remainingRerolls: MutableState<Int>
) {
    val currentDice = gameState.value.computerDice

    // Decide which dice to keep (true) and which to reroll (false)
    val diceToKeep = List(5) { (0..1).random() == 1 }

    // Log each die selection decision
    diceToKeep.forEachIndexed { index, keep ->
        Log.d("DiceGame", "Die #${index+1}: Value ${currentDice[index]} - ${if (keep) "KEEPING" else "REROLLING"}")
    }

    // Generate new values only for dice that weren't selected to keep
    val newDice = currentDice.mapIndexed { index, value ->
        if (diceToKeep[index]) {
            // Keep this die
            value
        } else {
            // Reroll this die
            val newValue = (1..6).random()
            Log.d("DiceGame", "Die #${index+1}: Rerolled from $value to $newValue")
            newValue
        }
    }

    logRerollSummary(currentDice, diceToKeep, newDice)

    // Update computer dice with new values
    gameState.value = gameState.value.copy(
        computerDice = newDice
    )

    // Decrement computer rerolls
    remainingRerolls.value--
    Log.d("DiceGame", "Computer rerolls left: ${remainingRerolls.value}")
}

private fun considerSecondReroll(
    gameState: MutableState<GameState>,
    remainingRerolls: MutableState<Int>
) {
    // Decide if computer wants to reroll again
    val willRerollAgain = (0..1).random() == 1
    Log.d("DiceGame", "Computer second reroll decision: $willRerollAgain")

    if (willRerollAgain) {
        val currentDice = gameState.value.computerDice

        // Decide which dice to keep for second reroll
        val diceToKeep = List(5) { (0..1).random() == 1 }

        // Log second reroll decisions
        diceToKeep.forEachIndexed { index, keep ->
            Log.d("DiceGame", "Second reroll - Die #${index+1}: Value ${currentDice[index]} - ${if (keep) "KEEPING" else "REROLLING"}")
            remainingRerolls.value--
            Log.d("DiceGame", "Computer rerolls left after second decision: ${remainingRerolls.value}")
        }


        // Generate new values for second reroll
        val newDice = currentDice.mapIndexed { index, value ->
            if (diceToKeep[index]) {
                // Keep this die
                value
            } else {
                // Reroll this die
                val newValue = (1..6).random()
                Log.d("DiceGame", "Second reroll - Die #${index+1}: Rerolled from $value to $newValue")
                newValue
            }
        }

        Log.d("DiceGame", "Computer's second reroll summary:")
        Log.d("DiceGame", "- Original dice: $currentDice")
        Log.d("DiceGame", "- Dice kept: ${currentDice.filterIndexed { index, _ -> diceToKeep[index] }}")
        Log.d("DiceGame", "- Dice rerolled: ${currentDice.filterIndexed { index, _ -> !diceToKeep[index] }}")
        Log.d("DiceGame", "- New dice set: $newDice")

        // Update computer dice with new values
        gameState.value = gameState.value.copy(
            computerDice = newDice
        )
    } else {
        Log.d("DiceGame", "Computer decided not to use second reroll")
    }


    Log.d("DiceGame", "Computer rerolls left after second decision: ${remainingRerolls.value}")
}

private fun logRerollSummary(
    currentDice: List<Int>,
    diceToKeep: List<Boolean>,
    newDice: List<Int>
) {
    Log.d("DiceGame", "Computer's reroll summary:")
    Log.d("DiceGame", "- Original dice: $currentDice")
    Log.d("DiceGame", "- Dice kept: ${currentDice.filterIndexed { index, _ -> diceToKeep[index] }}")
    Log.d("DiceGame", "- Dice rerolled: ${currentDice.filterIndexed { index, _ -> !diceToKeep[index] }}")
    Log.d("DiceGame", "- New dice set: $newDice")
}

@Composable
fun GameContent(
    gameState: GameState,
    playerRerolls: Int,
    computerRerolls: Int,
    computerDiceThrown: Boolean,
    inRerollMode: Boolean,
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
        // Show appropriate instruction text based on game state
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



