package com.example.dice_game
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dice_game.ui.theme.DiceGameTheme
import com.example.dice_game.components.AnimatedImageSequenceBackground
import com.example.dice_game.components.AnimatedTitle
import com.example.dice_game.components.CustomButton
import com.example.dice_game.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceGameTheme {
                GUI()
            }
        }

    }
}


@Composable
fun GUI() {
    val currentContext = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background using images
        AnimatedImageSequenceBackground()

        AnimatedTitle()


        CustomButton(
            text = "New Game",
            onClick = {
                val intent= Intent(currentContext,GameScreen::class.java)
                currentContext.startActivity(intent)


            },
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 200.dp)
        )



        CustomButton(
            text = "About",
            onClick = { showAboutDialog = true },
            isGradient = true,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 280.dp)
        )
        if (showAboutDialog) {
            AlertDialog(
                modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .padding(horizontal =1.dp),
                onDismissRequest = { showAboutDialog = false },
                containerColor = Color.Transparent,
                shape = RoundedCornerShape(16.dp),
                title = null,
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                    ) {
                        // Brown header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = BrownGradient,
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Profile image
                                Image(
                                    painter = painterResource(id = R.drawable.witch),
                                    contentDescription = "Wizard Frog",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                        .background(LightPurple, CircleShape),

                                )

                                // Text content
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp)
                                ) {
                                    Text(
                                        text = "Dimalsha Perera",
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "ID: 20230655",
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }


                            }
                        }

                        // Dialog content
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "I confirm that I understand what plagiarism is and have read and understood the section on Assessment Offences in the Essential Information for Students. The work that I have submitted is entirely my own. Any work from other authors is duly referenced and acknowledged.",
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,

                                color = Color.DarkGray,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(horizontal = 8.dp)


                            )
                        }

                        // Confirm button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomButton(
                                text = "Confirm",
                                onClick = { showAboutDialog = false },
                                isGradient = true,
                                width = 150,
                                height = 60,
                                fontSize = 16
                            )
                        }
                    }
                },
                confirmButton = { }
            )
        }

    }
}



