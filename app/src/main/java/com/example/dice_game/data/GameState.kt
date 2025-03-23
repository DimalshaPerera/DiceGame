package com.example.dice_game.data

data class GameState(
//generates a random number between 1 and 6 for each element.
//    val playerDice: List<Int> = List(5) { (1..6).random() },
    val playerDice: List<Int> = emptyList(),
    val computerDice: List<Int> = emptyList(),
    val playerScore: Int = 0,
    val computerScore: Int = 0,
    val hasThrown: Boolean = false,
    val selectedDice: List<Boolean> = List(5) { false }
)


