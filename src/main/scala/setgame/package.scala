package object setgame {

  /** Models the four card features. */
  sealed abstract class Shape
  case object Oval     extends Shape
  case object Squiggle extends Shape
  case object Diamond  extends Shape

  sealed abstract class Color
  case object Red      extends Color
  case object Purple   extends Color
  case object Green    extends Color

  sealed abstract class Number
  case object One      extends Number
  case object Two      extends Number
  case object Three    extends Number

  sealed abstract class Shade
  case object Solid    extends Shade
  case object Striped  extends Shade
  case object Outlined extends Shade

  /** Models a card containing four individual features. */
  case class Card(shape: Shape, color: Color, number: Number, shade: Shade)

  /** Models game state as a phantom type moving back and forth between running and called by player. */
  sealed trait GameState
  object GameState {
    sealed trait Running  extends GameState
    sealed trait Called   extends GameState
    sealed trait Finished extends GameState
  }

  /** Models a game transition result that is either:
    *
    * - a `Right` game in `Running` or `Called` state that can continue to be played,
    * - or a `Left` game that's `Finished`.
    **/
  type Transition[S <: GameState] = Either[Game[GameState.Finished], Game[S]]

  /** Names for structures. */
  type Player                     = String                    // Name
  type Score                      = Map[Player, Int]          // Name -> Points
  type Move                       = List[Card]                // Game.MoveSize
  type Board                      = List[Card]                // Game.BoardSize
  type Deck                       = List[Card]                // Derived size

}
