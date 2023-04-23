import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import java.io.File

suspend fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
    //sentNameScoretoServer()
    //val client = HTTPSenderReceiver()
    //client.sentNameScoreToServer("Max",3)
    //client.sentNameScoreToServer("Bob Dylan",1)
    //print(client.GetHighScores())
    val hmGame = HangmanGame()
    hmGame.playHangmanSentScoreToServer()
}

class GameLogic(private var wordToGuess: String, tries: Int) {

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
            message = "You guessed right!"
            if (gameIsWon()) {
                message = "$message\nYou win!"
                return Triple(message, true, false)
            }
        } else {
            message = "You guessed wrong!"
            if (gameIsLost()) {
                message = "$message\nYou loose!"
                return Triple(message, false, true)
            }

        }
        return Triple("$message\n${triesLeft} Versuche übrig", false, false)
    }

    fun playRound(newInput: Char): Triple<String, Boolean, Boolean> {
        return makeMessage(tryChar(newInput))

    }

    fun getWordToGuess(): String {
        return wordToGuess
    }

}


class WordHelpers {
    private val germanWords: List<String> =
        File("./myWordListGerman.txt").bufferedReader().readLines().filter { word -> word.length < 5 }

    fun isLetter(s: String): Boolean {
        return (s.length == 1) && s[0].isLetter()
    }

    fun isGermanWord(word: String): Boolean {

        return germanWords.contains(word)

    }

    fun randomGermanWord(): String {
        return germanWords.random()
    }

}


class TUI {


    private val wordHelpers = WordHelpers()

    fun randomGermanWord(): String {
        return wordHelpers.randomGermanWord()
    }

    fun askForLetter(): Char {
        while (true) {
            println("Gib einen Buchstaben ein!")
            val maybeLetter: String = readln()
            if (wordHelpers.isLetter(maybeLetter)) {
                return maybeLetter[0]
            }
            println(maybeLetter + "ist kein Buchstabe")
        }

    }


    fun askForWord(): String {
        while (true) {
            println("Welches Wort soll erraten werden!")
            val maybeWord: String = readln()
            /*            if (maybeWord.all { c -> c.isLetter() }  ) {
                            return maybeWord
                        }*/
            if (wordHelpers.isGermanWord(maybeWord)) {
                return maybeWord
            }
            //println("Das Wort darf nur Buchstaben enthalten!")
            println("Das ist kein deutsches Wort!")
        }


    }

}

class HangmanGame {
    private val tui = TUI()
    //private val gameLogic = GameLogic("Hallo", 3)
    private val httpSenderReceiver  :HTTPSenderReceiver = HTTPSenderReceiver()


    private fun playHangmanTUI(wordToGuess: String): Boolean {
        val gameLogic = GameLogic(wordToGuess, 7)
        //var gameOnGoing = true
        var gameWon = false
        var gameLost = false
        while (!(gameWon || gameLost)) {
            println("Das Wort ist: " + gameLogic.showOnlyGuessedLettersInWord())
            val newInput = tui.askForLetter()
            val (message: String, newGameWon: Boolean, newGameLost: Boolean) = gameLogic.playRound(newInput)
            println(message)
            gameWon = newGameWon
            gameLost = newGameLost
            //gameOnGoing = !(gameWon || gameLost)

        }
        println("Das Wort war: " + gameLogic.getWordToGuess())
        return gameWon

    }

    fun playHangmanTUI2Players(): Boolean {
        val wordToGuess: String = tui.askForWord()
        println("\n".repeat(100))
        return playHangmanTUI(wordToGuess)
    }

    private fun playHangmanTUIOnePlayer(): Boolean {
        val wordToGuess: String = tui.randomGermanWord()
        //println("\n".repeat(100))
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
            println("Next Round!")

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

class HTTPSenderReceiver  {
    private val client = HttpClient(CIO)  {
        install(ContentNegotiation){json()}
    }

    suspend fun getHighScores(): String {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/allPlayers") {
            contentType(ContentType.Application.Json)}
        val players = response.body<List<Player>>()
        println(response.status.isSuccess().toString())
        println(response.bodyAsText())
        //println("HighScore")
        //return "HighScore" + players.foldRight("") {player,acc ->  "\n" + (player.pretty()) + acc   }
        return players.fold("HighScore") {acc,player ->  acc + "\n" + (player.pretty())    }
    }

    suspend fun sentNameScoreToServer(playerName:String, score: Int): String {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/players") {
            contentType(ContentType.Application.Json)
            setBody(Player(playerName, score))

        }
        return (response.status.isSuccess().toString())
    }


}

@Serializable
data class Player(val name: String, val score: Int){
    fun pretty(): String {
        return name + " ".repeat(30 - name.length ) + score
    }
}


