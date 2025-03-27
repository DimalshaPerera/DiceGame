

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
import com.example.dice_game.components.GameResultDialog
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
    val isInitialSetup = remember { mutableStateOf(true) }

    // Handler for delayed actions
    val handler = remember { Handler(Looper.getMainLooper()) }
    val isTargetScoreApplied = remember { mutableStateOf(false) }

    // Background
    GameScreenBackground()

    // Main game content
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "H:${humanWins}/C:${computerWins}",
            modifier = Modifier.padding(20.dp),
            color = White,
            fontSize = 20.sp
        )

        AnimatedTitle(isGameScreen = true)

        // Game content area
        if (isInitialSetup.value) {
            GameRules(
                onWinningScoreSet = { score ->
                    // Update game state with chosen winning score
                    gameState.value = gameState.value.copy(targetScore = score)
                    isInitialSetup.value = false
                    isTargetScoreApplied.value = true
                },
                onTargetScoreApplied = { applied ->
                    isTargetScoreApplied.value = applied
                }
            )
        }
            else {
                if (!gameState.value.hasThrown) {
                    GameRules(
                        isTargetScoreApplied = isTargetScoreApplied.value,
                        onWinningScoreSet = { score ->
                            // Allow changing target score before first throw
                            gameState.value = gameState.value.copy(targetScore = score)
                            isTargetScoreApplied.value = true
                        },
                        onTargetScoreApplied = { applied ->
                            isTargetScoreApplied.value = applied
                        }
                    )
                } else {
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
                                // Toggle selection status of the dice
                                updatedSelectedDice[index] = !updatedSelectedDice[index]
                                gameState.value = gameState.value.copy(selectedDice = updatedSelectedDice)
                            }
                        }
                    )
                }
            }

        Result(gameState)

        // Bottom control panel - Now always visible
        ControlPanel(
            hasThrown = gameState.value.hasThrown,
            remainingPlayerRerolls = gameState.value.playerRerolls,
            inRerollMode = gameState.value.inRerollMode,
            scoringCompleted = gameState.value.scoringCompleted,
            isTieBreaker = gameState.value.isTieBreaker,
            onThrow = {
                // can only throw when target is applied
                if (isTargetScoreApplied.value) {
                    handleThrowAction(gameState, handler)
                }
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
    val hasUpdatedWin = remember { mutableStateOf(false) }

    // BackHandler to handle back press when result dialog is shown
    BackHandler(enabled = showResultDialog.value) {
        val intent = Intent(currentContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        currentContext.startActivity(intent)
    }

    // Check win conditions immediately after scoring is completed
    if (gameState.value.scoringCompleted && !hasUpdatedWin.value) {
        if (gameState.value.playerScore >= gameState.value.targetScore && gameState.value.playerScore > gameState.value.computerScore) {
            humanWins++
            showResultDialog.value = true
            resultMessage.value = "You Win!"
            resultColor.value = Green
            frogImage.value = R.drawable.happy_frog
            Log.d("res", "player wins")
            hasUpdatedWin.value = true
        } else if (gameState.value.computerScore >=gameState.value.targetScore && gameState.value.computerScore > gameState.value.playerScore) {
            computerWins++
            showResultDialog.value = true
            resultMessage.value = "You Lose!"
            resultColor.value = Color.Red
            frogImage.value = R.drawable.sad_frog
            Log.d("res", "computer wins")

            hasUpdatedWin.value = true
        } else if (gameState.value.playerScore >= gameState.value.targetScore && gameState.value.computerScore >= gameState.value.targetScore) {
            showTieFrogDialog.value = true
            resultMessage.value = "     It's a Tie! \n Rolling again"
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
                    selectedDice = List(5) { false },
                    isTieBreaker = true,
                    playerScore = playerTieBreakDice.sum(),
                    computerScore = computerTieBreakDice.sum()
                )
            }, 5000) // 5 seconds
        }
    }

    // Automatically handle tie-breaker result
    if (isTieBreaker.value && gameState.value.isTieBreaker) {
        val playerTieBreakScore = gameState.value.playerScore
        val computerTieBreakScore = gameState.value.computerScore

        Log.d("TieBreaker", "Tie-Break Scores - Player: $playerTieBreakScore, Computer: $computerTieBreakScore")

        if (playerTieBreakScore > computerTieBreakScore) {
            handler.postDelayed({
                humanWins++
                showResultDialog.value = true
                resultMessage.value = "You Win!"
                resultColor.value = Green
                frogImage.value = R.drawable.happy_frog
                isTieBreaker.value = false
                hasUpdatedWin.value = true
            }, 5000)
        } else if (computerTieBreakScore > playerTieBreakScore) {
            handler.postDelayed({
                computerWins++
                showResultDialog.value = true
                resultMessage.value = "You Lose!"
                resultColor.value = Color.Red
                frogImage.value = R.drawable.sad_frog
                isTieBreaker.value = false
                hasUpdatedWin.value = true
            }, 5000)
        } else {
            showTieFrogDialog.value = true
            resultMessage.value = "     Another Tie! \n Rolling again"
            resultColor.value = DarkYellow
            frogImage.value = R.drawable.confused

            // Schedule next tie-breaker round
            handler.postDelayed({
                showTieFrogDialog.value = false

                // Generate new dice for next tie-breaker round
                val nextPlayerTieBreakDice = generateDice()
                val nextComputerTieBreakDice = generateDice()

                // Update game state to continue tie-breaker
                gameState.value = gameState.value.copy(
                    playerDice = nextPlayerTieBreakDice,
                    computerDice = nextComputerTieBreakDice,
                    computerDiceThrown = true,
                    playerRerolls = 0,  // Prevent player from rerolling
                    computerRerolls = 0,  // Prevent computer from rerolling
                    inRerollMode = false,
                    scoringCompleted = false,
                    selectedDice = List(5) { false },
                    isTieBreaker = true,
                    playerScore = nextPlayerTieBreakDice.sum(),
                    computerScore = nextComputerTieBreakDice.sum()
                )
            }, 5000)
        }
    }

    // Use the new GameResultDialog for tie frog dialog
    GameResultDialog(
        showDialog = showTieFrogDialog.value,
        message = resultMessage.value,
        color = resultColor.value,
        frogImage = frogImage.value,
        onDismiss = {
            showTieFrogDialog.value = false
        }
    )


    GameResultDialog(
        showDialog = showResultDialog.value,
        message = resultMessage.value,
        color = resultColor.value,
        frogImage = frogImage.value,
        onDismiss = {
            val intent = Intent(currentContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            currentContext.startActivity(intent)
        }
    )
}


