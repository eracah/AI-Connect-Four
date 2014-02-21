import java.util.Random;
import java.lang.Math;
import static java.lang.System.*;

public class MiniMaxAI extends AIModule
{
	/// Random number generator to play random games.
	private final Random r = new Random(System.currentTimeMillis());
	/// Used as a helper when picking random moves.
	private int[] moves;

	private int maxDepth = 6;

	private int numGames = 2;
	private int ourPlayer = 0;
	private int realDepth = 0;

	/// Simulates random games and chooses the move that leads to the highest expected value.
	@Override
	public void getNextMove(final GameStateModule state)
	{
		ourPlayer = state.getActivePlayer();
		chosenMove = 0;
		int realDepth = state.getCoins();
		chosenMove = miniMax(state);
	}

	public int miniMax(final GameStateModule state)
	{
		final GameStateModule game = state.copy();
		int max = -Integer.MAX_VALUE;

		//find the maximum utility out of all the possible moves
		for (int action = 0; action < state.getWidth(); action++)
			if(minValue(game, action) > max)
				max = minValue(game, action);

		return max;
		
	}

	public int minValue(final GameStateModule state, int move)
	{
		final GameStateModule game = state.copy();

		game.makeMove(move);
		if (atTerminal(game))
		{
			System.out.println("minValue terminate");
			return utility(game);
		}
			
		int v = Integer.MAX_VALUE;
		for(int action = 0; action < state.getWidth(); action++)
			if (game.canMakeMove(action))
				v = ((maxValue(game, action) < v) ? maxValue(game, action) : v);
		return v;
				

	}

	public int maxValue(final GameStateModule state, int move)
	{
		final GameStateModule game = state.copy();

		game.makeMove(move);
		if (atTerminal(game))
		{
			System.out.println("maxValue terminate");
			return utility(game);

		}
			

		int v = -Integer.MAX_VALUE;
		for(int action = 0; action < state.getWidth(); action++)
			if (game.canMakeMove(action))
				v = ((minValue(game, action) > v) ? minValue(game, action) : v);
		return v;

	}

	public boolean atTerminal(final GameStateModule state)
	{
		return ((state.getCoins() - realDepth) == maxDepth);
	}

	public int utility(final GameStateModule state)
	{
		
		int count = 0;
		for(int i = 0; i< numGames; i++)
			if(playRandomGame(state) == ourPlayer)
				count++;
		return((int) Math.floor(count / numGames));
		
	}

	/// Returns a random legal move in a given state.
	/**
	 * Given a game state, returns the index of a column that is a legal move.
	 *
	 * @param state The state in which to get a legal move.
	 * @return A random legal column to drop a coin in.
	 */
	private int getMove(final GameStateModule state)
	{
		// Fill in what moves are legal.
		int numLegalMoves = 0;
		for(int i = 0; i < state.getWidth(); ++i)
			if(state.canMakeMove(i))
				moves[numLegalMoves++] = i;

		// Pick one randomly.
		final int n = r.nextInt(numLegalMoves);
		return moves[n];
	}

	// Given the result of the last game, update our chosen move.
	/**
	 * After simulating a game, updates the array containing all of the expected values
	 * and updates the chosen move to reflect the move with the highest positive expectation
	 * value.
	 *
	 * @param ourPlayer The index of the player representing us.
	 * @param result The result of the last game (0 for draw, 1 for player 1 win, etc.)
	 * @param values The array of expected values.
	 * @param move The move played that led to this outcome.
	 */
	private void updateGuess(final int ourPlayer, final int result, int[] values, int move)
	{
		// On a draw, we can skip making changes.
		if(result == 0)
			return;

		// Update the expected value of this move depending on whether we win or lose.
		values[move] += (result == ourPlayer ? 1 : -1);

		// Update the move to be the best known move.  This is necessary since we need
		// to have the best move available at all times because we run forever.
		for(int i = 0; i < values.length; ++i)
			if(values[i] > values[chosenMove])
				chosenMove = i;
	}

	/// Given a game, plays it through to the end using random moves.
	/**
	 * Given a game state, chooses a sequence of random moves until the end of the game
	 * and returns the result of the game.  The input state is not modified.
	 *
	 * @param state The state from which to play.
	 * @return The result of the game as dictated by GameStateModule.getWinner
	 * @see GameStateModule.getWinner
	 */
	private int playRandomGame(final GameStateModule state)
	{
		// Duplicate the state to prevent changes from propagating.
		final GameStateModule game = state.copy();
		while(!game.isGameOver())
			game.makeMove(getMove(game));

		// It's over!  Return who won.
		return game.getWinner();
	}
}
