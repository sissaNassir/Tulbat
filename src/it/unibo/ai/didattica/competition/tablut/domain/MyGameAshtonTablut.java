package it.unibo.ai.didattica.competition.tablut.domain;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.tulbat.heuristics.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Game engine inspired by the Ashton Rules of Tablut
 *
 *
 * @author A. Piretti, Andrea Galassi, Federico Cremonini
 *
 */

public class MyGameAshtonTablut extends GameAshtonTablut implements Game<State, Action, State.Turn>, Cloneable {

    private boolean isCamp [][];
    //private boolean isEscape [][];
    private boolean isCastle [][];
    private int len;

    //nuovo costruttore
    public MyGameAshtonTablut(int repeated_moves_allowed, int cache_size, String logs_folder, String whiteName,
                              String blackName) {
        this(new StateTablut(), repeated_moves_allowed, cache_size, logs_folder, whiteName, blackName);
    }
    //nuovo costruttore
    public MyGameAshtonTablut(State state, int repeated_moves_allowed, int cache_size, String logs_folder,
                              String whiteName, String blackName) {
        //
        super(state, repeated_moves_allowed, cache_size, logs_folder, whiteName, blackName);
        //
        len = state.getBoard().length;
        isCamp = new boolean [len][len];
        //isEscape = new boolean [len][len];
        isCastle = new boolean [len][len];
        //
        for (int i = 0 ; i < len ; i++) {
            Arrays.fill(isCamp[i], false);
            //Arrays.fill(isEscape[i], false);
            Arrays.fill(isCastle[i], false);
        }
        //
        isCamp[0][3] = true;
        isCamp[0][4] = true;
        isCamp[0][5] = true;
        isCamp[1][4] = true;
        isCamp[3][0] = true;
        isCamp[3][8] = true;
        isCamp[4][0] = true;
        isCamp[4][1] = true;
        isCamp[4][7] = true;
        isCamp[4][8] = true;
        isCamp[5][0] = true;
        isCamp[5][8] = true;
        isCamp[7][4] = true;
        isCamp[8][3] = true;
        isCamp[8][4] = true;
        isCamp[8][5] = true;
        //
        /*
        isEscape[0][0] = true;
        isEscape[0][1] = true;
        isEscape[0][2] = true;
        isEscape[0][6] = true;
        isEscape[0][7] = true;
        isEscape[0][8] = true;
        isEscape[1][0] = true;
        isEscape[1][8] = true;
        isEscape[2][0] = true;
        isEscape[2][8] = true;
        isEscape[6][0] = true;
        isEscape[6][8] = true;
        isEscape[7][0] = true;
        isEscape[7][8] = true;
        isEscape[8][0] = true;
        isEscape[8][1] = true;
        isEscape[8][2] = true;
        isEscape[8][6] = true;
        isEscape[8][7] = true;
        isEscape[8][8] = true;
        */
        //
        isCastle[4][4] = true;
    }
    @Override
    public List<Action> getActions(State state) {
        State.Turn turn = state.getTurn();

        List<Action> result = new ArrayList<Action>();
        int distance;
        String from;
        String to;
        len = state.getBoard().length;

        for(int i = 0 ; i < len ; i++) {
            for (int j = 0 ; j < len ; j++) {
                if (state.getPawn(i, j).toString().equals(turn.toString()) ||
                        (state.getPawn(i, j).equals(State.Pawn.KING) && turn.equals(State.Turn.WHITE)) ) {

                    distance = 1;
                    est:
                    for (int es = i + 1 ; es < len ; es++)
                    {
                        if (this.isFree(state, i, j, es, j, distance)) {
                            from = state.getBox(i, j);
                            if(from == null) {
                            	System.out.println("Esplosione EST FROM");
                            }
                            to = state.getBox(es, j);
                            if(to == null) {
                            	System.out.println("Esplosione EST TO");
                            }
                            
                            Action action = null;
                            try {
                                action = new Action(from, to, turn);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // check if action is admissible and if it is, add it to list possibleActions
                            try {
                                result.add(action);

                            } catch (Exception e) {

                            }

                        } else {
                            break est;
                        }

                        distance++;
                    }

                    distance = 1;
                    ovest:
                    for (int ov = i - 1 ; ov >= 0 ; ov--) {
                        if (this.isFree(state, i, j, ov, j, distance)) {
                            from = state.getBox(i, j);
                            if(from == null) {
                            	System.out.println("Esplosione OVEST FROM");
                            }
                            to = state.getBox(ov, j);
                            if(to == null) {
                            	System.out.println("Esplosione OVEST TO");
                            }

                            Action action = null;
                            try {
                                action = new Action(from, to, turn);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // check if action is admissible and if it is, add it to list possibleActions
                            try {
                                result.add(action);

                            } catch (Exception e) {

                            }

                        } else {
                            break ovest;
                        }

                        distance++;
                    }
                    distance = 1;
                    sud:
                    for (int su = j + 1 ; su < len ; su++)
                    {
                        if (this.isFree(state, i, j, i, su, distance)) {
                            from = state.getBox(i, j);
                            if(from == null) {
                            	System.out.println("Esplosione SUD FROM");
                            }
                            to = state.getBox(i, su);
                            if(to == null) {
                            	System.out.println("Esplosione SUD TO");
                            }

                            Action action = null;
                            try {
                                action = new Action(from, to, turn);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // check if action is admissible and if it is, add it to list possibleActions
                            try {
                                result.add(action);

                            } catch (Exception e) {

                            }

                        } else {
                            break sud;
                        }

                        distance++;
                    }
                    distance = 1;
                    nord:
                    for (int no = j - 1 ; no >= 0 ; no--) {
                        if (this.isFree(state, i, j, i, no, distance)) {
                            from = state.getBox(i, j);
                            if(from == null) {
                            	System.out.println("Esplosione NORD FROM");
                            }
                            to = state.getBox(i, no);
                            if(to == null) {
                            	System.out.println("Esplosione NORD TO");
                            }

                            Action action = null;
                            try {
                                action = new Action(from, to, turn);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // check if action is admissible and if it is, add it to list possibleActions
                            try {
                                result.add(action);

                            } catch (Exception e) {

                            }

                        } else {
                            break nord;
                        }

                        distance++;
                    }
                }
            }
        }
        // TODO Auto-generated method stub
        return result;
    }
    private boolean isFree(State state, int fromI, int fromJ, int toI, int toJ, int distance) {

        boolean result = false;
        State.Turn turn = state.getTurn();

        if(turn == State.Turn.WHITE){
            if(!isCamp[toI][toJ] && !isCastle[toI][toJ] && state.getPawn(toI, toJ) == State.Pawn.EMPTY) {
                result = true;
            }
        }
        if(turn == State.Turn.BLACK) {
            if(isCamp[fromI][fromJ] && distance < 3 && !isCastle[toI][toJ] && state.getPawn(toI, toJ) == State.Pawn.EMPTY) {
                result = true;
            }else if(!isCamp[toI][toJ] && !isCastle[toI][toJ] && state.getPawn(toI, toJ) == State.Pawn.EMPTY) {
                result = true;
            }
        }

        return result;
    }
    @Override
    public State getInitialState() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Turn getPlayer(State state) {
        return state.getTurn();
    }
    @Override
    public Turn[] getPlayers() {
        return new State.Turn[0];
    }
    @Override
    public State getResult(State state, Action action) {
        // move pawn
        state = this.movePawn(state.clone(), action);

        // check the state for any capture
        if (state.getTurn().equalsTurn("W")) {
            state = this.checkCaptureBlack(state, action);
        } else if (state.getTurn().equalsTurn("B")) {
            state = this.checkCaptureWhite(state, action);
        }


        //TODO This version of code doesn't check draws

        return state;
    }
    @Override
    public double getUtility(State state, Turn turn) {
        // if it is a terminal state
        if ((turn.equals(State.Turn.BLACK) && state.getTurn().equals(State.Turn.BLACKWIN))
                || (turn.equals(State.Turn.WHITE) && state.getTurn().equals(State.Turn.WHITEWIN)))
            return Double.POSITIVE_INFINITY;
        else if ((turn.equals(State.Turn.BLACK) && state.getTurn().equals(State.Turn.WHITEWIN))
                || (turn.equals(State.Turn.WHITE) && state.getTurn().equals(State.Turn.BLACKWIN)))
            return Double.NEGATIVE_INFINITY;


        // if it isn't a terminal state
        MyHeuristics heuristics = null;
        if (turn.equals(State.Turn.WHITE)) {
            heuristics = new MyWhiteHeuristics(state);
        } else {
            heuristics = new MyBlackHeuristics(state);
        }
        return  heuristics.evaluateState();


        /*
        Tentativo con Negamax
        NegaMaxHeuristics heuristics = new NegaMaxHeuristics(state);

        if (turn.equals(State.Turn.BLACK)) {
            return  - (heuristics.evaluateState());
        }
        return  heuristics.evaluateState();

         */
    }
    @Override
    public boolean isTerminal(State state) {
        if (state.getTurn().equals(State.Turn.WHITEWIN) || state.getTurn().equals(State.Turn.BLACKWIN) || state.getTurn().equals(State.Turn.DRAW) 
        		//|| state.getNumberOf(State.Pawn.BLACK) == 0
        		) {
            return true;
        }
        return false;
    }


}
