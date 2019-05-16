package setgame

/** Models the game state at value level, as well as type-level to assert allowed transitions at compile time. */
case class Game[S <: GameState](board: Board, deck: Deck, score: Score, caller: Player = null) {

  import Game._

  // state transitions

  /** Transitions the game to `Called` when a player calls on a `Running` game. */
  def call[T >: S <: GameState.Running](player: Player): Transition[GameState.Called] =
    transition(Game(board, deck, score, player))

  /** Transition the game to `Running` with the game's board, deck and score updated for given `Called` for move. */
  def remove[T >: S <: GameState.Called](move: Move): Transition[GameState.Running] =
    isValid(move) && isSet(move) match {
      case true if board.size <= BoardSize =>
        transition(Game(
          board.filterNot(move.contains) ++ deck.take(MoveSize),
          deck.drop(MoveSize),
          updatedScore(caller, 1)))
      case true =>
        transition(Game(
          board.filterNot(move.contains),
          deck,
          updatedScore(caller, 1)))
      case false =>
        transition(Game(
          board,
          deck,
          updatedScore(caller, -1)))
    }

  /** Transitions the game to `Running` after the game's board has been enlarged. */
  def enlarge[T >: S <: GameState.Running]: Transition[GameState.Running] =
    transition(Game(
      board ++ deck.take(MoveSize),
      deck.drop(MoveSize),
      score))

  // utilities

  /** Utility returning a transition to given next game, or the final score if next holds no possible moves. */
  private def transition[T <: GameState](next: Game[T]): Transition[T] =
    if (possibleMoves(next.board).nonEmpty || next.deck.nonEmpty)
      Right(next)
    else
      Left(next.score)

  /** Utility returning the score updated for given player and addition. */
  private def updatedScore(player: Player, addition: Int): Score =
    score.updated(player, score(player) + addition)

  /** Utility returning all possible moves found on the board. */
  private def possibleMoves(board: Board): List[Move] =
    board.combinations(MoveSize).filter(isSet).toList

  // assertions

  /** Utility returning whether given move is of valid move size and is present on the board. */
  private def isValid(move: Move): Boolean =
    move.size == MoveSize && move.forall(card => board.contains(card))

  /** Utility returning whether given move comprises a valid set. */
  private def isSet(move: Move): Boolean = {
    val nrOfShapes  = move.map(_.shape).toSet.size
    val nrOfColors  = move.map(_.color).toSet.size
    val nrOfNumbers = move.map(_.number).toSet.size
    val nrOfShades  = move.map(_.shade).toSet.size

    (nrOfShapes  == 1 || nrOfShapes  == MoveSize) &&
    (nrOfColors  == 1 || nrOfColors  == MoveSize) &&
    (nrOfNumbers == 1 || nrOfNumbers == MoveSize) &&
    (nrOfShades  == 1 || nrOfShades  == MoveSize) &&
    move.size == MoveSize
  }

  /** Public (temporary) utility to facilitate simulation. */
  def hint: Option[Move] =
    possibleMoves(board).headOption

  /** Public (temporary) utility to facilitate useful logging during simulation. */
  override def toString: String =
    s"""
      |     board  = [${board.size}] $board,
      |     deck   = [${deck.size}] $deck,
      |     score  = $score""".stripMargin

}

object Game {

  import scala.util.Random

  val BoardSize = 12
  val MoveSize  = 3

  /** Creates a `Running` game with shuffled deck for given players. */
  def mkGame(players: Player*): Game[GameState.Running] =
    make(deck, players.toList)

  /** Creates an easy (solid figures only) `Running` game with shuffled deck for given players. */
  def mkEasyGame(players: Player*): Game[GameState.Running] =
    make(deck.filter(_.shade == Solid), players.toList)

  /** Shuffles given deck and returns a `Running` game from it for given players. */
  private def make(deck: Deck, players: List[Player]): Game[GameState.Running] = {
    val shuffled = Random.shuffle(deck)
    Game(shuffled.take(BoardSize), shuffled.drop(BoardSize), players.map(p => p -> 0).toMap)
  }

  /** Contains a non-shuffled deck of cards. */
  private val deck: Deck = for {
    shape  <- List(Oval, Squiggle, Diamond)
    color  <- List(Red, Purple, Green)
    number <- List(One, Two, Three)
    shade  <- List(Solid, Striped, Outlined)
  } yield Card(shape, color, number, shade)

}
