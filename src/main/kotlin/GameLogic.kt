class GameLogic(private val wordToGuess: String, tries: Int) {

    private var guessedLetters: MutableList<Char> = mutableListOf()
    private var triesLeft: Int = tries


    fun showOnlyGuessedLettersInWord(): String {
        return String((wordToGuess.map { c ->
            if (guessedLetters.contains(c)) {
                c
            } else {
                '_'
            }
        }).toCharArray())

    }

    private fun gameIsWon(): Boolean {
        return showOnlyGuessedLettersInWord() == wordToGuess
    }

    private fun gameIsLost(): Boolean {
        return triesLeft < 1
    }

    private fun tryChar(newInput: Char): Boolean {
        if (wordToGuess.contains(newInput)) {
            guessedLetters.add(newInput)
            return true
        } else {
            triesLeft -= 1
            return false
        }

    }

    private fun makeMessage(guessedRight: Boolean): Triple<String, Boolean, Boolean> {
        var message: String
        if (guessedRight) {
            message = "Richtig geraten!"
            if (gameIsWon()) {
                message = "$message\nDu hast gewonnen!"
                return Triple("$message\n", true, false)
            }
        } else {
            message = "Falsch gerraten!"
            if (gameIsLost()) {
                message = "$message\nDu hast verloren!"
                return Triple("$message\n", false, true)
            }

        }
        return Triple("$message\n${triesLeft} Versuche übrig\n", false, false)
    }

    fun playRound(newInput: Char): Triple<String, Boolean, Boolean> {
        return makeMessage(tryChar(newInput))

    }

    fun getWordToGuess(): String {
        return wordToGuess
    }

}