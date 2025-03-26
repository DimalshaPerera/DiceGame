package com.example.dice_game.data

// GameState.kt
data class GameState(
//    val playerDice: List<Int> = List(5) { 1 },
//    val computerDice: List<Int> = List(5) { 1 },
//    val selectedDice: List<Boolean> = List(5) { false },
//    val playerScore: Int = 0,
//    val computerScore: Int = 0,
//    val playerRerolls: Int = 2,
//    val computerRerolls: Int = 2,
//    val hasThrown: Boolean = false,
//    val computerDiceVisible: Boolean = false,
//    val inRerollMode: Boolean = false,
//    val scoringCompleted: Boolean = false,
//    val currentTurn: Int = 1




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
    val scoringCompleted: Boolean = false

)