/**
 * Hard Mode Strategy for Computer Player
 *
 * Strategy to catch up when behind in the game score
 */
fun computeHardModeTurn(
    gameState: MutableState<GameState>,
    handler: Handler
): List<Int> {
    val currentDice = gameState.value.computerDice
    val scoreGap = gameState.value.targetScore - gameState.value.computerScore
    val remainingRerolls = gameState.value.computerRerolls

    // Points to determine strategy levels
    val NEED_CATCH_UP = gameState.value.targetScore * 0.3 // 30% of target score
    val URGENT_CATCH_UP = gameState.value.targetScore * 0.15 // 15% of target score

    // Current dice total
    val currentDiceTotal = currentDice.sum()

    // Log decision factors
    Log.d("HardMode", "Score Gap: $scoreGap")
    Log.d("HardMode", "Current Dice Total: $currentDiceTotal")
    Log.d("HardMode", "Remaining Rerolls: $remainingRerolls")

    // Determine reroll strategy based on score gap
    val rerollStrategy = when {
        // Most urgent situation - reroll most dice
        scoreGap > URGENT_CATCH_UP && remainingRerolls > 0 -> {
            Log.d("HardMode", "CRITICAL MODE: Reroll most dice")
            List(5) { it < 4 }
        }

        // Significant gap - selective risky reroll
        scoreGap > NEED_CATCH_UP && remainingRerolls > 0 -> {
            Log.d("HardMode", "HIGH RISK MODE: Selective risky reroll")
            currentDice.mapIndexed { index, value ->
                value < 4 || (index % 2 == 0 && value < 5)
            }
        }

        // Moderate gap - conservative selective reroll
        scoreGap > (NEED_CATCH_UP / 2) && remainingRerolls > 0 -> {
            Log.d("HardMode", "MODERATE MODE: Conservative reroll")
            currentDice.map { it == currentDice.minOrNull() }
        }

        // Not behind enough to take risks
        else -> {
            Log.d("HardMode", "LOW RISK MODE: Keep most dice")
            List(5) { false }
        }
    }

    // Log reroll decisions
    rerollStrategy.forEachIndexed { index, shouldReroll ->
        Log.d("HardMode", "Die #${index+1}: ${currentDice[index]} - ${if (shouldReroll) "REROLL" else "KEEP"}")
    }

    // Perform reroll based on strategy
    val newDice = currentDice.mapIndexed { index, value ->
        if (rerollStrategy[index]) {
            val newValue = (1..6).random()
            Log.d("HardMode", "Rerolled Die #${index+1}: $value -> $newValue")
            newValue
        } else {
            value
        }
    }

    // Log final dice state
    Log.d("HardMode", "Final Dice State: $newDice")

    return newDice
}

/**
 * Perform a second reroll in hard mode if situation is critical
 */
fun computeHardModeSecondReroll(
    gameState: MutableState<GameState>,
    currentDice: List<Int>
): List<Int> {
    val scoreGap = gameState.value.targetScore - gameState.value.computerScore

    // Decide on second reroll based on remaining score gap
    val secondRerollStrategy = when {
        scoreGap > gameState.value.targetScore * 0.15 -> {
            Log.d("HardMode", "SECOND REROLL: High Risk")
            List(5) { it < 3 }
        }
        scoreGap > gameState.value.targetScore * 0.3 -> {
            Log.d("HardMode", "SECOND REROLL: Moderate Risk")
            currentDice.map { it < 4 }
        }
        else -> {
            Log.d("HardMode", "SECOND REROLL: Low Risk")
            List(5) { false }
        }
    }

    // Perform second reroll
    val newDice = currentDice.mapIndexed { index, value ->
        if (secondRerollStrategy[index]) {
            val newValue = (1..6).random()
            Log.d("HardMode", "Second Reroll Die #${index+1}: $value -> $newValue")
            newValue
        } else {
            value
        }
    }

    // Log final dice state
    Log.d("HardMode", "Final Second Reroll Dice State: $newDice")

    return newDice
}

