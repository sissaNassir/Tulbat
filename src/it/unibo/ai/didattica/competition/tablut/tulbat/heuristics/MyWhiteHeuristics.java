package it.unibo.ai.didattica.competition.tablut.tulbat.heuristics;

import java.util.HashMap;
import java.util.Map;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class MyWhiteHeuristics extends MyHeuristics {
	
	private final String BEST_POSITIONS = "bestPositions";
	private final String BLACK_EATEN = "numberOfBlackEaten";
	private final String WHITE_ALIVE = "numberOfWhiteAlive";
	private final String NUM_ESCAPES_KING = "numberOfWinEscapesKing";
	private final String KING_PROTECTION = "kingProtection";
	
	private final static int THRESHOLD_BEST = 2;
	private final static int NUM_BEST_POSITION = 4;
	
	private Map<String, Double> weights;
	private String[] keys;
	
	private boolean flag = false;



	public MyWhiteHeuristics(State state) {
		super(state);
		

		
		// TODO Auto-generated constructor stub
		weights = new HashMap<String, Double>();
		// Positions which are the best moves at the beginning of the game

		weights.put(BEST_POSITIONS, 2.0); //piccolissimo, come discriminante a pari valore
		weights.put(BLACK_EATEN, 20.0);//circa metà del bianco
		weights.put(WHITE_ALIVE, 35.0);//circa doppio nero
		weights.put(NUM_ESCAPES_KING, 18.0);//poco meno valore nero
		weights.put(KING_PROTECTION, 25.0);//poco meno valore bianco

		// Extraction of keys
		keys = new String[weights.size()];
		keys = weights.keySet().toArray(new String[0]);
		}

	@Override
	public double evaluateState() {
		//System.out.println(state.getTurn());
		State.Turn turn = state.getTurn();
		
		if(turn.equals(State.Turn.WHITE)) {
			if(wMloseNextMove())
				return Double.NEGATIVE_INFINITY;
			else if(wMwinNextMove())
				return 1000.0;
		}
		
		if(turn.equals(State.Turn.BLACK)) {
			if(bMwinNextTurn())
				return 1000.0;
		}
		
		if(turn.equals(State.Turn.WHITEWIN))
			return Double.POSITIVE_INFINITY;
		
		if(turn.equals(State.Turn.BLACKWIN))
			return Double.NEGATIVE_INFINITY;
		
		double utilityValue = 0;
		
		double bestPositions = (double) getNumberOnBestPositions() / NUM_BEST_POSITION;
		double numberOfWhiteAlive = (double) super.whitePieces / GameAshtonTablut.NUM_WHITE;
		double numberOfBlackEaten = (double) (GameAshtonTablut.NUM_BLACK - super.blackPieces)/ GameAshtonTablut.NUM_BLACK;
		double numberOfWinEscapesKing = (double) (winWays/GameAshtonTablut.NUM_BLACK);
		double kingProtection = (double) ((numEatens-super.blackAroundKing-((4-blackAroundKing-whiteAroundKing)/2))/(numEatens * GameAshtonTablut.NUM_WHITE));
			
		if(flag){
        	//white stamp
        	System.out.println("Number of white alive: " + numberOfWhiteAlive);
			System.out.println("Number of white pawns in best positions " + bestPositions);
			System.out.println("Number of escapes: " + numberOfWinEscapesKing);
			System.out.println("Number of black surrounding king: " + kingProtection);
		}
		
		Map<String, Double> whiteAtomicUtilities = new HashMap<String, Double>();
        whiteAtomicUtilities.put(BEST_POSITIONS, bestPositions);
        whiteAtomicUtilities.put(WHITE_ALIVE, numberOfWhiteAlive);
        whiteAtomicUtilities.put(BLACK_EATEN, numberOfBlackEaten);
        whiteAtomicUtilities.put(NUM_ESCAPES_KING, numberOfWinEscapesKing);
        whiteAtomicUtilities.put(KING_PROTECTION, kingProtection);
        
        for (int i = 0; i < weights.size(); i++) {
        	utilityValue += weights.get(keys[i]) * whiteAtomicUtilities.get(keys[i]);
			if (flag) {
				System.out.println(keys[i] + ":  " + weights.get(keys[i]) + " * " + whiteAtomicUtilities.get(keys[i]) + " = "
						+ weights.get(keys[i]) * whiteAtomicUtilities.get(keys[i]));
			}
		}
		
		return utilityValue;
	}
	
	public double kingProtection() {
		
		return (double) (blackAroundKing / getNumEatenPositions());
		
	}
	
	public boolean bMwinNextTurn() {
		
		if(winWays > 0)
			return true;
		else
			return false;
	}
	
	public boolean wMloseNextMove() {
		
		if(blackAroundKing == numEatens || xKing == -1 || yKing == -1) {
			return true;
		}
		if(numEatens - blackAroundKing > 1)
			return false;

		if(xKing > 0 && xKing < len-1 && (state.getPawn(xKing + 1, yKing).equalsPawn(State.Pawn.BLACK.toString()) || isCamp[xKing + 1][yKing])&& state.getPawn(xKing -1, yKing).equalsPawn(State.Pawn.EMPTY.toString())) {
			int xK = xKing - 1;
			int yK = yKing;
			
			ovest:
				for(int i = xK ; i >= 0 ; i--) {
					if(isCamp[i][yK] && isCastle[i][yK] && state.getPawn(i, yK).equals(Pawn.WHITE)){
						break ovest;
					}
					if(state.getPawn(i, yK).equals(Pawn.BLACK))
						return true;
				}	
			sud:
				for(int j = yK ; j < len ; j++) {
					if(isCamp[xK][j] && isCastle[xK][j] && state.getPawn(xK, j).equals(Pawn.WHITE)){
						break sud;
					}
					if(state.getPawn(xK, j).equals(Pawn.BLACK))
						return true;
				}
			
			nord:
				for(int j = yK ; j >= 0 ; j--) {
					if(isCamp[xK][j] && isCastle[xK][j] && state.getPawn(xK, j).equals(Pawn.WHITE)){
						break nord;
					}
					if(state.getPawn(xK, j).equals(Pawn.BLACK))
						return true;
				}
		}
			if(yKing > 0 && yKing < len-1 && (state.getPawn(xKing, yKing+1).equalsPawn(State.Pawn.BLACK.toString()) || isCamp[xKing][yKing+1]) && state.getPawn(xKing -1, yKing).equalsPawn(State.Pawn.EMPTY.toString())) {
			int xK = xKing;
			int yK = yKing - 1;
			
			est:
				for(int i = xK ; i < len ; i++) {
					if(isCamp[i][yK] && isCastle[i][yK] && state.getPawn(i, yK).equals(Pawn.WHITE)){
						break est;
					}
					if(state.getPawn(i, yK).equals(Pawn.BLACK))
						return true;
				}
			
			ovest:
				for(int i = xK ; i >= 0 ; i--) {
					if(isCamp[i][yK] && isCastle[i][yK] && state.getPawn(i, yK).equals(Pawn.WHITE)){
						break ovest;
					}
					if(state.getPawn(i, yK).equals(Pawn.BLACK))
						return true;
				}
			
			nord:
				for(int j = yK ; j >= 0 ; j--) {
					if(isCamp[xK][j] && isCastle[xK][j] && state.getPawn(xK, j).equals(Pawn.WHITE)){
						break nord;
					}
					if(state.getPawn(xK, j).equals(Pawn.BLACK))
						return true;
				}
			
			
			
		}
			if(xKing < len - 1 && xKing > 0 && (state.getPawn(xKing-1, yKing).equalsPawn(State.Pawn.BLACK.toString()) || isCamp[xKing-1][yKing])&& state.getPawn(xKing -1, yKing).equalsPawn(State.Pawn.EMPTY.toString())){
			int xK = xKing + 1;
			int yK = yKing;
			
			est:
				for(int i = xK ; i < len ; i++) {
					if(isCamp[i][yK] && isCastle[i][yK] && state.getPawn(i, yK).equals(Pawn.WHITE)){
						break est;
					}
					if(state.getPawn(i, yK).equals(Pawn.BLACK))
						return true;
				}
			sud:
				for(int j = yK ; j < len ; j++) {
					if(isCamp[xK][j] && isCastle[xK][j] && state.getPawn(xK, j).equals(Pawn.WHITE)){
						break sud;
					}
					if(state.getPawn(xK, j).equals(Pawn.BLACK))
						return true;
				}
			
			nord:
				for(int j = yK ; j >= 0 ; j--) {
					if(isCamp[xK][j] && isCastle[xK][j] && state.getPawn(xK, j).equals(Pawn.WHITE)){
						break nord;
					}
					if(state.getPawn(xK, j).equals(Pawn.BLACK))
						return true;
				}
		}
			if(yKing < len - 1 && yKing > 0 && (state.getPawn(xKing, yKing-1).equalsPawn(State.Pawn.BLACK.toString()) || isCamp[xKing][yKing-1])&& state.getPawn(xKing -1, yKing).equalsPawn(State.Pawn.EMPTY.toString())) {
			int xK = xKing;
			int yK = yKing + 1;
			
			est:
				for(int i = xK ; i < len ; i++) {
					if(isCamp[i][yK] && isCastle[i][yK] && state.getPawn(i, yK).equals(Pawn.WHITE)){
						break est;
					}
					if(state.getPawn(i, yK).equals(Pawn.BLACK))
						return true;
				}
			
			ovest:
				for(int i = xK ; i >= 0 ; i--) {
					if(isCamp[i][yK] && isCastle[i][yK] && state.getPawn(i, yK).equals(Pawn.WHITE)){
						break ovest;
					}
					if(state.getPawn(i, yK).equals(Pawn.BLACK))
						return true;
				}
			
			sud:
				for(int j = yK ; j < len ; j++) {
					if(isCamp[xK][j] && isCastle[xK][j] && state.getPawn(xK, j).equals(Pawn.WHITE)){
						break sud;
					}
					if(state.getPawn(xK, j).equals(Pawn.BLACK))
						return true;
				}
		}
		
		
		
		return false;
	}
	
	public boolean wMwinNextMove() {
		if(winWays > 1 && !wMloseNextMove())
			return true;
		else
			return false;
	}
	

	
	private int getNumberOnBestPositions() {

		int num = 0;

		if (super.whitePieces >= GameAshtonTablut.NUM_WHITE - THRESHOLD_BEST) {
			return whiteInBestPositions;
		}

		return num;
	}
}
