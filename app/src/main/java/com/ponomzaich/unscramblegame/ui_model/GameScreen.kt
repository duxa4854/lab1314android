package com.ponomzaich.unscramblegame.ui_model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ponomzaich.unscramblegame.data.MAX_NO_OF_WORDS

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Unscramble Game",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 32.sp
        )

        GameStatus(
            wordCount = gameUiState.currentWordCount,
            score = gameUiState.score
        )

        GameLayout(
            currentScrambledWord = gameUiState.currentScrambledWord,
            userGuess = gameViewModel.userGuess,
            onUserGuessChanged = { gameViewModel.updateUserGuess(it) },
            onKeyboardDone = { gameViewModel.checkUserGuess() },
            isGuessWrong = gameUiState.isGuessedWordWrong,
            isGameOver = gameUiState.isGameOver, // Passed down here
            score = gameUiState.score,           // Passed down here
            onSubmitClicked = { gameViewModel.checkUserGuess() },
            onSkipClicked = { gameViewModel.skipWord() },
            onPlayAgain = { gameViewModel.resetGame() }
        )
    }
}

@Composable
fun GameStatus(
    wordCount: Int,
    score: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Слово $wordCount из $MAX_NO_OF_WORDS",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Счет: $score",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun GameLayout(
    currentScrambledWord: String,
    userGuess: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    isGuessWrong: Boolean,
    isGameOver: Boolean,
    score: Int,
    onSubmitClicked: () -> Unit,
    onSkipClicked: () -> Unit,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentScrambledWord,
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 45.sp
                )
            }
        }

        Text(
            text = "Разгадайте слово",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = userGuess,
            onValueChange = onUserGuessChanged,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Введите слово") },
            isError = isGuessWrong,
            supportingText = if (isGuessWrong) {
                {
                    Text(
                        text = "Неправильно! Попробуйте еще раз.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else null,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onKeyboardDone() }
            )
        )


        Button(
            onClick = onSubmitClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isGameOver
        ) {
            Text(
                text = "Проверить",
                fontSize = 16.sp
            )
        }

        OutlinedButton(
            onClick = onSkipClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isGameOver ) {
            Text(
                text = "Пропустить",
                fontSize = 16.sp
            )
        }

        if (isGameOver) {
            FinalScoreDialog(
                score = score,
                onPlayAgain = onPlayAgain
            )
        }
    }
}

@Composable
fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
        },
        title = { Text(text = "Поздравляем!") },
        text = {
            Column {
                Text(text = "Вы набрали:")
                Text(
                    text = "$score очков",
                    style = MaterialTheme.typography.displaySmall,
                    fontSize = 36.sp
                )
            }
        },
        modifier = modifier,

        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = "Играть снова")
            }
        }
    )
}