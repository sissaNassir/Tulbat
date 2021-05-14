package it.unibo.ai.didattica.competition.tablut.tulbat.MonteCarlo;

import aima.core.search.adversarial.AlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.MyGameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class AlphaBetaTimerSearch extends AlphaBetaSearch<State, Action, State.Turn> {
	
	private Timer timer;
	private MyGameAshtonTablut game;
	private int iterations;
	//private Metrics metrics;

	public AlphaBetaTimerSearch(MyGameAshtonTablut game, int iterations, int timeout) {
		super(game);
		
		this.game = game;
		this.timer = new  Timer(timeout);
		this.iterations = iterations;
		
	}
	
	@Override
    public Action makeDecision (State state) {
		
		this.timer.start();
		
		//metrics = new Metrics();
		
		Action result = null;
		double resultValue = Double.NEGATIVE_INFINITY;
		
        State.Turn player = state.getTurn();
        for (Action action : game.getActions(state)) {
            double value = minValue(game.getResult(state, action), player,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
            if (value > resultValue) {
                result = action;
                resultValue = value;
            }
        }
		return result;
	}
	
	
	
	private double minValue(State state, Turn player, double alpha, double beta, int depth) {
		if (game.isTerminal(state) || depth > this.iterations || this.timer.timeOutOccurred())
            return game.getUtility(state, player);
        double value = Double.POSITIVE_INFINITY;
        for (Action action : game.getActions(state)) {
            value = Math.min(value, maxValue( //
                    game.getResult(state, action), player, alpha, beta, depth +1));
            if (value <= alpha)
                return value;
            beta = Math.min(beta, value);
        }
        return value;
	}



	private double maxValue(State state, Turn player, double alpha, double beta, int depth) {
		if (game.isTerminal(state) || depth > this.iterations || this.timer.timeOutOccurred())
            return game.getUtility(state, player);
        double value = Double.NEGATIVE_INFINITY;
        for (Action action : game.getActions(state)) {
            value = Math.max(value, minValue( //
                    game.getResult(state, action), player, alpha, beta, depth + 1));
            if (value >= beta)
                return value;
            alpha = Math.max(alpha, value);
        }
        return value;
	}



	private static class Timer {
        private long duration;
        private long startTime;

        Timer(int maxSeconds) {
            this.duration = 1000 * maxSeconds;
        }
        void start() {
            startTime = System.currentTimeMillis();

        }

        boolean timeOutOccurred() {
            return System.currentTimeMillis() > startTime + duration;
        }
    }

}
