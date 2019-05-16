## Set-Game

Dear Chris, Kelvin, and colleagues, thank you for the your invitation to continue the application process, I hope this codebase provides an interesting read and answer to the exercise.  The Set-game provided many possibilities to focus on when encoded in Scala.  Please allow me to elaborate a little bit on the form I choose as a rationale.

I have encoded the core game logic in the class `Game` using a couple of type alias definitions in the package object `setgame`.  A program that simulates a randomly generated Set-game for three players is encoded in the class `Main` which includes a display of the required functionality:

- Run a single player move.
- Retrieve the current players' scores.
- Return who has won a game.

I've treated the exercise as a proof of concept, and coded it without external dependencies, only using Scala the language and the `List`, `Map` and `Either` data structures that are provided by Scala the library.  This was intentional as I wanted to focus on displaying my familiarity with Scala basics, instead of an arbitrary external library or framework.  In the same manner I did not add tests, even though (I hope you agree) these could be added rather easily.  In short, I kept things small on purpose.

The overall design treats an instance of the case class `Game` as a step in the game, containing at value level the current `board` of cards on the table, the current `deck` of cards yet unseen by the players, a `score` mapping that maintains the number of card set moves any player has removed from the board up till now, and a `caller` reference that is used to hold the player that called upon a certain board.  Instances of a new `Game` can be created for multiple players via the `mkGame` and `mkEasyGame` methods on `Game`s companion object.

A game is played by a succession of `call`, `remove` and `enlarge` methods that advance the state in the game and thus return a new `Game` as a right sided result when the game is not finished, and a left sided `Score` result when it is.  The `Game` class calculates itself when no move is possible any longer with the use of the `possibleMoves` method and the `isSet` method.  The latter one encodes what it means for a move of cards to comprise a "Game Set" and is also used to decide the next state when a player tries to remove a set of cards from the board, the former one generates a list of all possible card moves and filters out moves that are not sets.  Passing the next game state as a result of a player's action makes it easy for the client-side code interacting with a game to ensure correct behaviour under concurrent client calls, for example by maintaining a reference to the current game in an `AtomicReference` or `MRef` without the need to encode concurrency constraints in the class itself.  Again, this was done deliberately, as to be able to display my understanding of the game logic in a small codebase instead of issues of concurrency.

To make sure that only allowed methods can be called in succession I used a `GameState` phantom type to annotate the state transition methods allowed so that correct sequenciality of client side usage is checked at compile time.  The `GameState` alternates between `Running`, in which state players are allowed to `call` upon a move spotted on the current board or `enlarge` that board if no move was to be found, conversely, in the state `Called` a move can be `remove`d from the board by the player that called upon it.  This makes sure illegal client side state transitions result in a compile time error.  The `caller` reference is `null`ed in state `Running` which was chosen over the use of the `Option` type because it (counter-intuitively) yielded more readable code in the implementation of `remove`.  Note that this reference is used only when the `GameState` is `Called` and `Game` internally, and thus is arguably an implementation detail.  Two methods are (temporary) present to drive simulation of a full game: `hint` which just returns the `headOption` of the `possibleMoves` on the board, and the overwritten `toString` as to provide for better logging when a simulation is played.

In order to run a simulation, execute:

```
$ sbt run
```

I hope you enjoy.
