package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Arrays;
public class Brute_Force extends HusPlayer {  
	    HusMove next_move;
	    final int MAX_DEPTH = 5;
	    public Brute_Force() { super("Brute Force 5"); }
	    double avg_time = 0;
	    int count = 0;
	    /** Changed so it doesn't any more hue. 
	     	Battle of the AI
	     **/
	    public HusMove chooseMove(HusBoardState board_state)
	    {
	        // Pick a random move from the set of legal moves.
	    	double time = System.currentTimeMillis();
	        best_move(0, board_state);
	        avg_time = (avg_time * count + (time - System.currentTimeMillis()))/(count + 1);
	        count++;
	        System.out.println(avg_time);
	        return next_move;
	    }
	    
	    
	    //There isn't a need for any more
	    private int heuristic_func(int[] value){
			int ret = 0;
			for(int i = 0; i < 32; i++){
				ret += value[i];
			}
	    	return ret;
	    }
	    
	    //Brute force 5 level look ahead
	    int best_move(int depth, HusBoardState state){
	    	int temp;
	    	int opt;
	    	if(depth % 2 == 0)
	    		opt = 0;
	    	else
	    		opt = 96;
	    	
	    	int index;
	    	
	    	if(depth == MAX_DEPTH){
	    		return heuristic_func(state.getPits()[player_id]);
	    	}
	    	ArrayList<HusMove> legal_move = state.getLegalMoves();
	    	index = legal_move.size() - 1;
	    	for(int i = 0; i < legal_move.size(); i++){
	    		HusBoardState board_cpy = (HusBoardState) state.clone();
	    		board_cpy.move(legal_move.get(i)); //Simulate it
	    		
	    		temp = best_move(depth + 1, board_cpy);
	    		if(depth % 2 == 0 && temp > opt){
	    			opt = temp;
	    			index = i;
	    		}
	    		else if(depth % 2 == 1 && temp < opt){
	    			opt = temp;
	    			index = i;
	    		}
	    		
	    	}
	    	//System.out.println("Value " + opt);
	    	if(depth == 0)
	    		next_move = legal_move.get(index);
	    	return opt;
	    	
	    }
}
