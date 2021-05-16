package it.unibo.ai.didattica.competition.tablut.tulbat.heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class NegaMaxHeuristics extends MyHeuristics {
	
	//white parameters
	private final String BEST_POSITIONS = "bestPositions";
	private final String BLACK_EATEN = "numberOfBlackEaten";
	private final String WHITE_ALIVE = "numberOfWhiteAlive";
	private final String NUM_ESCAPES_KING = "numberOfWinEscapesKing";
	private final String BLACK_SURROUND_KING = "blackSurroundKing";
	private final String PROTECTION_KING = "protectionKing";
	
	//black parameters
	private final String RHOMBUS_POSITIONS = "rhombusPositions";
    private final String WHITE_EATEN = "numberOfWhiteEaten";
    private final String BLACK_ALIVE = "numberOfBlackAlive";
    //private final String BLACK_SURROUND_KING = "blackSurroundKing";
    
    //Threshold used to decide whether to use best position
    private final static int THRESHOLD_BEST = 2;
    //Number of white in best position
    private final static int NUM_BEST_POSITION = 4;
    
    //Threshold used to decide whether to use rhombus configuration
    private final int THRESHOLD_RHOMBUS = 10;
    //Number of tiles on rhombus
    private final int NUM_TILES_ON_RHOMBUS = 8;
    
    private final Map<String,Double> whiteWeights;
    private String[] whiteKeys;
    
    private final Map<String,Double> blackWeights;
    private String[] blackKeys;
    
    
  //Flag to enable console print
    private boolean flag = false;
    private State state;
    

	public NegaMaxHeuristics(State state) {
		super(state);
		this.state = state;

		// TODO Auto-generated constructor stub
		// Initializing whiteWeights
		whiteWeights = new HashMap<String, Double>();
		// Positions which are the best moves at the beginning of the game
		whiteWeights.put(BEST_POSITIONS, 2.0);
		whiteWeights.put(BLACK_EATEN, 20.0);
		whiteWeights.put(WHITE_ALIVE, 35.0);
		whiteWeights.put(NUM_ESCAPES_KING, 18.0);
		whiteWeights.put(BLACK_SURROUND_KING, 7.0);
		whiteWeights.put(PROTECTION_KING, 18.0);
		
		//Extraction of keys
        whiteKeys = new String[whiteWeights.size()];
        whiteKeys = whiteWeights.keySet().toArray(new String[0]);
        
        //Initializing weights
        blackWeights = new HashMap<String, Double>();
        blackWeights.put(BLACK_ALIVE, 35.0);
        blackWeights.put(WHITE_EATEN, 48.0);
        blackWeights.put(BLACK_SURROUND_KING, 15.0);
        blackWeights.put(RHOMBUS_POSITIONS, 2.0);

        //Extraction of keys
        blackKeys = new String[blackWeights.size()];
        blackKeys = blackWeights.keySet().toArray(new String[0]);
	}

	@Override
	public double evaluateState() {
		double whiteValue = 0.0;
		double blackValue = 0.0;
		
		// Atomic functions to combine to get utility value through the weighted sum
		double bestPositions = (double) getNumberOnBestPositions() / NUM_BEST_POSITION;
		double numberOfWhiteAlive = (double) super.whitePieces / GameAshtonTablut.NUM_WHITE;
		double numberOfBlackEaten = (double) (GameAshtonTablut.NUM_BLACK - super.blackPieces) / GameAshtonTablut.NUM_BLACK;
		double blackSurroundKing = (double) (getNumEatenPositions(state) - super.blackAroundKing) / getNumEatenPositions(state);
		double protectionKing = protectionKing();
		double numberOfWinEscapesKing = super.winWays > 1 ? (double) super.winWays/*countWinWays(state)*/ / 4 : 0.0;
		

        //Atomic functions to combine to get utility value through the weighted sum
        double numberOfBlack = (double) blackPieces / GameAshtonTablut.NUM_BLACK;
        double numberOfWhiteEaten = (double) (GameAshtonTablut.NUM_WHITE - whitePieces) / GameAshtonTablut.NUM_WHITE;
        double pawnsNearKing = super.blackAroundKing + super.whiteAroundKing;
        double numberOfPawnsOnRhombus = (double) getNumberOnRhombus() / NUM_TILES_ON_RHOMBUS;

        if(flag){
        	//white stamp
        	System.out.println("Number of white alive: " + numberOfWhiteAlive);
			System.out.println("Number of white pawns in best positions " + bestPositions);
			System.out.println("Number of escapes: " + numberOfWinEscapesKing);
			System.out.println("Number of black surrounding king: " + blackSurroundKing);
        	//black stamp
            System.out.println("Number of rhombus: " + numberOfPawnsOnRhombus);
            System.out.println("Number of pawns near to the king:" + pawnsNearKing);
            System.out.println("Number of white pawns eaten: " + numberOfWhiteEaten);
            System.out.println("Black pawns: " + numberOfBlack);
        }
        
        
        
        Map<String, Double> whiteAtomicUtilities = new HashMap<String, Double>();
        whiteAtomicUtilities.put(BEST_POSITIONS, bestPositions);
        whiteAtomicUtilities.put(WHITE_ALIVE, numberOfWhiteAlive);
        whiteAtomicUtilities.put(BLACK_EATEN, numberOfBlackEaten);
        whiteAtomicUtilities.put(NUM_ESCAPES_KING, numberOfWinEscapesKing);
        whiteAtomicUtilities.put(BLACK_SURROUND_KING, blackSurroundKing);
        whiteAtomicUtilities.put(PROTECTION_KING, protectionKing);
        
        for (int i = 0; i < whiteWeights.size(); i++) {
			whiteValue += whiteWeights.get(whiteKeys[i]) * whiteAtomicUtilities.get(whiteKeys[i]);
			if (flag) {
				System.out.println(whiteKeys[i] + ":  " + whiteWeights.get(whiteKeys[i]) + " * " + whiteAtomicUtilities.get(whiteKeys[i]) + " = "
						+ whiteWeights.get(whiteKeys[i]) * whiteAtomicUtilities.get(whiteKeys[i]));
			}
		}

        //Weighted sum of functions to get final utility value
        Map<String,Double> blackAtomicUtilities = new HashMap<String,Double>();
        blackAtomicUtilities.put(BLACK_ALIVE,numberOfBlack);
        blackAtomicUtilities.put(WHITE_EATEN, numberOfWhiteEaten);
        blackAtomicUtilities.put(BLACK_SURROUND_KING,pawnsNearKing);
        blackAtomicUtilities.put(RHOMBUS_POSITIONS,numberOfPawnsOnRhombus);
        
        

        for (int i = 0; i < blackWeights.size(); i++){
        	blackValue += blackWeights.get(blackKeys[i]) * blackAtomicUtilities.get(blackKeys[i]);
            if(flag) {
                System.out.println(blackKeys[i] + ": " +
                		blackWeights.get(blackKeys[i]) + "*" +
                        blackAtomicUtilities.get(blackKeys[i]) +
                        "= " + blackWeights.get(blackKeys[i]) * blackAtomicUtilities.get(blackKeys[i]));
            }
        }
        if(this.state.getTurn().equals(State.Turn.WHITE))
		{
			return whiteValue -blackValue;

		}
        else if(this.state.getTurn().equals(State.Turn.BLACK))
		{
			return blackValue - whiteValue;
		}
		else
		{
			return 1.0;
		}

	}
	
	public int getNumEatenPositions(State state){

	       if (kingPosition[0] == 4 && kingPosition[1] == 4){
	           return 4;
	       } else if ((kingPosition[0] == 3 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 3)
	                  || (kingPosition[0] == 5 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 5)){
	           return 3;
	       } else{
	           return 2;
	       }

	   }
	
	private int getNumberOnBestPositions() {

		if (super.whitePieces >= GameAshtonTablut.NUM_WHITE - THRESHOLD_BEST) {
			return whiteInBestPositions;
		}else {
			return 0;
		}
	}
	
	public int checkNearPawns(State state, int[] position, String target){
		int count=0;
	    //GET TURN
	    State.Pawn[][] board = state.getBoard();
	    if(board[position[0]-1][position[1]].equalsPawn(target))
	        count++;
	    if(board[position[0]+1][position[1]].equalsPawn(target))
	        count++;
	    if(board[position[0]][position[1]-1].equalsPawn(target))
	    	count++;
	    if(board[position[0]][position[1]+1].equalsPawn(target))
	    	count++;
	    return count;
	}
	
	private double protectionKing() {

		// Values whether there is only a white pawn near to the king
		final double VAL_NEAR = 0.6;
		final double VAL_TOT = 1.0;

		double result = 0.0;

		// Pawns near to the king
		ArrayList<int[]> pawnsPositions=super.blackPositions;
		/*ArrayList<int[]> pawnsPositions = (ArrayList<int[]>) positionNearPawns(state, kingPos,
				State.Pawn.BLACK.toString());*/
		

		// There is a black pawn that threatens the king and 2 pawns are enough to eat
		// the king
		if (pawnsPositions.size() == 1 && getNumEatenPositions(state) == 2) {
			int[] enemyPos = pawnsPositions.get(0);
			// Used to store other position from where king could be eaten
			int[] targetPosition = new int[2];
			// Enemy right to the king
			if (enemyPos[0] == kingPosition[0] && enemyPos[1] == kingPosition[1] + 1) {
				// Left to the king there is a white pawn and king is protected
				targetPosition[0] = kingPosition[0];
				targetPosition[1] = kingPosition[1] - 1;
				if (state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())) {
					result += VAL_NEAR;
				}
				// Enemy left to the king
			} else if (enemyPos[0] == kingPosition[0] && enemyPos[1] == kingPosition[1] - 1) {
				// Right to the king there is a white pawn and king is protected
				targetPosition[0] = kingPosition[0];
				targetPosition[1] = kingPosition[1] + 1;
				if (state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())) {
					result += VAL_NEAR;
				}
				// Enemy up to the king
			} else if (enemyPos[1] == kingPosition[1] && enemyPos[0] == kingPosition[0] - 1) {
				// Down to the king there is a white pawn and king is protected
				targetPosition[0] = kingPosition[0] + 1;
				targetPosition[1] = kingPosition[1];
				if (state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())) {
					result += VAL_NEAR;
				}
				// Enemy down to the king
			} else {
				// Up there is a white pawn and king is protected
				targetPosition[0] = kingPosition[0] - 1;
				targetPosition[1] = kingPosition[1];
				if (state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())) {
					result += VAL_NEAR;
				}
			}

			// Considering whites to use as barriers for the target pawn
			double otherPoints = VAL_TOT - VAL_NEAR;
			double contributionPerN = 0.0;

			// Whether it is better to keep free the position
			if (targetPosition[0] == 0 || targetPosition[0] == 8 || targetPosition[1] == 0 || targetPosition[1] == 8) {
				if (state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.EMPTY.toString())) {
					result = 1.0;
				} else {
					result = 0.0;
				}
			} else {
				// Considering a reduced number of neighbours whether target is near to citadels
				// or throne
				if (targetPosition[0] == 4 && targetPosition[1] == 2 || targetPosition[0] == 4 && targetPosition[1] == 6
						|| targetPosition[0] == 2 && targetPosition[1] == 4
						|| targetPosition[0] == 6 && targetPosition[1] == 4
						|| targetPosition[0] == 3 && targetPosition[1] == 4
						|| targetPosition[0] == 5 && targetPosition[1] == 4
						|| targetPosition[0] == 4 && targetPosition[1] == 3
						|| targetPosition[0] == 4 && targetPosition[1] == 5) {
					contributionPerN = otherPoints / 2;
				} else {
					contributionPerN = otherPoints / 3;
				}

				result += contributionPerN * checkNearPawns(state, targetPosition, State.Pawn.WHITE.toString());
				//result += contributionPerN* super.whiteAroundKing;
			}

		}
		return result;
	}
	
	public int getNumberOnRhombus(){

        if (blackPieces >= THRESHOLD_RHOMBUS) {
            return blackInBestPositions;
        }else{
            return 0;
        }
    }

}
