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
            println(maybeLetter + " ist kein Buchstabe")
        }

    }


//    fun askForWord(): String {
//        while (true) {
//            println("Welches Wort soll erraten werden!")
//            val maybeWord: String = readln()
//            /*            if (maybeWord.all { c -> c.isLetter() }  ) {
//                            return maybeWord
//                        }*/
//            if (wordHelpers.isGermanWord(maybeWord)) {
//                return maybeWord
//            }
//            //println("Das Wort darf nur Buchstaben enthalten!")
//            println("Das ist kein deutsches Wort!")
//        }
//
//
//    }

    fun printOnlyGuessedLettersInWord(onlyGuessedLettersInWord: String) {
        println("Das Wort ist: " + onlyGuessedLettersInWord)
    }

    fun printWordAfterGameOver(word: String) {
        println("Das Wort war: " + word)
    }

//    fun printEmptyLines(numberOfLines:Int){
//        println("\n".repeat(numberOfLines))
//    }

    fun printScore (score:Int){
        println("Du hast " + score + " Punkte erreicht!")
    }

    fun printHighScores(players:List<Player>){
        println(players.fold("HighScore") {acc,player ->  acc + "\n" + (player.pretty())})
    }


}