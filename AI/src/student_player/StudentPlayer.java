package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Stack;

import student_player.mytools.MyTools;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {
	
    int[][] pits;
    int[] my_pits;
    int[] op_pits;
    int ret;
    int counter = 0;
    Stack<HusBoardState> list;
    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260498154"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state)
    {
        // Get the contents of the pits so we can use it to make decisions.
        pits = board_state.getPits();
        ArrayList<HusMove> moves = board_state.getLegalMoves();
        HusMove move = moves.get(0);
        // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.

        
     
        my_pits = pits[player_id];
        op_pits = pits[opponent_id];

        // Use code stored in ``mytools`` package.
        MyTools.getSomething();
        int value = 0;
        int max = 0;
        int index = 0;
        int[][] temp_pit;
        // Get the legal moves for the current board state.
        for(int i = 0; i < moves.size(); i++){
        		HusBoardState cloned_board_state = (HusBoardState) board_state.clone();
                move = moves.get(i);
                // We can see the effects of a move like this...
                cloned_board_state.move(move);
        		temp_pit = board_state.getPits();
        		value = heuristic_func(temp_pit[1]);
        		if(value > max){
        			max = value;
        			index = i;
        		}

        }
        
        System.out.println(max);
        
        // But since this is a placeholder algorithm, we won't act on that information.
        return moves.get(index);
    }
    
    private void print_pit(){
    	System.out.println( pits[0].length + " PLS ");
    	for(int i = 0; i < pits.length; i++){
    		for(int j = 0; j < pits[i].length; j++){
    			System.out.print(pits[i][j] + ",");
    		}
    		System.out.println();
    	}
    }
    
    //There isn't a need for any more
    private int heuristic_func(int[] value){
		ret = 0;
		for(int i = 0; i < 32; i++){
			ret += value[i];
		}
    	return ret;
    }
    
  
}
