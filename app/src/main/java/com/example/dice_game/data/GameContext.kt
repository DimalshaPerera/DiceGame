package com.example.dice_game.data

import androidx.compose.runtime.MutableState

data class GameContext(
    val playerRerolls: MutableState<Int>,
    val computerRerolls: MutableState<Int>,
    val computerDiceThrown: MutableState<Boolean>,
    val inRerollMode: MutableState<Boolean>,
    val currentTurn: MutableState<Int>,
    val scoringCompleted: MutableState<Boolean>
)
