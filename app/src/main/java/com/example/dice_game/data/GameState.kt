package com.example.dice_game.data

// GameState.kt
data class GameState(




    val playerDice: List<Int> = List(5) { (1..6).random() },
    val computerDice: List<Int> = List(5) { (1..6).random() },
    val playerScore: Int = 0,
    val computerScore: Int = 0,
    val hasThrown: Boolean = false,
    val selectedDice: List<Boolean> = List(5) { false },
    val playerRerolls: Int = 2,
    val computerRerolls: Int = 2,
    val inRerollMode: Boolean = false,
    val computerDiceThrown: Boolean = false,
    val scoringCompleted: Boolean = false,
    val isTieBreaker: Boolean = false,
    val targetScore: Int = 101,
    val isHardMode: Boolean = false,
)
