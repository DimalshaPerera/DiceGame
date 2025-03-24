package com.example.dice_game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dice_game.R

// DiceComponents.kt
@Composable
fun SelectableDiceImage(
    value: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectable: Boolean = true
) {
    val diceImageResId = getDiceImageResource(value)
    val selectionColor = Color(0xFF4CAF50)
    val borderWidth = if (isSelected) 3.dp else 0.dp
    val clickModifier = if (selectable) Modifier.clickable(onClick = onClick) else Modifier

    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(width = borderWidth, color = selectionColor, shape = RoundedCornerShape(8.dp))
            .then(clickModifier)
    ) {
        Image(
            painter = painterResource(id = diceImageResId),
            contentDescription = "Dice showing $value",
            modifier = Modifier.size(70.dp)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x1A4CAF50))
            )
        }
    }
}

@Composable
fun DiceImage(value: Int) {
    val diceImageResId = getDiceImageResource(value)
    Image(
        painter = painterResource(id = diceImageResId),
        contentDescription = "Dice showing $value",
        modifier = Modifier.size(70.dp)
    )
}

private fun getDiceImageResource(value: Int): Int {
    return when (value) {
        1 -> R.drawable.d_1
        2 -> R.drawable.d_2
        3 -> R.drawable.d_3
        4 -> R.drawable.d_4
        5 -> R.drawable.d_5
        6 -> R.drawable.d_6
        else -> R.drawable.d_1
    }
}