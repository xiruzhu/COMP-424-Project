package hus;

import student_player.Node;
import hus.HusBoardState;
import hus.HusPlayer;
import student_player.Node;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/** A random Hus player. */
public class RandomHusPlayer extends HusPlayer {
 int MAX_DEPTH; //The most depth we will visit
	
    int[][] pits;
    int[] my_pits;
    int[] op_pits;
    int counter = 0;
    /*
     * Stack implementation
     * Very good stuff
     */
    
    
    
    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public RandomHusPlayer() { super("Brute Force AI"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    
    
    public HusMove chooseMove(HusBoardState board_state)
    {
    	if(board_state.getTurnNumber() <= 1){
    		System.out.println("test");
    		//return board_state.getLegalMoves().get((int) (board_state.getLegalMoves().size() * Math.random()));
    	}
        // Get the contents of the pits so we can use it to make decisions.
    	/*
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
        */
    	return strategy(board_state);
    	
        //System.out.println(best_move(0, board_state));
        // But since this is a placeholder algorithm, we won't act on that information.
        //return next_move;
    }
    
    final int victory = 1000;
    final int defeat = -1000;
    final int offense = 1;
    final int neutral = 0;
    final int defense = -1;
    int mode;
    
    private int offensive(int[][] value){ //For when we have the advantage
		int ret = 0;
		for(int i = 0; i < 32; i++){
			ret += value[player_id][i];
			if(value[opponent_id][i] < 2)
				ret += 1;  //So we want the move that reduce the enemy's ability to move
		}
    	return ret;
    }
    
    private int defensive(int[][] value){ //For when we are disadvantaged
		int ret = 0;
		for(int i = 0; i < 32; i++){
			ret += value[player_id][i];
			if(value[player_id][i] > 2)
				ret += 1;  //So we want the move that gives us the most spread out and safe moves
		}
    	return ret;
    }
    
    private int neutral(int[][] value){ //For when we are disadvantaged
		int ret = 0;
		for(int i = 0; i < 32; i++){
			ret += value[player_id][i];
			ret += 1;
		}
    	return ret;
    }

    Node ab_pruning(HusBoardState state, int depth, int pruning){
    	Node opt = null;
    	Node temp;
    	int opt_value;
    	ArrayList<HusMove> moves;
    	
    	if(state.getTurnPlayer() == player_id)
    		opt_value = defeat;
    	else
    		opt_value = victory;
    	
		//For victory/defeat determination
		if(state.gameOver())
			if(state.getWinner() == player_id)
				return new Node(state, victory);
			else
				return new Node(state, defeat);
		
		//Stop at this point and return the heuristic
		if(depth == MAX_DEPTH){
			if(mode == offense)
				return new Node(state, neutral(state.getPits()));
			else if(mode == neutral)
				return new Node(state, neutral(state.getPits()));
			else
				return new Node(state, neutral(state.getPits()));
		}
		
    	moves = state.getLegalMoves();
    	for(int i = 0; i < moves.size(); i++){
    		HusBoardState cpy = (HusBoardState)state.clone();
    		cpy.move(moves.get(i));
    		temp = ab_pruning(cpy, depth + 1, opt_value);
    			if(state.getTurnPlayer() == player_id){
    				//max mode
    				if(temp.value >= opt_value){
    					opt_value = temp.value;
    					opt = temp;
    				}
    				//So given that we are in the max phase. If we found a value e.g   3 -> 2, 4, we don't need to look at the 4 branch anymore since they would pick 2
    				if(opt_value > pruning)
    					return new Node(cpy, opt_value);
    					
    			}else{ //min mode
    				if(temp.value <= opt_value){
    					opt_value = temp.value;
    					opt = temp;
    				}
    				//So given that we are in the min phase. If we found a value e.g   3 -> 4, 2 we don't need to look at the 2 branch anymore since they would pick 4    				
    				if(opt_value < pruning)
    					return new Node(cpy, opt_value);
    			}
    		
    	}
    	return opt;
    }
    
    Node[] ab_pruning(HusBoardState state){
    	ArrayList<HusMove> moves = state.getLegalMoves();
    	Node[] result = new Node[moves.size()];
    	int pruning = 0;
    	//Check if game is over first
    	if(state.gameOver()){
    		result = new Node[1];
    		if(state.getWinner() == player_id)
    			result[0] = new Node(state, victory);
    		else
    			result[0] = new Node(state, defeat);
    		return result;
    	}
    	//Run thru all possible values
    	for(int i = 0; i < moves.size(); i++){
    		HusBoardState cpy = (HusBoardState) state.clone();
    		cpy.move(moves.get(i));
    		result[i] = ab_pruning(cpy, 1, pruning);
    		//For the pruning part
    		result[i].move = moves.get(i);
    		if(state.getTurnPlayer() == player_id){
    			if(result[i].value > pruning)
    				pruning = result[i].value;
    		}
    		else{
    			if(result[i].value < pruning)
    				pruning = result[i].value;
    		}
    	}
    	Arrays.sort(result);
    	return result;
    }
    
    HusMove strategy(HusBoardState state){
    	int total = defeat;
    	int index = 0;
    	Node top_moves[]; //Record the top moves we will be looking at to further expand
    	Node second_moves[] = new Node[3];
    	//This will be sorted
    	int current_heuristic = neutral(state.getPits());
    	
    	double time = System.currentTimeMillis();
    	
    	if(current_heuristic > 66)
    		mode = neutral;
    	else if(current_heuristic < 30)
    		mode = neutral;
    	else
    		mode = neutral;
    	
    	//Find the appropriate depth
    	if(state.getLegalMoves().size() < 13)
    		MAX_DEPTH = 6;
    	else
    		MAX_DEPTH = 6;
    		
    	top_moves = ab_pruning(state);
    	
    	/*
    	for(int i = 0; i < 3 && i < top_moves.length; i++){
    		//If the value are even, further explore
    		if(top_moves[i] != null && top_moves[i].value >= top_moves[0].value){
    			second_moves[i] = ab_pruning(top_moves[i].state)[0]; //get the first one
    		}
    	}

    	
    	
    	//Find the best of the three moves
    	for(int i = 0; i < 3; i++){
    		if(second_moves[i] != null){
    			if(second_moves[i].value > total){
    				total = second_moves[i].value;
    				index = i;
    			}
    		}
    	}

    	//Return value;
    	if(!state.isLegal(top_moves[index].move)){
    		System.out.println("Warning! Default action chosen");
    		return null;
    	}
       
        System.out.println("Time taken: " + (time - System.currentTimeMillis())/1000);
        System.out.println("Move " + index + " Value " + top_moves[index].value + " Value 1" + top_moves[0].value);
        System.out.println("Move " + index + " Value " + second_moves[index].value + " Value 1" + second_moves[0].value);
		*/
    	return top_moves[index].move;
        //return null;
    }
    
    
}
