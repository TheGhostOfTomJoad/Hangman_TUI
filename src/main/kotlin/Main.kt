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




