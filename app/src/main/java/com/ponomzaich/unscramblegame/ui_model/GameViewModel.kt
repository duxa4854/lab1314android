package com.ponomzaich.unscramblegame.ui_model

import android.provider.UserDictionary
import androidx.compose.runtime.Updater
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ponomzaich.unscramblegame.data.GameUiState
import com.ponomzaich.unscramblegame.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.ponomzaich.unscramblegame.data.MAX_NO_OF_WORDS
import com.ponomzaich.unscramblegame.data.SCORE_INCREASE
import kotlinx.coroutines.flow.update


class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private  lateinit var  currentWord: String

    private var usedWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    fun resetGame(){
        usedWords.clear()
        _uiState.value = GameUiState(
            currentScrambledWord = pickRandomWordAndShuffle()
        )

    }
    private fun shuffleCurrentWord(word: String): String{
        val tempWord = word.toCharArray()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return  String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String{
        currentWord = allWords.random()

        while (usedWords.contains(currentWord)){
            currentWord = allWords.random()

        }
        usedWords.add(currentWord)

        return shuffleCurrentWord(currentWord)
    }
    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }
    private fun updateGameState(updatedScope: Int){
        if (usedWords.size == MAX_NO_OF_WORDS){
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScope,
                    isGameOver = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score =  updatedScope,
                    currentWordCount =  currentState.currentWordCount + 1
                )
            }
        }
    }
    fun checkUserGuess() {
        if (userGuess.equals(currentWord,ignoreCase = true)) {
            val  updatedScope = _uiState.value.score + SCORE_INCREASE
            updateGameState(updatedScope)
        } else{
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        updateUserGuess("")
    }
    fun skipWord(){
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }
}