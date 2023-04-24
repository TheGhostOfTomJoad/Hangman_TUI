import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

class HTTPSenderReceiver  {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
    }

    suspend fun getHighScores(): List<Player> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/allPlayers") {
            contentType(ContentType.Application.Json)
        }
        println(response.status.isSuccess().toString())
        println(response.bodyAsText())

        //println("HighScore")
        //return "HighScore" + players.foldRight("") {player,acc ->  "\n" + (player.pretty()) + acc   }
        return response.body<List<Player>>() // .fold("HighScore") {acc,player ->  acc + "\n" + (player.pretty())    }
    }

    suspend fun sentNameScoreToServer(player: Player): Boolean {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/players") {
            contentType(ContentType.Application.Json)
            setBody(player)

        }
        return (response.status.isSuccess())
    }


}

@Serializable
data class Player(val name: String, val score: Int){
    fun pretty(): String {
        return name + " ".repeat(30 - name.length ) + score
    }
}