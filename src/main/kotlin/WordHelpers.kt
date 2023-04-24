import java.io.File

class WordHelpers {
    private val germanWords: List<String> =
        File("./myWordListGerman.txt").bufferedReader().readLines()//.filter { word -> word.length < 5 }

    fun isLetter(s: String): Boolean {
        return (s.length == 1) && s[0].isLetter()
    }

//    fun isGermanWord(word: String): Boolean {
//
//        return germanWords.contains(word)
//
//    }

    fun randomGermanWord(): String {
        return germanWords.random()
    }

}