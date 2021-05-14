package it.unibo.ai.didattica.competition.tablut.tulbat.heuristics;

import java.util.ArrayList;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public abstract class MonteCarloHeuristics {

	protected State state;
	private int len;

	protected int whitePieces; // without king
	protected int blackPieces;
	protected int whiteAroundKing;
	protected int blackAroundKing;
	protected int whiteInBestPositions;// out of 4
	protected int blackInBestPositions;// out of 8
	protected boolean kingInCastle;
	protected int xKing;
	protected int yKing;
	// positions near the king of the white
	ArrayList<int[]> whitePositions;
	// position near the king for the black
	ArrayList<int[]> blackPositions;
	protected boolean safePositionKing;
	protected int winWays; // number of escapes that the king can reach
	
	// protected boolean whiteWinNextMove;
	// protected boolean blackWinNextMove;

	public MonteCarloHeuristics(State state) {

		this.state = state;

		len = state.getBoard().length;
		whitePieces = 0;
		blackPieces = 0;
		whiteAroundKing = 0;
		blackAroundKing = 0;
		whiteInBestPositions = 0;
		blackInBestPositions = 0;
		whitePositions = new ArrayList<int[]>();
		blackPositions = new ArrayList<int[]>();
		safePositionKing = false;
		winWays = 0;
		int[] pos = new int[2];
		int col = 0;
		int row = 0;

		// king in castle
		if (state.getPawn(4, 4).equals(State.Pawn.KING)) {
			kingInCastle = true;
		} else {
			kingInCastle = false;
		}

		// white best position
		if (state.getPawn(2, 3).equals(State.Pawn.WHITE)) {
			whiteInBestPositions++;
		}
		if (state.getPawn(3, 5).equals(State.Pawn.WHITE)) {
			whiteInBestPositions++;
		}
		if (state.getPawn(5, 3).equals(State.Pawn.WHITE)) {
			whiteInBestPositions++;
		}
		if (state.getPawn(6, 5).equals(State.Pawn.WHITE)) {
			whiteInBestPositions++;
		}

		// black best position
		if (state.getPawn(1, 2).equals(State.Pawn.BLACK)) {
			blackInBestPositions++;
		}
		if (state.getPawn(1, 6).equals(State.Pawn.BLACK)) {
			blackInBestPositions++;
		}
		if (state.getPawn(2, 1).equals(State.Pawn.BLACK)) {
			blackInBestPositions++;
		}
		if (state.getPawn(2, 7).equals(State.Pawn.BLACK)) {
			blackInBestPositions++;
		}
		if (state.getPawn(6, 1).equals(State.Pawn.BLACK)) {
			blackInBestPositions++;
		}
		if (state.getPawn(6, 7).equals(State.Pawn.BLACK)) {
			blackInBestPositions++;
		}
		if (state.getPawn(7, 2).equals(State.Pawn.BLACK)) {
			blackInBestPositions++;
		}
		if (state.getPawn(7, 6).equals(State.Pawn.BLACK)) {
			blackInBestPositions++;
		}

		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				// if is white
				if (state.getPawn(i, j).equals(State.Pawn.WHITE)) {
					whitePieces++;
				}
				// if is black
				if (state.getPawn(i, j).equals(State.Pawn.BLACK)) {
					blackPieces++;
				}
				// if is king
				if (state.getPawn(i, j).equals(State.Pawn.KING)) {
					// check around king

					xKing = i;
					yKing = j;

					if (i > 0) {
						if (state.getPawn(i - 1, j).equals(State.Pawn.WHITE)) {
							whiteAroundKing++;
							pos[0] = i - 1;
							pos[1] = j;
							whitePositions.add(pos);
						}
						if (state.getPawn(i - 1, j).equals(State.Pawn.BLACK)) {
							blackAroundKing++;
							pos[0] = i - 1;
							pos[1] = j;
							blackPositions.add(pos);
						}
					}
					if (i < (len - 1)) {
						if (state.getPawn(i + 1, j).equals(State.Pawn.WHITE)) {
							whiteAroundKing++;
							pos[0] = i + 1;
							pos[1] = j;
							whitePositions.add(pos);
						}
						if (state.getPawn(i + 1, j).equals(State.Pawn.BLACK)) {
							blackAroundKing++;
							pos[0] = i + 1;
							pos[1] = j;
							blackPositions.add(pos);
						}
					}
					if (j > 0) {
						if (state.getPawn(i, j - 1).equals(State.Pawn.WHITE)) {
							whiteAroundKing++;
							pos[0] = i;
							pos[1] = j - 1;
							whitePositions.add(pos);
						}
						if (state.getPawn(i, j - 1).equals(State.Pawn.BLACK)) {
							blackAroundKing++;
							pos[0] = i;
							pos[1] = j - 1;
							blackPositions.add(pos);
						}
					}
					if (j < (len - 1)) {
						if (state.getPawn(i, j + 1).equals(State.Pawn.WHITE)) {
							whiteAroundKing++;
							pos[0] = i;
							pos[1] = j + 1;
							whitePositions.add(pos);
						}
						if (state.getPawn(i, j + 1).equals(State.Pawn.BLACK)) {
							blackAroundKing++;
							pos[0] = i;
							pos[1] = j + 1;
							blackPositions.add(pos);
						}
					}

					if (i > 2 && i < 6) {
						if (j > 2 && j < 6) {
							safePositionKing = true;
						}
					} else {
						safePositionKing = false;
					}
					//calulate win ways
					if (!safePositionKing) {
						if ((!(j > 2 && j < 6)) && (!(i > 2 && i < 6))) {
							// not safe row not safe col
							col = countFreeColumn(state,i,j);
							row = countFreeRow(state,i,j);
						}
						if ((j > 2 && j < 6)) {
							// safe row not safe col
							row = countFreeRow(state,i,j);
						}
						if ((i > 2 && i < 6)) {
							// safe col not safe row
							col = countFreeColumn(state,i,j);
						}
						// System.out.println("ROW:"+row);
						// System.out.println("COL:"+col);
						winWays = col + row;
					}

					winWays = col + row;

				}
			}
		}

	}
	
    /**
    *
    * @return number of free columns
    */
   public int countFreeColumn(State state,int iking, int yking){
       //lock column
       int row=iking;
       int column=yking;
       int[] currentPosition = new int[2];
       int freeWays=0;
       int countUp=0;
       int countDown=0;
       //going down
       for(int i=row+1;i<=8;i++) {
           currentPosition[0]=i;
           currentPosition[1]=column;
           if (checkOccupiedPosition(state,currentPosition)) {
               countDown++;
           }
       }
       if(countDown==0)
           freeWays++;
       //going up
       for(int i=row-1;i>=0;i--) {
           currentPosition[0]=i;
           currentPosition[1]=column;
           if (checkOccupiedPosition(state,currentPosition)){
               countUp++;
           }
       }
       if(countUp==0)
           freeWays++;

       return freeWays;
   }
   
   /**
   *
   * @return number of free rows that a Pawn has
   */
  public int countFreeRow(State state,int iking, int jking){
      int row=iking;
      int column=jking;
      int[] currentPosition = new int[2];
      int freeWays=0;
      int countRight=0;
      int countLeft=0;
      //going right
      for(int i = column+1; i<=8; i++) {
          currentPosition[0]=row;
          currentPosition[1]=i;
          if (checkOccupiedPosition(state,currentPosition)) {
              countRight++;
          }
      }
      if(countRight==0)
          freeWays++;
      //going left
      for(int i=column-1;i>=0;i--) {
          currentPosition[0]=row;
          currentPosition[1]=i;
          if (checkOccupiedPosition(state,currentPosition)){
              countLeft++;
          }
      }
      if(countLeft==0)
          freeWays++;

      return freeWays;
  }

   /**
    *
    * @return true if a position is occupied, false otherwise
    */
   public boolean checkOccupiedPosition(State state,int[] position){
       return !state.getPawn(position[0], position[1]).equals(State.Pawn.EMPTY);
   }

	public abstract double evaluateState();
}
