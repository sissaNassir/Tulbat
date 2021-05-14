package it.unibo.ai.didattica.competition.tablut.tulbat.heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;

public class UltimateWhiteHeuristics extends MyHeuristics {

    private final String BEST_POSITIONS = "bestPositions";
    private final String BLACK_EATEN = "numberOfBlackEaten";
    private final String WHITE_ALIVE = "numberOfWhiteAlive";
    private final String NUM_ESCAPES_KING = "numberOfWinEscapesKing";

    private final String BLACK_SURROUND_KING = "blackSurroundKing";
    private final String PROTECTION_KING = "protectionKing";


    private final static int THRESHOLD_BEST = 2;
    private final static int NUM_BEST_POSITION = 4;

    private Map<String, Double> weights;
    private String[] keys;

    private boolean flag = false;



    public UltimateWhiteHeuristics(State state) {
        super(state);



        // TODO Auto-generated constructor stub
        weights = new HashMap<String, Double>();
        // Positions which are the best moves at the beginning of the game
        weights.put(BEST_POSITIONS, 2.0);
        weights.put(BLACK_EATEN, 20.0);
        weights.put(WHITE_ALIVE, 35.0);
        weights.put(NUM_ESCAPES_KING, 18.0);
        weights.put(BLACK_SURROUND_KING, 7.0);
        weights.put(PROTECTION_KING, 18.0);

        // Extraction of keys
        keys = new String[weights.size()];
        keys = weights.keySet().toArray(new String[0]);

    }

    @Override
    public double evaluateState() {

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

        // Atomic functions to combine to get utility value through the weighted sum
        double bestPositions = (double) getNumberOnBestPositions() / NUM_BEST_POSITION;
        double numberOfWhiteAlive = (double) super.whitePieces / GameAshtonTablut.NUM_WHITE;
        double numberOfBlackEaten = (double) (GameAshtonTablut.NUM_BLACK - super.blackPieces)
                / GameAshtonTablut.NUM_BLACK;
        double blackSurroundKing = (double) (getNumEatenPositions()
                - super.blackAroundKing/*checkNearPawns(state, kingPosition(state), State.Turn.BLACK.toString())*/)
                / getNumEatenPositions();
        double protectionKing = protectionKing();

        int numberWinWays = super.winWays;//countWinWays(state);
        double numberOfWinEscapesKing = numberWinWays > 1 ? (double) super.winWays/*countWinWays(state)*/ / 4 : 0.0;

        if(flag){
            //white stamp
            System.out.println("Number of white alive: " + numberOfWhiteAlive);
            System.out.println("Number of white pawns in best positions " + bestPositions);
            System.out.println("Number of escapes: " + numberOfWinEscapesKing);
            System.out.println("Number of black surrounding king: " + protectionKing);
        }

        Map<String, Double> whiteAtomicUtilities = new HashMap<String, Double>();
        whiteAtomicUtilities.put(BEST_POSITIONS, bestPositions);
        whiteAtomicUtilities.put(WHITE_ALIVE, numberOfWhiteAlive);
        whiteAtomicUtilities.put(BLACK_EATEN, numberOfBlackEaten);
        whiteAtomicUtilities.put(NUM_ESCAPES_KING, numberOfWinEscapesKing);
        whiteAtomicUtilities.put(BLACK_SURROUND_KING, blackSurroundKing);
        whiteAtomicUtilities.put(PROTECTION_KING, protectionKing);

        for (int i = 0; i < weights.size(); i++) {
            utilityValue += weights.get(keys[i]) * whiteAtomicUtilities.get(keys[i]);
            if (flag) {
                System.out.println(keys[i] + ":  " + weights.get(keys[i]) + " * " + whiteAtomicUtilities.get(keys[i]) + " = "
                        + weights.get(keys[i]) * whiteAtomicUtilities.get(keys[i]));
            }
        }

        return utilityValue;
    }

    private double protectionKing() {

        // Values whether there is only a white pawn near to the king
        final double VAL_NEAR = 0.6;
        final double VAL_TOT = 1.0;

        double result = 0.0;

        int[] kingPos = kingPosition;
        // Pawns near to the king
        ArrayList<int[]> pawnsPositions=super.blackPositions;
		/*ArrayList<int[]> pawnsPositions = (ArrayList<int[]>) positionNearPawns(state, kingPos,
				State.Pawn.BLACK.toString());*/


        // There is a black pawn that threatens the king and 2 pawns are enough to eat
        // the king
        if (pawnsPositions.size() == 1 && getNumEatenPositions() == 2) {
            int[] enemyPos = pawnsPositions.get(0);
            // Used to store other position from where king could be eaten
            int[] targetPosition = new int[2];
            // Enemy right to the king
            if (enemyPos[0] == kingPos[0] && enemyPos[1] == kingPos[1] + 1) {
                // Left to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0];
                targetPosition[1] = kingPos[1] - 1;
                if (state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())) {
                    result += VAL_NEAR;
                }
                // Enemy left to the king
            } else if (enemyPos[0] == kingPos[0] && enemyPos[1] == kingPos[1] - 1) {
                // Right to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0];
                targetPosition[1] = kingPos[1] + 1;
                if (state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())) {
                    result += VAL_NEAR;
                }
                // Enemy up to the king
            } else if (enemyPos[1] == kingPos[1] && enemyPos[0] == kingPos[0] - 1) {
                // Down to the king there is a white pawn and king is protected
                targetPosition[0] = kingPos[0] + 1;
                targetPosition[1] = kingPos[1];
                if (state.getPawn(targetPosition[0], targetPosition[1]).equalsPawn(State.Pawn.WHITE.toString())) {
                    result += VAL_NEAR;
                }
                // Enemy down to the king
            } else {
                // Up there is a white pawn and king is protected
                targetPosition[0] = kingPos[0] - 1;
                targetPosition[1] = kingPos[1];
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

    private int getNumberOnBestPositions() {

        int num = 0;

        if (super.whitePieces >= GameAshtonTablut.NUM_WHITE - THRESHOLD_BEST) {
            return whiteInBestPositions;
        }

        return num;
    }
}
