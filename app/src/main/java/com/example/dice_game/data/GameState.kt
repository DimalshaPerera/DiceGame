package com.example.dice_game.data

data class GameState(
//generates a random number between 1 and 6 for each element.
    val playerDice: List<Int> = List(5) { (1..6).random() },
    val computerDice: List<Int> = List(5) { (1..6).random() },
    val hasThrown: Boolean = false,
    val playerScore: Int = 0,
    val computerScore: Int = 0

)


