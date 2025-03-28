package com.example.dice_game

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.dice_game.components.Result
import com.example.dice_game.components.GameRules
import com.example.dice_game.components.GameScreenBackground
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

var computerWins = 0
var humanWins = 0

@Composable
fun Game() {
    // Game state variables
    var playerDice = rememberSaveable { mutableStateOf(List(5) { (1..6).random() }) }
    var computerDice = rememberSaveable { mutableStateOf(List(5) { (1..6).random() }) }
    var playerRerolls = rememberSaveable { mutableStateOf(2) }
    var computerRerolls = rememberSaveable { mutableStateOf(2) }
    var inRerollMode = rememberSaveable { mutableStateOf(false) }
    var selectedDice = rememberSaveable { mutableStateOf(List(5) { false }) }
    var computerDiceThrown = rememberSaveable { mutableStateOf(false) }

    var playerScore = rememberSaveable { mutableStateOf(0) }
    var computerScore = rememberSaveable { mutableStateOf(0) }
    var hasThrown = rememberSaveable { mutableStateOf(false) }

    var scoringCompleted = rememberSaveable { mutableStateOf(false) }
    var isTieBreaker = rememberSaveable { mutableStateOf(false) }
    var targetScore = rememberSaveable { mutableStateOf(101) }
    var isHardMode = rememberSaveable { mutableStateOf(false) }

    var isInitialSetup = rememberSaveable { mutableStateOf(true) }
    Log.d("GameMode", "Current Game Mode: ${if (isHardMode.value) "HARD" else "EASY"}")

    // Handler for delayed actions
    var handler = remember { Handler(Looper.getMainLooper()) }
    var isTargetScoreApplied = rememberSaveable { mutableStateOf(false) }

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
                    targetScore.value = score
                    isInitialSetup.value = false
                    isTargetScoreApplied.value = true
                },
                onTargetScoreApplied = { applied ->
                    isTargetScoreApplied.value = applied
                },
                onGameModeChanged = { hardMode ->
                    isHardMode.value = hardMode
                }
            )
        } else {
            if (!hasThrown.value) {
                GameRules(
                    isTargetScoreApplied = isTargetScoreApplied.value,
                    onWinningScoreSet = { score ->
                        targetScore.value = score
                        isTargetScoreApplied.value = true
                    },
                    onTargetScoreApplied = { applied ->
                        isTargetScoreApplied.value = applied
                    },
                    onGameModeChanged = { hardMode ->
                        isHardMode.value = hardMode
                    }
                )
            } else {
                GameContent(
                    playerDice = playerDice.value,
                    computerDice = computerDice.value,
                    playerRerolls = playerRerolls.value,
                    computerRerolls = computerRerolls.value,
                    computerDiceThrown = computerDiceThrown.value,
                    inRerollMode = inRerollMode.value,
                    playerScore = playerScore.value,
                    computerScore = computerScore.value,
                    selectedDice = selectedDice.value,
                    onDiceSelected = { index ->
                        if (inRerollMode.value) {
                            val updatedSelectedDice = selectedDice.value.toMutableList()
                            updatedSelectedDice[index] = !updatedSelectedDice[index]
                            selectedDice.value = updatedSelectedDice
                        }
                    }
                )
            }
        }

        Result(
            playerScore = playerScore,
            computerScore = computerScore,
            targetScore = targetScore,
            scoringCompleted = scoringCompleted,
            isTieBreaker = isTieBreaker,
            playerDice = playerDice,
            computerDice = computerDice,
            computerDiceThrown = computerDiceThrown,
            inRerollMode = inRerollMode,
            playerRerolls = playerRerolls,
            computerRerolls = computerRerolls,
            selectedDice = selectedDice,
            isHardMode = isHardMode
        )

        // Bottom control panel
        ControlPanel(
            hasThrown = hasThrown.value,
            remainingPlayerRerolls = playerRerolls.value,
            inRerollMode = inRerollMode.value,
            scoringCompleted = scoringCompleted.value,
            isTieBreaker = isTieBreaker.value,
            onThrow = {
                if (isTargetScoreApplied.value) {
                    handleThrowAction(
                        playerDice, computerDice, playerRerolls, computerRerolls,
                        inRerollMode, selectedDice, computerDiceThrown, playerScore,
                        computerScore, hasThrown, scoringCompleted, isTieBreaker,
                        targetScore, isHardMode, handler
                    )
                }
            },
            onScore = {
                calculateScore(
                    playerDice, computerDice, playerScore, computerScore,
                    scoringCompleted, isTieBreaker, handler, isHardMode,
                    playerRerolls, computerRerolls
                )
            },
            onReroll = {
                if (playerRerolls.value > 0 && !inRerollMode.value) {
                    inRerollMode.value = true
                    selectedDice.value = List(5) { false }
                    playerRerolls.value = playerRerolls.value - 1

                    if (playerRerolls.value == 0) {
                        handler.postDelayed({
                            calculateScore(
                                playerDice, computerDice, playerScore, computerScore,
                                scoringCompleted, isTieBreaker, handler, isHardMode,
                                playerRerolls, computerRerolls
                            )
                        }, 1500)
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private fun handleThrowAction(
    playerDice: MutableState<List<Int>>,
    computerDice: MutableState<List<Int>>,
    playerRerolls: MutableState<Int>,
    computerRerolls: MutableState<Int>,
    inRerollMode: MutableState<Boolean>,
    selectedDice: MutableState<List<Boolean>>,
    computerDiceThrown: MutableState<Boolean>,
    playerScore: MutableState<Int>,
    computerScore: MutableState<Int>,
    hasThrown: MutableState<Boolean>,
    scoringCompleted: MutableState<Boolean>,
    isTieBreaker: MutableState<Boolean>,
    targetScore: MutableState<Int>,
    isHardMode: MutableState<Boolean>,
    handler: Handler
) {
    if (!hasThrown.value) {
        // First throw
        playerDice.value = generateDice()
        computerDice.value = generateDice()
        selectedDice.value = List(5) { false }
        computerDiceThrown.value = false
        hasThrown.value = true

        scheduleComputerActions(
            playerDice, computerDice, playerRerolls, computerRerolls,
            inRerollMode, selectedDice, computerDiceThrown, playerScore,
            computerScore, hasThrown, scoringCompleted, isTieBreaker,
            targetScore, isHardMode, handler
        )
    } else if (inRerollMode.value) {
        rerollPlayerDice(playerDice, selectedDice, inRerollMode, playerRerolls)

        if (playerRerolls.value == 0) {
            handler.postDelayed({
                calculateScore(
                    playerDice, computerDice, playerScore, computerScore,
                    scoringCompleted, isTieBreaker, handler, isHardMode,
                    playerRerolls, computerRerolls
                )
            }, 2000)
        }
    } else {
        startNewTurn(
            playerDice, computerDice, playerRerolls, computerRerolls,
            inRerollMode, selectedDice, computerDiceThrown, playerScore,
            computerScore, hasThrown, scoringCompleted, isTieBreaker,
            handler, isHardMode
        )
    }
}

private fun scheduleComputerActions(
    playerDice: MutableState<List<Int>>,
    computerDice: MutableState<List<Int>>,
    playerRerolls: MutableState<Int>,
    computerRerolls: MutableState<Int>,
    inRerollMode: MutableState<Boolean>,
    selectedDice: MutableState<List<Boolean>>,
    computerDiceThrown: MutableState<Boolean>,
    playerScore: MutableState<Int>,
    computerScore: MutableState<Int>,
    hasThrown: MutableState<Boolean>,
    scoringCompleted: MutableState<Boolean>,
    isTieBreaker: MutableState<Boolean>,
    targetScore: MutableState<Int>,
    isHardMode: MutableState<Boolean>,
    handler: Handler
) {
    handler.postDelayed({
        computerDiceThrown.value = true

        handler.postDelayed({
            computerTurn(
                playerDice, computerDice, playerRerolls, computerRerolls,
                inRerollMode, selectedDice, computerDiceThrown, playerScore,
                computerScore, hasThrown, scoringCompleted, isTieBreaker,
                targetScore, isHardMode, handler
            )
        }, 300)
    }, 300)
}

private fun rerollPlayerDice(
    playerDice: MutableState<List<Int>>,
    selectedDice: MutableState<List<Boolean>>,
    inRerollMode: MutableState<Boolean>,
    playerRerolls: MutableState<Int>
) {
    val newDice = playerDice.value.mapIndexed { index, value ->
        if (selectedDice.value[index]) value else (1..6).random()
    }
    playerDice.value = newDice
    selectedDice.value = List(5) { false }
    inRerollMode.value = false
}

private fun startNewTurn(
    playerDice: MutableState<List<Int>>,
    computerDice: MutableState<List<Int>>,
    playerRerolls: MutableState<Int>,
    computerRerolls: MutableState<Int>,
    inRerollMode: MutableState<Boolean>,
    selectedDice: MutableState<List<Boolean>>,
    computerDiceThrown: MutableState<Boolean>,
    playerScore: MutableState<Int>,
    computerScore: MutableState<Int>,
    hasThrown: MutableState<Boolean>,
    scoringCompleted: MutableState<Boolean>,
    isTieBreaker: MutableState<Boolean>,
    handler: Handler,
    isHardMode: MutableState<Boolean>
) {
    playerDice.value = generateDice()
    computerDice.value = generateDice()
    selectedDice.value = List(5) { false }
    playerRerolls.value = 2
    computerRerolls.value = 2
    computerDiceThrown.value = false
    scoringCompleted.value = false
    hasThrown.value = true

    handler.postDelayed({
        computerDiceThrown.value = true

        handler.postDelayed({
            computerTurn(
                playerDice, computerDice, playerRerolls, computerRerolls,
                inRerollMode, selectedDice, computerDiceThrown, playerScore,
                computerScore, hasThrown, scoringCompleted, isTieBreaker,
                mutableStateOf(101), isHardMode, handler
            )
        }, 300)
    }, 500)
}

fun generateDice(): List<Int> = List(5) { (1..6).random() }

private fun calculateScore(
    playerDice: MutableState<List<Int>>,
    computerDice: MutableState<List<Int>>,
    playerScore: MutableState<Int>,
    computerScore: MutableState<Int>,
    scoringCompleted: MutableState<Boolean>,
    isTieBreaker: MutableState<Boolean>,
    handler: Handler,
    isHardMode: MutableState<Boolean>,
    playerRerolls: MutableState<Int>,
    computerRerolls: MutableState<Int>
) {
    if (computerRerolls.value > 0) {
        Log.d("DiceGame", "Before scoring, computer reconsidering rerolls...")

        handler.postDelayed({
            computerTurn(
                playerDice, computerDice, playerRerolls, computerRerolls,
                mutableStateOf(false), mutableStateOf(List(5) { false }),
                mutableStateOf(true), playerScore, computerScore,
                mutableStateOf(true), scoringCompleted, isTieBreaker,
                mutableStateOf(101), isHardMode, handler
            )
            finalizeScore(playerDice, computerDice, playerScore, computerScore, scoringCompleted)
        }, 1000)
    } else {
        finalizeScore(playerDice, computerDice, playerScore, computerScore, scoringCompleted)
    }
}

private fun finalizeScore(
    playerDice: MutableState<List<Int>>,
    computerDice: MutableState<List<Int>>,
    playerScore: MutableState<Int>,
    computerScore: MutableState<Int>,
    scoringCompleted: MutableState<Boolean>
) {
    playerScore.value += playerDice.value.sum()
    computerScore.value += computerDice.value.sum()
    scoringCompleted.value = true
}

private fun computerTurn(
    playerDice: MutableState<List<Int>>,
    computerDice: MutableState<List<Int>>,
    playerRerolls: MutableState<Int>,
    computerRerolls: MutableState<Int>,
    inRerollMode: MutableState<Boolean>,
    selectedDice: MutableState<List<Boolean>>,
    computerDiceThrown: MutableState<Boolean>,
    playerScore: MutableState<Int>,
    computerScore: MutableState<Int>,
    hasThrown: MutableState<Boolean>,
    scoringCompleted: MutableState<Boolean>,
    isTieBreaker: MutableState<Boolean>,
    targetScore: MutableState<Int>,
    isHardMode: MutableState<Boolean>,
    handler: Handler
) {
    Log.d("DiceGame", "Computer turn started. Remaining rerolls: ${computerRerolls.value}")
    Log.d("DiceGame", "Current computer dice: ${computerDice.value}")

    // Only allow computer to reroll if it has remaining rerolls
    if (computerRerolls.value > 0) {
        // Random decision whether to reroll (50% chance)
        val willReroll = if (isHardMode.value) {
            // In hard mode, always try to reroll strategically
            true
        } else {
            // In easy mode, random chance to reroll
            (0..1).random() == 1
        }
        Log.d("DiceGame", "Computer decided to reroll: $willReroll")

        if (willReroll) {
            if (isHardMode.value) {
                // Use hard mode specific reroll strategy
                val newDice = computeHardModeTurn(
                    computerDice, computerScore, playerScore, targetScore, computerRerolls
                )

                // Log each die decision
                computerDice.value.forEachIndexed { index, value ->
                    Log.d("HardMode", "Die #${index+1}: ${value} -> ${newDice[index]}")
                }

                // Update computer dice and rerolls
                computerDice.value = newDice
                computerRerolls.value = computerRerolls.value - 1

                // Consider second reroll in hard mode
                if (computerRerolls.value > 0) {
                    handler.postDelayed({
                        val secondRerollDice = computeHardModeSecondReroll(
                            computerDice, computerScore, playerScore, targetScore
                        )

                        // Log second reroll decisions
                        computerDice.value.forEachIndexed { index, value ->
                            Log.d("HardMode", "Second Reroll - Die #${index+1}: ${value} -> ${secondRerollDice[index]}")
                        }

                        computerDice.value = secondRerollDice
                        computerRerolls.value = computerRerolls.value - 1
                    }, 1000)
                }
            } else {
                // Easy mode - existing random reroll logic
                performComputerReroll(computerDice, computerRerolls)

                // Check if computer wants to do a second reroll after a delay
                if (computerRerolls.value > 0) {
                    handler.postDelayed({
                        considerSecondReroll(computerDice, computerRerolls)
                    }, 1000)
                }
            }
        } else {
            // Computer decided not to reroll
            Log.d("DiceGame", "Computer decided to keep all dice: ${computerDice.value}")
        }
    } else {
        Log.d("DiceGame", "Computer has no rerolls available, keeping current dice: ${computerDice.value}")
    }
}

private fun performComputerReroll(
    computerDice: MutableState<List<Int>>,
    computerRerolls: MutableState<Int>
) {
    val currentDice = computerDice.value

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
    computerDice.value = newDice
    computerRerolls.value = computerRerolls.value - 1
}

private fun considerSecondReroll(
    computerDice: MutableState<List<Int>>,
    computerRerolls: MutableState<Int>
) {
    // Decide if computer wants to reroll again
    val willRerollAgain = (0..1).random() == 1
    Log.d("DiceGame", "Computer second reroll decision: $willRerollAgain")

    if (willRerollAgain && computerRerolls.value > 0) {
        val currentDice = computerDice.value

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
        computerDice.value = newDice
        computerRerolls.value = computerRerolls.value - 1
    } else {
        Log.d("DiceGame", "Computer decided not to use second reroll")
    }

    Log.d("DiceGame", "Computer rerolls left after second decision: ${computerRerolls.value}")
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



// COMPUTER'S SECOND STRATEGY
// When the user presses the button in the game, rules hard mode is enabled.
// The strategy adapts based on:
// - Current computer score
// - Player's score
// - Distance from winning score

// Key Principles:
// - Manage the risk based on the score gap

// When Significantly Behind:
//   - Aggressive rerolling approach
//   - Prioritize replacing low-value dice (below 4)
//   - High-risk, high-reward strategy to quickly close the score gap

// When Moderately Behind:
//   - Selective rerolling
//   - Focus on replacing dice with values below 3

// When Close to Target or Leading:
//   - Minimal or no rerolling
//   - Preserve current dice to maintain advantage

// First Reroll Strategy:
//   - Large Score Gap (>50% of target): Reroll all dice below 4
//     - High-risk catch-up mode
//   - Moderate Gap (>30% of target): Reroll dice below 3
//     - Strategic improvement
//   - Small Gap (<15% of target): No rerolling
//     - Preserve current dice

// Second Reroll:
//   - Similar to first reroll but more careful approach
//   - Focus on extreme catch-up or minimal changes

// Advantages:
//   - Dynamic response to game state
//   - Balanced risk approach
//   - Probabilistic decision-making

fun computeHardModeTurn(
    computerDice: MutableState<List<Int>>,
    computerScore: MutableState<Int>,
    playerScore: MutableState<Int>,
    targetScore: MutableState<Int>,
    computerRerolls: MutableState<Int>
): List<Int> {
    val currentDice = computerDice.value
    val currentScore = computerScore.value
    val playerCurrentScore = playerScore.value
    val target = targetScore.value
    val remainingRerolls = computerRerolls.value

    // Calculate score gaps
    val scoreGap = target - currentScore
    val playerScoreGap = target - playerCurrentScore


    // Decide on the reroll strategy based on the score gap
    val rerollStrategy = when {
        // big gap catchup needed - reroll all dice below 4 (aggressive)
        scoreGap > target * 0.5 -> {
            currentDice.map { value -> value < 4 }
        }
        // Significant gap - reroll dice with values less than 3 (strategic)
        scoreGap > target * 0.3 -> {
            currentDice.map { value -> value < 3 }
        }
        // Moderate gap - reroll the lowest value dice (selective)
        scoreGap > target * 0.15 -> {
            currentDice.map { value -> value == currentDice.minOrNull() }
        }
        // Minimal gap or leading - no rerolling (conservative)
        else -> {
            List(5) { false }
        }
    }

    // Log reroll decisions for debugging
    rerollStrategy.forEachIndexed { index, shouldReroll ->
        Log.d("HardMode", "Die #${index+1}: ${currentDice[index]} - ${if (shouldReroll) "REROLL" else "KEEP"}")
    }

    // Perform reroll based on strategy
    val newDice = currentDice.mapIndexed { index, value ->
        //if the die needs to be rerolled
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

fun computeHardModeSecondReroll(
    computerDice: MutableState<List<Int>>,
    computerScore: MutableState<Int>,
    playerScore: MutableState<Int>,
    targetScore: MutableState<Int>
): List<Int> {
    val currentDice = computerDice.value
    val currentScore = computerScore.value
    val playerCurrentScore = playerScore.value
    val target = targetScore.value

    // Calculate score gaps
    val scoreGap = target - currentScore
    val playerScoreGap = target - playerCurrentScore

    // Determine second reroll strategy
    val secondRerollStrategy = when {
        // High risk - reroll lowest value dice to improve chances (aggressive)
        scoreGap > target * 0.4 -> {
            Log.d("HardMode", "SECOND REROLL: High Risk Catchup")
            // For each die, check if it is the lowest value and Reroll the die with the minimum value
            currentDice.map { value -> value == currentDice.minOrNull() }
        }
        // Moderate risk - reroll dice with values less than 3 (strategic)
        scoreGap > target * 0.2 -> {
            Log.d("HardMode", "SECOND REROLL: Moderate Risk")
            //for each die checking the value's less than 3  if it is, reroll
            currentDice.map { value -> value < 3 }
        }
        // Minimal gap - low risk - no rerolling
        else -> {
            Log.d("HardMode", "SECOND REROLL: Low Risk")
            List(5) { false }
        }
    }

    // Perform second reroll
    val newDice = currentDice.mapIndexed { index, value ->
        if (secondRerollStrategy[index]) {
            val newValue = (1..6).random()
            Log.d("HardMode", "Second Reroll - Die #${index+1}: $value -> $newValue")
            newValue
        } else {
            //keep the val if no reroll needed
            value
        }
    }

    // Log final dice state
    Log.d("HardMode", "Final Second Reroll Dice State: $newDice")
    // Return the new dice state
    return newDice
}


