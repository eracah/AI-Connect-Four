import java.util.Random;
import java.lang.Math;
import static java.lang.System.*;

public class MiniMaxAI extends AIModule
{
	/// Random number generator to play random games.
	private final Random r = new Random(System.currentTimeMillis());
	/// Used as a helper when picking random moves.
	private int[] moves;

	private int maxDepth = 1;

	private int numGames = 2;
	private int ourPlayer = 0;
	private int realDepth = 0;

	/// Simulates random games	@Override
	public void getNextMove(final GameStateModule state)
	{
		ourPlayer = state.getActivePlayer();
		chosenMove = getFirstLegalMove(state);
		
		int realDepth = state.getCoins();
		while(!terminate)
		{
			chosenMove = miniMax(state);
			maxDepth++;
		}
			
	}

	public int getFirstLegalMove(final GameStateModule game)
	{
			int move = 0;
			while (!game.canMakeMove(move))
					move = (move + 1) % game.getWidth();
			return move;
	}

	public int miniMax(final GameStateModule state)
	{
		final GameStateModule game = state.copy();
		int maxUtility = -Integer.MAX_VALUE;
		int move = getFirstLegalMove(game);
		

		int utility = 0;
		//find the maximum utility out of all the possible moves
		for (int action = 0; (!terminate) && (action < state.getWidth());  action++)
		{
			if(game.canMakeMove(action))
			{
				utility = maxValue(game, action);
				if(utility >= maxUtility)
				{
					maxUtility = utility;
					move = action;
				}
			}
		}//for

		
		return move;
		
	}

	public int minValue(final GameStateModule state, int move)
	{
		final GameStateModule game = state.copy();

		game.makeMove(move);
		if (atTerminal(game))
			return getUtility(game);
			
		int minUtility = Integer.MAX_VALUE;
		for(int action = 0; action < state.getWidth() && !terminate; action++)
			if (game.canMakeMove(action))
			{
				int utility = maxValue(game, action);
				if(utility <= minUtility)
				{
					minUtility = utility;
				}
			}
		return minUtility;
				

	}

	public int maxValue(final GameStateModule state, int move)
	{
		final GameStateModule game = state.copy();

		game.makeMove(move);
		if (atTerminal(game))
		{
			//System.out.println("maxValue terminate");
			return getUtility(game);

		}
			
		int maxUtility = -Integer.MAX_VALUE;
		for(int action = 0; (action < state.getWidth()) && !terminate; action++)
			if (game.canMakeMove(action))
			{
				int utility = minValue(game, action);
				if(utility >= maxUtility)
					maxUtility = utility;
			}
		return maxUtility;
	}

	public boolean atTerminal(final GameStateModule state)
	{
		return ((state.getCoins() - realDepth) == maxDepth);
	}

	public int getUtility(final GameStateModule state)
	{
		int max = 0;
		int xpos = 0;
		for (int col = 0; col < state.getWidth(); col ++)
		{
			if (state.getHeightAt(col) > max)
			{
				max = state.getHeightAt(col);
				xpos = col;
			}
				

		}
		if (state.getAt(xpos, max) == ourPlayer)
			return 1;
		else
			return -1;
		
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
		// return 0;
		// Fill in what moves are legal.
		final GameStateModule game = state.copy();
		System.out.println("got to getMove");
		int numLegalMoves = 0;
		for(int i = 0; i < game.getWidth() && !terminate; ++i)
			{
			System.out.println("accessed state just fine");
			if(game.canMakeMove(i))
				moves[numLegalMoves++] = i;
		}

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
		// return 0;
		// Duplicate the state to prevent changes from propagating.
		final GameStateModule game = state.copy();
		while(!game.isGameOver())
		{
			if(terminate)
				return 0;
			game.makeMove(getMove(game));
		}

		// It's over!  Return who won.
		return game.getWinner();
	}
}
