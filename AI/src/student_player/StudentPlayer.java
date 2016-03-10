package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Arrays;

/** A random Hus player. */
public class StudentPlayer extends HusPlayer {  
	
	public class move_value implements Comparable{
		double value;
		int prob; //Out of Integer.MAX_VALUE
		int overall_prob;
		HusMove move;    //The actual move
		
		move_value(double value, HusMove move){
			this.value = value;
			this.move = move;
		}
		
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			if(arg0 == null)
				return 1;
			else if(this.value < ((move_value)arg0).value)
				return 1;
			return -1;
		}
	}
    //Recommended depth
	int REC_DEPTH = 6;
    //Maximum depth allowed
    int MAX_DEPTH = 12;
    int init = 0;
    Hash_System table;
    public StudentPlayer() { super("260498154"); }
    
    int generate_probability(move_value[] moves){
    	int max = 0;
    	int ret;
    	/*
    	for(int i = 0; i < moves.length; i++){
    		moves[i].prob = 4 *((int)((moves.length-i) * moves[i].value)) + 1;
    		for(int j = i; j < moves.length; j++){
    			moves[i].prob -= 4 * moves[j].value; 
    		}
    		max += moves[i].prob;
    	}
    	*/
    	for(int i = 0; i < moves.length - 1; i++){
    		if(moves[i].value == moves[0].value){
    			moves[i].prob = 500;
    		}else
    			moves[i].prob = 1;
    		max += moves[i].prob;
    	}
    	ret = max;
    	for(int i = 0; i < moves.length; i++){
    		max -= moves[i].prob;
    		moves[i].overall_prob = max;
    		System.out.println(moves[i].overall_prob + " " + moves[i].value + " " + ret);
    	}
    	return ret;
    }
    
    public HusMove chooseMove(HusBoardState state)
    {	
    	double max = 0;
    	int[][] values;
    	int index = 0;
    	int rand;
    	ArrayList<HusMove> moves = state.getLegalMoves();
    	move_value[] distribution= new move_value[moves.size()];
    	long time = System.currentTimeMillis();
    	if(init == 0){
    		init =1;
    		table = new Hash_System(true);
    		System.out.println("Table Initialized");
    	}
    	double[] result = init_traverse(state);
    	values = table.get_values(state.getPits(), player_id);
    	for(int i = 0; i < distribution.length; i++){
    		distribution[i] = new move_value(result[i], moves.get(i));
    		if(values != null){
    			if(values[moves.get(i).getPit()][1] != 0)
    			distribution[i].value += (values[moves.get(i).getPit()][0] / values[moves.get(i).getPit()][1] -.5) * 15;
    		}
    	}
    	Arrays.sort(distribution);
    	max = generate_probability(distribution);
    	rand = (int)(Math.random() * max);
        System.out.println("Time taken: " + ((double)(time - System.currentTimeMillis()))/1000);

    	for(int i = 0; i < moves.size(); i++){
    		if(rand >= distribution[i].overall_prob)
    			return distribution[i].move;
    	}
    	return distribution[0].move;
    	
    	//Default move is 0
    	/*
    	int index = 0;
    	Hash_Value val = null;
    	Node top_moves[]; //Record the top moves we will be looking at to further expand
    	if(init == 0){
    		init =1;
    		table = new Hash_System(true);
    		System.out.println("Table Initialized");
    	}
    	else{
    		val = table.get_hash_value(state.getPits(), player_id);
    	}
    	
    	
    	//This top_moves is sorted from biggest to smallest, in terms of heuristic value
    	//neutral is our heuristic function
    	//Timer System for information
    	double time = System.currentTimeMillis();
    	
    	//Given the current value of our system, engage in a potentially deeper search -> Goes to 12 at most
    	//if(current_heuristic > 64){
    		//Recommended depth
    		//REC_DEPTH = 2;
    		//Get the values from brute_force, simulated annealing method
    		//top_moves = brute_force(state);
    	/*}
    	else{
    		if(state.getLegalMoves().size() < 9)
    			REC_DEPTH = 7;
    		else
    			REC_DEPTH = 6;
        	top_moves = ab_pruning(state);
    	}*/
    	
    	/*
		if(state.getLegalMoves().size() < 9)
			REC_DEPTH = 7;
		else
			REC_DEPTH = 6;
    	top_moves = ab_pruning(state);
    	
    	if(val != null){ //So we found it
    	
    		for(int i = 0; i < top_moves.length; i++){
    			int move_num = top_moves[i].move.getPit();
    			System.out.println(val.moves[move_num][1]);
    			if(val.moves[move_num][1] != 0){
    				top_moves[i].value += (int)((double)(val.moves[move_num][0] / val.moves[move_num][1] -.5) * 10);
    				System.out.println( " Value " + (int)((double)(val.moves[move_num][0] / val.moves[move_num][1] -.5) * 10));
    			}
    		}
    	}
		Arrays.sort(top_moves);
        System.out.println("Time taken: " + (time - System.currentTimeMillis())/1000);
    	return top_moves[index].move;
    	*/
    }

    final int victory = 1000;
    final int defeat = -1000;
    final int offense = 1;
    final int neutral = 0;
    final int defense = -1;
    
    private int offensive(int[][] value){ //For when we have the advantage, and thus want to maximize gain
		int ret = 0;
    	for(int i = 0; i < 32; i++){
    		   if(value[opponent_id][i%15] == 0){
    		    ret += 2*value[opponent_id][i];//safe holes
    		   }
    		   else ret += value[opponent_id][i];
    	}
    	
    	return ret;
    }
    
    private int defensive(int[][] value){ //For when we are disadvantaged, and thus want to minimize loss
		int ret = 0;
		for(int i = 0; i < 16; i++){
			ret += value[player_id][i];
			if(value[player_id][i] > 0)
				ret += 1;  //So we want the move that gives us the most spread out and safe moves
		}
    	return ret;
    }
    
    private int neutral(int[][] value){ //For when we are disadvantaged
		int ret = 0;
		for(int i = 0; i < 32; i++)
			ret += value[player_id][i];
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
		if(depth >= REC_DEPTH){
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
    	//Arrays.sort(result);
    	return result;
    }
    


    int counter_val = 0;
    Node hill_climb(HusBoardState state, int depth, int current){ 
    	//Actually more like simulated annealing. This is mainly to add a bit of randomness to the algorithm
    	//Not actually improves anything... just slows it down
    	//System.out.println(counter_val++ + " depth : " + depth);
    	//The point of this is to hill climb to a better solution

    	//So at level 3, there is about > 10% chance to explore a bad local option
    	//at level 6, there is about 
    	int random = ((int)(Math.random() * 100 )) % (depth * depth * depth);
    	Node opt = null;
    	Node temp;
    	int opt_value = 0;
    	int place_holder;
    	int index = 0;
    	ArrayList<HusMove> moves;
    	
		if(state.gameOver())
			if(state.getWinner() == player_id)
				return new Node(state, victory);
			else
				return new Node(state, defeat);
		moves = state.getLegalMoves();		
		if(depth >= MAX_DEPTH)
			return new Node(state, current);
		//Here we will introduce a bias
		//When we are min-maxing
		if(state.getTurnPlayer() == player_id){
			for(int i = 0; i < moves.size(); i++){
				HusBoardState cpy = (HusBoardState) state.clone();
				cpy.move(moves.get(i));
				place_holder = neutral(cpy.getPits()); //Test the value
				if(place_holder > current || random == 0){
					temp = hill_climb(state, depth + 1, place_holder);
					if(temp.value >= opt_value){
    					opt_value = temp.value;
    					opt = temp;
    				}
				}
			}
			
			if(opt == null)
				opt = new Node(state, current);
		}
		else{
			opt_value = victory;
			for(int i = 0; i < moves.size(); i++){
				HusBoardState cpy = (HusBoardState) state.clone();
				cpy.move(moves.get(i));
				place_holder = neutral(cpy.getPits()); //Test the value
				if(place_holder < opt_value){
					opt_value = place_holder;
					index = i;
				}
			}
			state.move(moves.get(index));
			opt = hill_climb(state, depth+1, current);
		}
		return opt;
    }
    
    Node brute_force_opt(HusBoardState state, int depth){
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
		
		if(depth >= REC_DEPTH){
			return hill_climb(state, depth+1, neutral(state.getPits()) + 5);	
			//return new Node(state, neutral(state.getPits()));
		}
		
    	moves = state.getLegalMoves();
    	for(int i = 0; i < moves.size(); i++){
    		HusBoardState cpy = (HusBoardState)state.clone();
    		cpy.move(moves.get(i));
    		temp = brute_force_opt(cpy, depth + 1);
    			if(state.getTurnPlayer() == player_id){
    				//max mode
    				if(temp.value >= opt_value){
    					opt_value = temp.value;
    					opt = temp;
    				}
    				//So given that we are in the max phase. If we found a value e.g   3 -> 2, 4, we don't need to look at the 4 branch anymore since they would pick 2
    			}else{ //min mode
    				if(temp.value <= opt_value){
    					opt_value = temp.value;
    					opt = temp;
    				}
    				//So given that we are in the min phase. If we found a value e.g   3 -> 4, 2 we don't need to look at the 2 branch anymore since they would pick 4    				
    			}
    		
    	}
  
    	return opt;
    }
    
    Node[] brute_force(HusBoardState state){
    	ArrayList<HusMove> moves = state.getLegalMoves();
    	Node[] result = new Node[moves.size()];
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
    		result[i] = brute_force_opt(cpy, 1);
    		//For the pruning part
    		result[i].move = moves.get(i);
    	}
    	//Arrays.sort(result);
    	return result;
    }
    
    int traverse(HusBoardState state, int depth, int prune){
    	ArrayList<HusMove> list;
    	int opt = 0;
    	int temp;
    	if(state.gameOver()){
			if(state.getWinner() == player_id)
				return victory;
			else
				return defeat;
    	}
    	if(depth == REC_DEPTH){
    		return neutral(state.getPits());
    	}
    	list = state.getLegalMoves();
    	if(depth % 2 != 0)
    		opt = victory;
    	for(int i = 0; i < list.size(); i++){
    		HusBoardState cpy = (HusBoardState)state.clone();
    		cpy.move(list.get(i));
    		temp = traverse(cpy, depth + 1, opt);
    		if(depth % 2 == 0){
    			if(temp > opt)
    				opt = temp;
    			if(temp > prune)
    				return temp;
    		}
    		else{
    			if(temp < opt)
    				opt = temp;
    			if(temp < prune)
    				return temp;
    		}
    	}
    	return opt;
    }
    
    double[] init_traverse(HusBoardState state){
    	ArrayList<HusMove> list = state.getLegalMoves();
    	double[] ret = new double[list.size()];
    	int max = 0;
    	for(int i = 0; i < list.size(); i++){
    		HusBoardState cpy = (HusBoardState)state.clone();
    		cpy.move(list.get(i));
    		ret[i] = traverse(cpy, 1, max);
    		if(ret[i] > max)
    			max = (int)ret[i];
    	}
    	return ret;
    }
    
}

