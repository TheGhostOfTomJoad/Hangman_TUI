class HangmanGame {
    private val tui = TUI()

    //private val gameLogic = GameLogic("Hallo", 3)
    private val httpSenderReceiver: HTTPSenderReceiver = HTTPSenderReceiver()


    private fun playHangmanTUI(wordToGuess: String): Boolean {
        val gameLogic = GameLogic(wordToGuess, 7)
        //var gameOnGoing = true
        var gameWon = false
        var gameLost = false
        while (!(gameWon || gameLost)) {
            tui.printOnlyGuessedLettersInWord(gameLogic.showOnlyGuessedLettersInWord())
            val newInput = tui.askForLetter()
            val (message: String, newGameWon: Boolean, newGameLost: Boolean) = gameLogic.playRound(newInput)
            println(message)
            gameWon = newGameWon
            gameLost = newGameLost
            //gameOnGoing = !(gameWon || gameLost)

        }
        tui.printWordAfterGameOver(gameLogic.getWordToGuess())
        return gameWon

    }

    fun playHangmanTUI2Players(): Boolean {
        val wordToGuess: String = tui.askForWord()
        tui.printEmptyLines(100)
        return playHangmanTUI(wordToGuess)
    }

    private fun playHangmanTUIOnePlayer(): Boolean {
        val wordToGuess: String = tui.randomGermanWord()
        return playHangmanTUI(wordToGuess)
    }


    private fun playHangmanHighScore(): Int {
        var noGameLost = true
        var score = 0
        while (noGameLost) {
            val wonLastGame = playHangmanTUIOnePlayer()
            if (wonLastGame) {
                score += 1
            }
            noGameLost = wonLastGame
        }
        return score
    }

    suspend fun playHangmanSentScoreToServer() {
        val score = playHangmanHighScore()
        println("Gib deinen Namen ein")
        val playerName = readln()
        httpSenderReceiver.sentNameScoreToServer(playerName, score)
        println("Send name = $playerName score =$score to server")
        print(httpSenderReceiver.getHighScores())
    }
}