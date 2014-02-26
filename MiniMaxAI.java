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

	public int[] utilities;

	/// Simulates random games	@Override
	public void getNextMove(final GameStateModule state)
	{
		utilities = new int[state.getWidth()];
		//System.out.println("new turn\n");
		maxDepth = 1;
		ourPlayer = state.getActivePlayer();
		chosenMove = getFirstLegalMove(state);
		
		realDepth = state.getCoins();
		while((!terminate) && (maxDepth <= ((state.getWidth() * state.getHeight()) - state.getCoins())))
		{
			
			chosenMove = miniMax(state);
			maxDepth++;
			
		}
		//System.out.println("terminate: " + terminate);
		//System.out.println("GetNextMove eval: " + getUtility(state));
	}

	public int getFirstLegalMove(final GameStateModule game)
	{
			int move = 4;
			while (!game.canMakeMove(move))
					move = (move + 1) % game.getWidth();
			return move;
	}

	public int miniMax(final GameStateModule state)
	{
		final GameStateModule game = state.copy();
		int maxUtility = -Integer.MAX_VALUE;
		int maxMove = getFirstLegalMove(game);
		

		int[] tempUtilities = new int[state.getWidth()];

		//find the maximum utility out of all the possible moves
		for (int action = 0; (!terminate) && (action < state.getWidth());  action++)
		{
			if(game.canMakeMove(action))
			{
				tempUtilities[action] = minValue(game, action);
				System.out.println(" action " + action + " utility " + tempUtilities[action]);
				
			}
		}//for

		if(!terminate)
			utilities = tempUtilities;

		maxMove = getMaxMove(utilities, state);
		maxUtility = utilities[maxMove];
		System.out.println(" max move " + maxMove + " maxUtility " + maxUtility);

		System.out.println("\n\n__________________________\n\n");

		
		return maxMove;
		
	}

	public int getMaxMove(int[] utilities, final GameStateModule state)
	{
		int maxUtility = -Integer.MAX_VALUE;
		int maxMove = 0;

		for (int move = 0; move < state.getWidth(); move ++)
		{
			if(utilities[move] > maxUtility)
				if(state.canMakeMove(move))
				{
					maxMove = move;
					maxUtility = utilities[move];
				}
		}
		return maxMove;
	}

	public int getEndGameScore(final GameStateModule state)
	{
		if(state.getWinner() == ourPlayer)
				return 500 / state.getCoins();
			else
				return -500 / state.getCoins();
	}

	public int minValue(final GameStateModule state, int move)
	{
		int minUtility = Integer.MAX_VALUE;
		final GameStateModule game = state.copy();

		game.makeMove(move);
		if(game.isGameOver())
		{
			return getEndGameScore(game);
		}

		if (atTerminal(game))
			return getUtility(game);

		
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
		int maxUtility = -Integer.MAX_VALUE;
		final GameStateModule game = state.copy(); 

		game.makeMove(move);
		if(game.isGameOver())
		{
			return getEndGameScore(game);
		}
		if (atTerminal(game))
		{
			return getUtility(game);
		}
			
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
		return ( ( (state.getCoins() - realDepth) == maxDepth) || 
		(state.getCoins() == (state.getWidth() * state.getHeight())));
	}

	public int getUtility(final GameStateModule state)
	{
		int opponent = ((ourPlayer == 1) ? 2 : 1);
		return eval(state, ourPlayer) - eval(state, opponent);
		
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
		final GameStateModule game = state.copy();
		//System.out.println("got to getMove");
		int numLegalMoves = 0;
		for(int i = 0; i < game.getWidth() && !terminate; ++i)
			{
			//System.out.println("accessed state just fine");
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

	private int eval(final GameStateModule state, int player)
	{
		//System.out.println("Entered eval");
		int totalPoints = 0;
		for(int i = 0; i < state.getWidth(); i++) //iterates across board
		{
			int y = state.getHeightAt(i);		//iterates up a column of pieces
			for(int j = 0; j < y; j++)
			{
				if(state.getAt(i, j) == player)	//if one of our pieces is found check its point potential with score function
				{
					totalPoints += score(i, j, state, player);
				}
			}
		}
	//System.out.println("Score: " + totalPoints);
	return totalPoints;
	}

	private int score(int x, int y, final GameStateModule state, int player)
	{
		int sum = 0;
		for(int i = -1; i < 2; i++)
		{
			for(int j = -1; j< 2; j++)
			{
				if(i == 0 && j == 0)
					continue;
				sum += check(i, j, x, y, state, player); // check up

			}

		}
	return sum;
	}

	private boolean checkBounds(int x, int y, GameStateModule state)
	{
		return (x < state.getWidth() && x >=0 && y < state.getHeight() && y >= 0);
	}
	private int check(int xIncrement, int yIncrement, int x, int y, final GameStateModule state, int player)
	{
		int ourPieces = 0;
		int blanks = 0;

		x += xIncrement;
		y += yIncrement;
		int counter = 0;
		while (checkBounds( x, y, state) && counter < 3) // record four spaces or until ceiling is reached
		{

			int whatsThere = state.getAt(x, y);
			if(whatsThere == player || whatsThere == 0)
			{
				blanks += 1 * ((whatsThere == 0) ? 1 : 0); //if space is blank, increment blanks counter
				ourPieces += 1 * ((whatsThere == player) ? 1 : 0); //if our piece, increment counter
				x += xIncrement;
				y += yIncrement;
				counter++;
			}

			else
				return 0; //case where piece is other players
		}
		return points(ourPieces, blanks);
	}

	private int points(int ourPieces, int blanks)
	{
		//System.out.println("ourPieces: " + ourPieces + " blanks " + blanks);
		//System.out.println("points fn returns: " + (ourPieces + 1) * ((ourPieces + blanks == 3) ? 1 : 0));
		return ((ourPieces + 1) * ((ourPieces + blanks == 3) ? 1 : 0));
	}
}
