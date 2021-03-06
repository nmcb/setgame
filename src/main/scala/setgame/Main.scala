package setgame

object Main extends App {


  /** Plays one simulation of the set game with three players. */
  val players = List("Huey", "Dewey", "Louie")
  val game = Game.mkGame(players: _*)
  play(game)


  /** Recursively simulates a game till done:
    * - uses a random player each round to call the game,
    * - whom will use `game.hint` as the next move to remove from the board,
    * - unless no move is possible, which will simulate the trigger to enlarge the board size.
    * */
  def play(game: Game[GameState.Dealt], round: Int = 0): Unit = {

    import scala.util.Random

    println(s"($round) GAME: $game")
    game.hint match {
      case Some(move) =>
        val player = Random.shuffle(game.score.keys).head
        println(s"($round) CALL: $player")
        val Right(called) = game.call(player)
        println(s"($round) MOVE: $move\n")
        called.remove(move) match {
          case Right(next) =>
            play(next, round + 1)
          case Left(finished) =>
            println(s"*** FINAL BOARD: ${finished.board}")
            println(s"*** FINAL SCORE: ${finished.score}")
        }
      case None =>
        println(s"($round) ENLARGED\n")
        val Right(enlarged) = game.enlarge
        play(enlarged, round + 1)
    }
  }
}
