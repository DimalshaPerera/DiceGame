package com.example.dice_game.components

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.dice_game.MainActivity
import com.example.dice_game.R
import com.example.dice_game.computerWins
import com.example.dice_game.generateDice
import com.example.dice_game.humanWins
import com.example.dice_game.ui.theme.DarkYellow
import com.example.dice_game.ui.theme.Green
import com.example.dice_game.ui.theme.Red

@Composable
fun Result(
    playerScore: MutableState<Int>,
    computerScore: MutableState<Int>,
    targetScore: MutableState<Int>,
    scoringCompleted: MutableState<Boolean>,
    isTieBreaker: MutableState<Boolean>,
    playerDice: MutableState<List<Int>>,
    computerDice: MutableState<List<Int>>,
    computerDiceThrown: MutableState<Boolean>,
    inRerollMode: MutableState<Boolean>,
    playerRerolls: MutableState<Int>,
    computerRerolls: MutableState<Int>,
    selectedDice: MutableState<List<Boolean>>,
    isHardMode: MutableState<Boolean>
) {
    val showResultDialog = remember { mutableStateOf(false) }
    val resultMessage = remember { mutableStateOf("") }
    val resultColor = remember { mutableStateOf(Color.Black) }
    val frogImage = remember { mutableStateOf(0) }
    val currentContext = LocalContext.current
    val handler = remember { Handler(Looper.getMainLooper()) }
    val showTieFrogDialog = remember { mutableStateOf(false) }
    val hasUpdatedWin = remember { mutableStateOf(false) }
    val tieBreakRound = remember { mutableStateOf(1) }
    val isProcessingResult = remember { mutableStateOf(false) }

    BackHandler(enabled = showResultDialog.value) {
        val intent = Intent(currentContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        currentContext.startActivity(intent)
    }

    // Regular game result check
    if (scoringCompleted.value && !hasUpdatedWin.value && !isTieBreaker.value && !isProcessingResult.value) {
        isProcessingResult.value = true

        if (playerScore.value >= targetScore.value && playerScore.value > computerScore.value) {
            humanWins++
            resultMessage.value = "You Win!"
            resultColor.value = Green
            frogImage.value = R.drawable.happy_frog
            hasUpdatedWin.value = true
            showResultDialog.value = true
        } else if (computerScore.value >= targetScore.value && computerScore.value > playerScore.value) {
            computerWins++
            resultMessage.value = "You Lose!"
            resultColor.value = Color.Red
            frogImage.value = R.drawable.sad_frog
            hasUpdatedWin.value = true
            showResultDialog.value = true
        } else if (playerScore.value >= targetScore.value && computerScore.value >= targetScore.value) {
            showTieFrogDialog.value = true
            resultMessage.value = "     It's a Tie! \n Rolling again"
            resultColor.value = DarkYellow
            frogImage.value = R.drawable.confused
            tieBreakRound.value = 1

            handler.postDelayed({
                showTieFrogDialog.value = false
                isTieBreaker.value = true
                hasUpdatedWin.value = false
                val playerTieBreakDice = generateDice()
                val computerTieBreakDice = generateDice()
                playerDice.value = playerTieBreakDice
                computerDice.value = computerTieBreakDice
                computerDiceThrown.value = true
                playerRerolls.value = 0
                computerRerolls.value = 0
                inRerollMode.value = false
                selectedDice.value = List(5) { false }
                playerScore.value = playerTieBreakDice.sum()
                computerScore.value = computerTieBreakDice.sum()
                scoringCompleted.value = true
                isProcessingResult.value = false
            }, 3000)
        } else {
            isProcessingResult.value = false
        }
    }

    // Tie-breaker result check
    if (isTieBreaker.value && scoringCompleted.value && !hasUpdatedWin.value && !isProcessingResult.value) {
        isProcessingResult.value = true

        if (playerScore.value > computerScore.value) {
            humanWins++
            hasUpdatedWin.value = true
            isTieBreaker.value = false

            handler.postDelayed({
                resultMessage.value = "You Win!"
                resultColor.value = Green
                frogImage.value = R.drawable.happy_frog
                showResultDialog.value = true
                isProcessingResult.value = false
            }, 3000)

        } else if (computerScore.value > playerScore.value) {
            computerWins++
            hasUpdatedWin.value = true
            isTieBreaker.value = false

            handler.postDelayed({
                resultMessage.value = "You Lose!"
                resultColor.value = Color.Red
                frogImage.value = R.drawable.sad_frog
                showResultDialog.value = true
                isProcessingResult.value = false
            }, 3000)

        } else {
            // Another tie - go to next tie-breaker round
            tieBreakRound.value++
            showTieFrogDialog.value = true
            resultMessage.value = "     Another Tie! \n Rolling again (Round ${tieBreakRound.value})"
            resultColor.value = DarkYellow
            frogImage.value = R.drawable.confused

            handler.postDelayed({
                showTieFrogDialog.value = false
                val nextPlayerTieBreakDice = generateDice()
                val nextComputerTieBreakDice = generateDice()
                playerDice.value = nextPlayerTieBreakDice
                computerDice.value = nextComputerTieBreakDice
                computerDiceThrown.value = true
                playerRerolls.value = 0
                computerRerolls.value = 0
                inRerollMode.value = false
                selectedDice.value = List(5) { false }
                playerScore.value = nextPlayerTieBreakDice.sum()
                computerScore.value = nextComputerTieBreakDice.sum()
                scoringCompleted.value = true
                isProcessingResult.value = false
            }, 3000)
        }
    }

    GameResultDialog(
        showDialog = showTieFrogDialog.value,
        message = resultMessage.value,
        color = resultColor.value,
        frogImage = frogImage.value,
        onDismiss = { showTieFrogDialog.value = false }
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