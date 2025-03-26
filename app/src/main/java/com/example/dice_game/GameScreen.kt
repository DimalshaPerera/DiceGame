

package com.example.dice_game

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.components.AnimatedTitle
import com.example.dice_game.components.ControlPanel

import com.example.dice_game.components.GameContent
import com.example.dice_game.components.GameRules
import com.example.dice_game.components.GameScreenBackground

import com.example.dice_game.data.GameState
import com.example.dice_game.ui.theme.BrownPrimary

import com.example.dice_game.ui.theme.DiceGameTheme

import com.example.dice_game.ui.theme.White
import com.example.dice_game.ui.theme.Green
import com.example.dice_game.ui.theme.Red
import com.example.dice_game.ui.theme.DarkYellow


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


var computerWins=0
var humanWins=0




@Composable
fun Game() {

    // Game state
    val gameState = remember {
        mutableStateOf(
            GameState()
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
            text = "H:${humanWins}/C:${computerWins}",

            modifier = Modifier.padding(20.dp),
            color = White,
            fontSize = 20.sp
        )

        AnimatedTitle(isGameScreen = true)

        // Game content area
        if (gameState.value.hasThrown) {
            GameContent(
                gameState = gameState.value,
                playerRerolls = gameState.value.playerRerolls,
                computerRerolls = gameState.value.computerRerolls,
                computerDiceThrown = gameState.value.computerDiceThrown,
                inRerollMode = gameState.value.inRerollMode,
                onDiceSelected = { index ->
                    // Only allow dice selection when in reroll mode
                    if (gameState.value.inRerollMode) {
                        val updatedSelectedDice = gameState.value.selectedDice.toMutableList()
                        // selection status of the dice
                        updatedSelectedDice[index] = !updatedSelectedDice[index]
                        gameState.value = gameState.value.copy(selectedDice = updatedSelectedDice)
                    }
                }
            )
        } else {
            GameRules()
        }
        Result(gameState)

        // Bottom control panel
        ControlPanel(
            hasThrown = gameState.value.hasThrown,
            remainingPlayerRerolls = gameState.value.playerRerolls,
            inRerollMode = gameState.value.inRerollMode,
            scoringCompleted = gameState.value.scoringCompleted,
            onThrow = {
                handleThrowAction(gameState, handler)
            },
            onScore = {
                calculateScore(gameState, handler)
            },
            onReroll = {
                if (gameState.value.playerRerolls > 0 && !gameState.value.inRerollMode) {
                    // Enter reroll mode
                    gameState.value = gameState.value.copy(
                        inRerollMode = true,

                        selectedDice = List(5) { false }, // Resets selected dice
                        playerRerolls = gameState.value.playerRerolls - 1
                    )

                    if (gameState.value.playerRerolls == 0) {
                        handler.postDelayed({
                            calculateScore(gameState, handler)
                        }, 1500)
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private fun handleThrowAction(
    gameState: MutableState<GameState>,
    handler: Handler
) {
    if (!gameState.value.hasThrown) {
        // First throw - Initialize new game
        gameState.value = gameState.value.copy(
            hasThrown = true,
            playerDice = generateDice(),
            computerDice = generateDice(),
            selectedDice = List(5) { false },
            computerDiceThrown = false
        )

        // Show computer dice after delay
        scheduleComputerActions(gameState, handler)
    } else if (gameState.value.inRerollMode) {
        // We're in reroll mode - reroll only unselected dice
        rerollPlayerDice(gameState)

        // Only proceed with computer turn if player has used all rerolls
        if (gameState.value.playerRerolls == 0) {
            handler.postDelayed({
                calculateScore(gameState, handler)
            }, 2000)
        }
    } else {
        // Starting a new turn after scoring - now we generate new dice
        startNewTurn(gameState, handler)
    }
}

private fun scheduleComputerActions(
    gameState: MutableState<GameState>,
    handler: Handler
) {
    // Show computer dice after delay
    handler.postDelayed({
        gameState.value = gameState.value.copy(computerDiceThrown = true)

        // Computer makes reroll decision after another delay
        handler.postDelayed({
            computerTurn(gameState, handler)
        }, 300)
    }, 300)
}

private fun rerollPlayerDice(
    gameState: MutableState<GameState>
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
        selectedDice = List(5) { false },
        inRerollMode = false
    )
}

private fun startNewTurn(
    gameState: MutableState<GameState>,
    handler: Handler
) {
    // Generate completely new dice values for player and computer
    val updatedPlayerDice = generateDice()
    val updatedComputerDice = generateDice()

    // Update game state
    gameState.value = gameState.value.copy(
        playerDice = updatedPlayerDice,
        computerDice = updatedComputerDice,
        selectedDice = List(5) { false },
        playerRerolls = 2,
        computerRerolls = 2,
        computerDiceThrown = false,
        scoringCompleted = false
    )

    // Show computer dice after delay
    handler.postDelayed({
        gameState.value = gameState.value.copy(computerDiceThrown = true)

        // Computer turn after delay
        handler.postDelayed({
            computerTurn(gameState, handler)
        }, 300)
    }, 500)
}

private fun generateDice(): List<Int> = List(5) { (1..6).random() }

private fun calculateScore(
    gameState: MutableState<GameState>,
    handler: Handler
) {
    // If the computer still has rerolls, let it decide again
    if (gameState.value.computerRerolls > 0) {
        Log.d("DiceGame", "Before scoring, computer reconsidering rerolls...")

        handler.postDelayed({
            computerTurn(gameState, handler)

            // After rerolls are done (or if no rerolls were used), finalize the score
            finalizeScore(gameState)
        }, 1000) // Small delay before rechecking
    } else {
        finalizeScore(gameState)
    }
}

private fun finalizeScore(
    gameState: MutableState<GameState>
) {
    val playerScore = gameState.value.playerDice.sum()
    val computerScore = gameState.value.computerDice.sum()

    // Update scores
    gameState.value = gameState.value.copy(
        playerScore = gameState.value.playerScore + playerScore,
        computerScore = gameState.value.computerScore + computerScore,
        scoringCompleted = true,
        computerDiceThrown = false,
        inRerollMode = false
    )
}

private fun computerTurn(
    gameState: MutableState<GameState>,
    handler: Handler
) {
    Log.d("DiceGame", "Computer turn started. Remaining rerolls: ${gameState.value.computerRerolls}")
    Log.d("DiceGame", "Current computer dice: ${gameState.value.computerDice}")

    // Only allow computer to reroll if it has remaining rerolls
    if (gameState.value.computerRerolls > 0) {
        // Random decision whether to reroll (50% chance)
        val willReroll = (0..1).random() == 1
        Log.d("DiceGame", "Computer decided to reroll: $willReroll")

        if (willReroll) {
            performComputerReroll(gameState)

            // Check if computer wants to do a second reroll after a delay
            if (gameState.value.computerRerolls > 0) {
                handler.postDelayed({
                    considerSecondReroll(gameState)
                }, 1000) // 1 second delay before second reroll decision
            }
        } else {
            // Computer decided not to reroll
            Log.d("DiceGame", "Computer decided to keep all dice: ${gameState.value.computerDice}")
        }
    } else {
        Log.d("DiceGame", "Computer has no rerolls available, keeping current dice: ${gameState.value.computerDice}")
    }
}

private fun performComputerReroll(
    gameState: MutableState<GameState>
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

    // Update computer dice and rerolls
    gameState.value = gameState.value.copy(
        computerDice = newDice,
        computerRerolls = gameState.value.computerRerolls - 1
    )
}

private fun considerSecondReroll(
    gameState: MutableState<GameState>
) {
    // Decide if computer wants to reroll again
    val willRerollAgain = (0..1).random() == 1
    Log.d("DiceGame", "Computer second reroll decision: $willRerollAgain")

    if (willRerollAgain && gameState.value.computerRerolls > 0) {
        val currentDice = gameState.value.computerDice

        // Decide which dice to keep for second reroll
        val diceToKeep = List(5) { (0..1).random() == 1 }

        // Log second reroll decisions
        diceToKeep.forEachIndexed { index, keep ->
            Log.d("DiceGame", "Second reroll - Die #${index+1}: Value ${currentDice[index]} - ${if (keep) "KEEPING" else "REROLLING"}")
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

        // Update computer dice and rerolls
        gameState.value = gameState.value.copy(
            computerDice = newDice,
            computerRerolls = gameState.value.computerRerolls - 1
        )
    } else {
        Log.d("DiceGame", "Computer decided not to use second reroll")
    }

    Log.d("DiceGame", "Computer rerolls left after second decision: ${gameState.value.computerRerolls}")
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
fun Result(gameState: MutableState<GameState>) {
    val showResultDialog = remember { mutableStateOf(false) }
    val resultMessage = remember { mutableStateOf("") }
    val resultColor = remember { mutableStateOf(Color.Black) }
    val frogImage = remember { mutableStateOf(0) }
    val currentContext = LocalContext.current
    val handler = remember { Handler(Looper.getMainLooper()) }

    // New state to track tie-breaker phase
    val showTieFrogDialog = remember { mutableStateOf(false) }
    val isTieBreaker = remember { mutableStateOf(false) }
    val tieBreakPlayerDice = remember { mutableStateOf(listOf<Int>()) }
    val tieBreakComputerDice = remember { mutableStateOf(listOf<Int>()) }

    // BackHandler to handle back press when result dialog is shown
    BackHandler(enabled = showResultDialog.value) {
        val intent = Intent(currentContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        currentContext.startActivity(intent)
    }

    // Check win conditions immediately after scoring is completed
    if (gameState.value.scoringCompleted) {
        if (gameState.value.playerScore >= 101 && gameState.value.playerScore > gameState.value.computerScore) {
            humanWins++
            showResultDialog.value = true
            resultMessage.value = "You Win!"
            resultColor.value = Green
            frogImage.value = R.drawable.happy_frog
            Log.d("res", "player wins")
        } else if (gameState.value.computerScore >= 101 && gameState.value.computerScore > gameState.value.playerScore) {
            computerWins++
            showResultDialog.value = true
            resultMessage.value = "You Lose!"
            resultColor.value = Color.Red
            frogImage.value = R.drawable.sad_frog
            Log.d("res", "computer wins")
        } else if (gameState.value.playerScore >= 101 && gameState.value.computerScore >= 101) {


            showTieFrogDialog.value = true
            resultMessage.value = "     It's a Tie! \n Let's roll again"
            resultColor.value = DarkYellow
            frogImage.value = R.drawable.confused

            // Schedule transition to tie-breaker after 5 seconds
            handler.postDelayed({
                showTieFrogDialog.value = false
                isTieBreaker.value = true

                // Generate initial tie-breaker dice
                val playerTieBreakDice = generateDice()
                val computerTieBreakDice = generateDice()

                tieBreakPlayerDice.value = playerTieBreakDice
                tieBreakComputerDice.value = computerTieBreakDice

                // Modify game state to allow interaction with tie-breaker dice
                gameState.value = gameState.value.copy(
                    playerDice = playerTieBreakDice,
                    computerDice = computerTieBreakDice,
                    computerDiceThrown = true,
                    playerRerolls = 0,
                    computerRerolls = 0,
                    inRerollMode = false,
                    scoringCompleted = false,
                    selectedDice = List(5) { false }
                )
            }, 5000) // 5 seconds
        }
    }

    // Tie-breaker result determination
    if (isTieBreaker.value && gameState.value.scoringCompleted) {
        val playerTieBreakScore = tieBreakPlayerDice.value.sum()
        val computerTieBreakScore = tieBreakComputerDice.value.sum()

        Log.d("TieBreaker", "Tie-Break Scores - Player: $playerTieBreakScore, Computer: $computerTieBreakScore")

        if (playerTieBreakScore > computerTieBreakScore) {
            humanWins++
            showResultDialog.value = true
            resultMessage.value = "You Win!"
            resultColor.value = Green
            frogImage.value = R.drawable.happy_frog
            isTieBreaker.value = false
        } else if (computerTieBreakScore > playerTieBreakScore) {
            computerWins++
            showResultDialog.value = true
            resultMessage.value = "You Lose!"
            resultColor.value = Color.Red
            frogImage.value = R.drawable.sad_frog
            isTieBreaker.value = false
        } else {
            // If still a tie, automatically generate new dice for next round
            val nextPlayerTieBreakDice = generateDice()
            val nextComputerTieBreakDice = generateDice()

            tieBreakPlayerDice.value = nextPlayerTieBreakDice
            tieBreakComputerDice.value = nextComputerTieBreakDice

            // Update game state to continue tie-breaker
            gameState.value = gameState.value.copy(
                playerDice = nextPlayerTieBreakDice,
                computerDice = nextComputerTieBreakDice,
                computerDiceThrown = true,
                playerRerolls = 0,  // Prevent player from rerolling
                computerRerolls = 0,  // Prevent computer from rerolling
                inRerollMode = false,
                scoringCompleted = false,
                selectedDice = List(5) { false }
            )
        }
    }

    // Tie Frog Dialog
    if (showTieFrogDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = frogImage.value),
                        contentDescription = resultMessage.value,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = resultMessage.value,
                        color = resultColor.value,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            },
            confirmButton = {
                // Empty for now
            }
        )
    }

    // Result Dialog
    if (showResultDialog.value) {
        AlertDialog(
            onDismissRequest = {
                val intent = Intent(currentContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                currentContext.startActivity(intent)
            },
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = frogImage.value),
                        contentDescription = resultMessage.value,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = resultMessage.value,
                        color = resultColor.value,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            },
            confirmButton = {
                // Empty for now, as the dialog is dismissed via onDismissRequest
            }
        )
    }
}

//@Composable
//fun Result(gameState: MutableState<GameState>) {
//    val showResultDialog = remember { mutableStateOf(false) }
//    val resultMessage = remember { mutableStateOf("") }
//    val resultColor = remember { mutableStateOf(Color.Black) }
//    val frogImage = remember { mutableStateOf(0) }
//    val currentContext = LocalContext.current
//    val handler = remember { Handler(Looper.getMainLooper()) }
//
//    // BackHandler to handle back press when result dialog is shown
//    BackHandler(enabled = showResultDialog.value) {
//        val intent = Intent(currentContext, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        currentContext.startActivity(intent)
//    }
//
//    // Check win conditions immediately after scoring is completed
//    if (gameState.value.scoringCompleted) {
//        if (gameState.value.playerScore >= 101 && gameState.value.playerScore > gameState.value.computerScore) {
//            humanWins++
//            showResultDialog.value = true
//            resultMessage.value = "You Win!"
//            resultColor.value = Green
//            frogImage.value = R.drawable.happy_frog
//            Log.d("res", "player wins")
//        } else if (gameState.value.computerScore >= 101 && gameState.value.computerScore > gameState.value.playerScore) {
//            computerWins++
//            showResultDialog.value = true
//            resultMessage.value = "You Lose!"
//            resultColor.value = Color.Red
//            frogImage.value = R.drawable.sad_frog
//            Log.d("res", "computer wins")
//        } else if (gameState.value.playerScore >= 101 && gameState.value.computerScore >= 101) {
////        if(gameState.value.playerScore==gameState.value.computerScore){
//            // Tie scenario
//            showResultDialog.value = true
//            resultMessage.value = "     It's a Tie! \n Let's roll again"
//            resultColor.value = DarkYellow
//            frogImage.value = R.drawable.confused
//
//            // Temporary tie dialog that disappears after 1 second
//            handler.postDelayed({
//                // Proceed with tie-breaker rolls
//                val playerTieBreakDice = generateDice()
//                val computerTieBreakDice = generateDice()
//
//                val playerTieBreakScore = playerTieBreakDice.sum()
//                val computerTieBreakScore = computerTieBreakDice.sum()
//
//                Log.d("TieBreaker", "First Tie-Break Roll")
//                Log.d("TieBreaker", "Player Tie-Break Dice: $playerTieBreakDice, Score: $playerTieBreakScore")
//                Log.d("TieBreaker", "Computer Tie-Break Dice: $computerTieBreakDice, Score: $computerTieBreakScore")
//
//                if (playerTieBreakScore > computerTieBreakScore) {
//                    humanWins++
//                    showResultDialog.value = true
//                    resultMessage.value = "You Win!"
//                    resultColor.value = Green
//                    frogImage.value = R.drawable.happy_frog
//                    Log.d("TieBreaker", "Player wins first tie-break roll")
//                } else if (computerTieBreakScore > playerTieBreakScore) {
//                    computerWins++
//                    showResultDialog.value = true
//                    resultMessage.value = "You Lose!"
//                    resultColor.value = Color.Red
//                    frogImage.value = R.drawable.sad_frog
//                    Log.d("TieBreaker", "Computer wins first tie-break roll")
//                } else {
//                    // Keep rolling until there's a winner
//                    var continueRolling = true
//                    var rollCount = 1
//
//                    while (continueRolling) {
//                        val nextPlayerTieBreakDice = generateDice()
//                        val nextComputerTieBreakDice = generateDice()
//
//                        val nextPlayerTieBreakScore = nextPlayerTieBreakDice.sum()
//                        val nextComputerTieBreakScore = nextComputerTieBreakDice.sum()
//
//                        Log.d("TieBreaker", "Continued Tie-Break Roll #$rollCount")
//                        Log.d("TieBreaker", "Player Dice: $nextPlayerTieBreakDice, Score: $nextPlayerTieBreakScore")
//                        Log.d("TieBreaker", "Computer Dice: $nextComputerTieBreakDice, Score: $nextComputerTieBreakScore")
//
//                        if (nextPlayerTieBreakScore > nextComputerTieBreakScore) {
//                            humanWins++
//                            showResultDialog.value = true
//                            resultMessage.value = "You Win!"
//                            resultColor.value = Green
//                            frogImage.value = R.drawable.happy_frog
//                            Log.d("TieBreaker", "Player wins after continued rolls")
//                            continueRolling = false
//                        } else if (nextComputerTieBreakScore > nextPlayerTieBreakScore) {
//                            computerWins++
//                            showResultDialog.value = true
//                            resultMessage.value = "You Lose!"
//                            resultColor.value = Color.Red
//                            frogImage.value = R.drawable.sad_frog
//                            Log.d("TieBreaker", "Computer wins after continued rolls")
//                            continueRolling = false
//                        }
//
//                        rollCount++
//                    }
//                }
//            }, 5000) // 1-second delay before starting tie-breaker
//        }
//    }
//
//    // Result Dialog
//    if (showResultDialog.value) {
//        AlertDialog(
//            onDismissRequest = {
//                val intent = Intent(currentContext, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                currentContext.startActivity(intent)
//            },
//            title = null,
//            text = {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = frogImage.value),
//                        contentDescription = resultMessage.value,
//                        modifier = Modifier
//                            .size(200.dp)
//                            .padding(bottom = 16.dp)
//                    )
//                    Text(
//                        text = resultMessage.value,
//                        color = resultColor.value,
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//                }
//            },
//            confirmButton = {
//
//            }
//        )
//    }
//}

//@Composable
//fun Result(gameState: MutableState<GameState>) {
//    val showResultDialog = remember { mutableStateOf(false) }
//    val resultMessage = remember { mutableStateOf("") }
//    val resultColor = remember { mutableStateOf(Color.Black) }
//    val frogImage = remember { mutableStateOf(0) }
//    val gameCompleted = remember { mutableStateOf(false) }
//    val currentContext = LocalContext.current
//
//    // Check win conditions immediately after scoring is completed
//    if (gameState.value.scoringCompleted) {
//        if (gameState.value.playerScore >= 101 && gameState.value.computerScore < 101) {
//            humanWins++
//            showResultDialog.value = true
//            resultMessage.value = "You Win!"
//            resultColor.value = Green
//            frogImage.value = R.drawable.happy_frog
//            Log.d("res", "player wins")
//        }
//        else if (gameState.value.computerScore >= 101 && gameState.value.playerScore < 101) {
//            computerWins++
//            showResultDialog.value = true
//            resultMessage.value = "You Lose!"
//            resultColor.value = Color.Red
//            frogImage.value = R.drawable.sad_frog
//            Log.d("res", "computer wins")
//        }
//        else if (gameState.value.playerScore >= 101 && gameState.value.computerScore >= 101) {
//            // Compare scores to determine winner
//            if (gameState.value.playerScore > gameState.value.computerScore) {
//                humanWins++
//                resultMessage.value = "You Win!"
//                resultColor.value = Color.Green
//                frogImage.value = R.drawable.happy_frog
//                Log.d("res", "player wins ")
//            } else if (gameState.value.computerScore > gameState.value.playerScore) {
//                computerWins++
//                resultMessage.value = "You Lose!"
//                resultColor.value = Red
//                frogImage.value = R.drawable.sad_frog
//                Log.d("res", "computer wins on tie")
//            } else {
//                // Exactly equal scores is a true tie
//                resultMessage.value = "It's a Tie!"
//                resultColor.value = DarkYellow
//                frogImage.value = R.drawable.confused
//                Log.d("res", "true tie game")
//            }
//
//            showResultDialog.value = true
//        }
//    }
//    BackHandler(enabled = showResultDialog.value) {
//
//        val intent = Intent(currentContext, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        currentContext.startActivity(intent)
//    }
//
//    // Result Dialog
//    if (showResultDialog.value) {
//
//        AlertDialog(
//            onDismissRequest = {
//
//                showResultDialog.value = false
//                val intent = Intent(currentContext, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                currentContext.startActivity(intent)
//
//            },
//            title = null,
//            text = {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = frogImage.value),
//                        contentDescription = resultMessage.value,
//                        modifier = Modifier
//                            .size(200.dp)
//                            .padding(bottom = 16.dp)
//                    )
//                    Text(
//                        text = resultMessage.value,
//                        color = resultColor.value,
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//
////                    Text(
////                        text = "You: ${gameState.value.playerScore},\n Computer: ${gameState.value.computerScore}",
////                        textAlign = TextAlign.Center,
////                        color = BrownPrimary
////                    )
//
//                }
//            },
//            confirmButton = {
////                TextButton(
////                    onClick = {
////                        showResultDialog.value = false
////                        gameState.value = GameState()
////                    }
////                ) {
////                    Text("New Game")
////                }
//            }
//        )
//        if (gameCompleted.value) {
//            // Disable all control panel actions
//            ControlPanel(
//                hasThrown = false,
//                remainingPlayerRerolls = 0,
//                inRerollMode = false,
//                scoringCompleted = true,
//                onThrow = {},
//                onScore = {},
//                onReroll = {},
//                modifier = Modifier
//            )
//        }
//
//
//
//    }
//
//
//}

//THIS IS THE no RIGHT ONE
//@Composable
//fun Result(gameState: MutableState<GameState>) {
//    val showResultDialog = remember { mutableStateOf(false) }
//    val resultMessage = remember { mutableStateOf("") }
//    val resultColor = remember { mutableStateOf(Color.Black) }
//    val frogImage = remember { mutableStateOf(0) }
//    val gameCompleted = remember { mutableStateOf(false) }
//    val currentContext = LocalContext.current
//
//    // Tie-breaker specific states
//    val isTieBreaker = remember { mutableStateOf(false) }
//    val tiePlayerScore = remember { mutableStateOf(0) }
//    val tieComputerScore = remember { mutableStateOf(0) }
//
//    // Check win conditions immediately after scoring is completed
//    if (gameState.value.scoringCompleted) {
//        if (gameState.value.playerScore >= 101 && gameState.value.computerScore < 101) {
//            humanWins++
//            showResultDialog.value = true
//            resultMessage.value = "You Win!"
//            resultColor.value = Green
//            frogImage.value = R.drawable.happy_frog
//            Log.d("res", "player wins")
//        }
//        else if (gameState.value.computerScore >= 101 && gameState.value.playerScore < 101) {
//            computerWins++
//            showResultDialog.value = true
//            resultMessage.value = "You Lose!"
//            resultColor.value = Color.Red
//            frogImage.value = R.drawable.sad_frog
//            Log.d("res", "computer wins")
//        }
//        else if (gameState.value.playerScore >= 101 && gameState.value.computerScore >= 101) {
//            // Both players reached 101 - start tie-breaker logic
//            if (gameState.value.playerScore == gameState.value.computerScore) {
//                isTieBreaker.value = true
//
//                // Generate single roll for both player and computer
//                val playerTieRoll = generateDice().sum()
//                val computerTieRoll = generateDice().sum()
//
//                tiePlayerScore.value = playerTieRoll
//                tieComputerScore.value = computerTieRoll
//
//                // Determine winner of tie-breaker
//                if (playerTieRoll > computerTieRoll) {
//                    humanWins++
//                    resultMessage.value = "You Win the Tie-Breaker!"
//                    resultColor.value = Green
//                    frogImage.value = R.drawable.happy_frog
//                    Log.d("res", "player wins tie-breaker")
//                } else if (computerTieRoll > playerTieRoll) {
//                    computerWins++
//                    resultMessage.value = "You Lose the Tie-Breaker!"
//                    resultColor.value = Red
//                    frogImage.value = R.drawable.sad_frog
//                    Log.d("res", "computer wins tie-breaker")
//                } else {
//                    // Recursive tie-breaker if scores are still equal
//                    val nextPlayerTieRoll = generateDice().sum()
//                    val nextComputerTieRoll = generateDice().sum()
//
//                    tiePlayerScore.value = nextPlayerTieRoll
//                    tieComputerScore.value = nextComputerTieRoll
//
//                    if (nextPlayerTieRoll > nextComputerTieRoll) {
//                        humanWins++
//                        resultMessage.value = "You Win the Tie-Breaker!"
//                        resultColor.value = Green
//                        frogImage.value = R.drawable.happy_frog
//                        Log.d("res", "player wins second tie-breaker")
//                    } else if (nextComputerTieRoll > nextPlayerTieRoll) {
//                        computerWins++
//                        resultMessage.value = "You Lose the Tie-Breaker!"
//                        resultColor.value = Red
//                        frogImage.value = R.drawable.sad_frog
//                        Log.d("res", "computer wins second tie-breaker")
//                    } else {
//                        // If still a tie, declare a true tie
//                        resultMessage.value = "It's a Tie!"
//                        resultColor.value = DarkYellow
//                        frogImage.value = R.drawable.confused
//                        Log.d("res", "true tie game")
//                    }
//                }
//
//                showResultDialog.value = true
//            } else if (gameState.value.playerScore > gameState.value.computerScore) {
//                humanWins++
//                resultMessage.value = "You Win!"
//                resultColor.value = Color.Green
//                frogImage.value = R.drawable.happy_frog
//                showResultDialog.value = true
//                Log.d("res", "player wins on points")
//            } else {
//                computerWins++
//                resultMessage.value = "You Lose!"
//                resultColor.value = Red
//                frogImage.value = R.drawable.sad_frog
//                showResultDialog.value = true
//                Log.d("res", "computer wins on points")
//            }
//        }
//    }
//
//    // Back handler for result dialog
//    BackHandler(enabled = showResultDialog.value) {
//        val intent = Intent(currentContext, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        currentContext.startActivity(intent)
//    }
//
//    // Result Dialog
//    if (showResultDialog.value) {
//        AlertDialog(
//            onDismissRequest = {
//                showResultDialog.value = false
//                val intent = Intent(currentContext, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                currentContext.startActivity(intent)
//            },
//            title = null,
//            text = {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = frogImage.value),
//                        contentDescription = resultMessage.value,
//                        modifier = Modifier
//                            .size(200.dp)
//                            .padding(bottom = 16.dp)
//                    )
//                    Text(
//                        text = resultMessage.value,
//                        color = resultColor.value,
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//
//                    // Add tie-breaker details if applicable
//                    if (isTieBreaker.value) {
//                        Text(
//                            text = "Tie-Breaker Scores:\nYou: ${tiePlayerScore.value}, Computer: ${tieComputerScore.value}",
//                            textAlign = TextAlign.Center,
//                            color = BrownPrimary,
//                            modifier = Modifier.padding(top = 8.dp)
//                        )
//                    }
//                }
//            },
//            confirmButton = {
//                // Optional: Add a confirm button if needed
//            }
//        )
//
//        if (gameCompleted.value) {
//            // Disable all control panel actions
//            ControlPanel(
//                hasThrown = false,
//                remainingPlayerRerolls = 0,
//                inRerollMode = false,
//                scoringCompleted = true,
//                onThrow = {},
//                onScore = {},
//                onReroll = {},
//                modifier = Modifier
//            )
//        }
//    }
//}

