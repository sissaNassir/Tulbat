package it.unibo.ai.didattica.competition.tablut.tulbat.MonteCarlo;

public class Negamax {
	//commentato l'originale
	/*private Move getBestMove() {
	    System.out.println("Getting best move");
	    System.out.println("Thinking...");

	    List<Move> validMoves = generateMoves(true);
	    int bestResult = Integer.MIN_VALUE;
	    Move bestMove = null;

	    for (Move move : validMoves) {

	        executeMove(move);
	        System.out.println("Evaluating: " + move);

	        int evaluationResult = -evaluateNegaMax(this.lookForward, "", Integer.MIN_VALUE, Integer.MAX_VALUE);
	        undoMove(move);

	        if (evaluationResult > bestResult) {
	            bestResult = evaluationResult;
	            bestMove = move;
	        }
	    }
	    System.out.println("Done thinking! The best move is: " + bestMove);
	    return bestMove;
	}
	
	public int evaluateNegaMax(int depth, String indent, int alpha, int beta) {
		if (depth <= 0 || this.chessGame.getGameState() == ChessGame.GAME_STATE_WHITE_WON
				|| this.chessGame.getGameState() == ChessGame.GAME_STATE_BLACK_WON) {

			return evaluateState();
		}

		List<Move> moves = generateMoves(false);
		int bestValue = Integer.MIN_VALUE;

		for (Move currentMove : moves) {

			executeMove(currentMove);
			int value = -evaluateNegaMax(depth - 1, indent + "    ", -beta, -alpha);
			System.out.println(indent + "Handling move: " + currentMove + " : " + value);
			undoMove(currentMove);
			counter++;

			if (value > bestValue) {
				bestValue = value;
			}

			if (bestValue > alpha) {
				alpha = bestValue;
			}

			if (bestValue >= beta) {
				break;
			}
		}
		System.out.println(indent + "max: " + alpha);
		return alpha;
	}*/
}
