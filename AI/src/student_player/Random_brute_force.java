package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/** A random Hus player. */
public class Random_brute_force extends HusPlayer {
    Random rand = new Random();
    HusMove next_move;
    
    HusMove best_move[] = new HusMove[3];
    int optimality[] = new int[3];

    PriorityQueue<int[]> max = new PriorityQueue<int[]>(new Comparator<int[]>(){
		@Override
		public int compare(int[] arg0, int[] arg1) {
			if(arg0[0] > arg1[0])
				return -1;
			else if(arg0[0] == arg1[0])
				return 0;
			else
				return 1;
		}
    }
    );
    
    int counter = 0;
    final int MAX_DEPTH = 5;
    public Random_brute_force() { super("random brute force player"); }
    double avg_time = 0;
    int count = 0;
    /** Changed so it doesn't any more hue. 
     	Battle of the AI
     **/
    public HusMove chooseMove(HusBoardState board_state)
    {
        // Pick a random move from the set of legal moves.
    	int total;
    	int random;
    	double time = System.currentTimeMillis();
        best_move(0, board_state, 97);
        avg_time = (avg_time * count + (time - System.currentTimeMillis()))/(count + 1);
        count++;
        System.out.println(avg_time);
        
        
        /* Random Component AI. This is pretty decent but not as good as brute force
         * 
         * 
         */
        System.out.println(optimality[0] + " "+ optimality[1] + " " + optimality[2] + " ");
        optimality[0] = optimality[0] * 9 - 4 * optimality[1] - 4 *optimality[2];
        optimality[1] = optimality[1] * 4 - 3 * optimality[2];
        optimality[2] = optimality[2];
        total = optimality[0] + optimality[1] + optimality[2];
        System.out.println(optimality[0] + " "+ optimality[1] + " " + optimality[2] + " " + total);
        
        random = (int) (Math.random() * total);
        if(random < optimality[2])
        	next_move = best_move[2];
        else if(random < optimality[1] + optimality[2])
        	next_move =  best_move[1];
        else
        	next_move =  best_move[0];
        	//return monte_ai(board_state);
        
        //random component
        // 40 60 80
        // avg = 60
        // x^2
        //Cleanup
        max.removeAll(max);
        optimality[0] = -1;
        optimality[1] = -1;
        optimality[2] = -1;
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
    

    
    //Brute force 5 level look ahead with Alpha Beta Pruning
    int best_move(int depth, HusBoardState state, int pruning){
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
    		
    		temp = best_move(depth + 1, board_cpy, opt);
    		if(depth % 2 == 0){
    			if(temp > opt){
    				opt = temp;
    				index = i;
    			}
    			
    			if(opt > pruning){
    				return 97; //The previous level would never select this one so
    			}
    		}
    		else if(depth % 2 == 1){
    			if(temp < opt){
    				opt = temp;
    				index = i;
    			}
    			if(opt < pruning){
    				return -1; //The previous level would never select this one so
    			}
    		}
    		
    		if(depth == 0){
    			max.add(new int[]{temp,i});
    		}
    			
    		
    	}
    	//System.out.println("Value " + opt);
    	if(depth == 0){
    		next_move = legal_move.get(index);
    		for(int i = 0; i < 3; i++){
    			if(i < max.size()){
    			optimality[i] = max.peek()[0];
    			best_move[i] = legal_move.get(max.poll()[1]);
    			}
    			else
    				best_move[i] = null;
    		}
    	}
    	return opt;

    }
    
    public int monte_descent(HusBoardState state, HusMove move){
    	int rand;
    	int count = 0;
    	HusBoardState cpy = (HusBoardState) state.clone();
    	cpy.move(move); //Set up the initial stuff
    	
    	while(!cpy.gameOver() && count <= 200){
    		count++;
    		ArrayList<HusMove> moves = cpy.getLegalMoves();
    		rand = (int)(moves.size() * Math.random());
    		cpy.move(moves.get(rand));
    	}
    	
    	if(count == 200)
    		return 1;
    	if(state.getTurnPlayer() != cpy.getWinner())
    		return 2;
    	else
    		return 0;
    }
    
    public HusMove monte_ai(HusBoardState state){
    	//ArrayList<HusMove> moves = state.getLegalMoves();
    	int move_value[] = new int[3];
    	int max = 0;
    	int max_move = 0;
    	for(int i = 0; i < 3; i++){
    		if(best_move[i] != null)
    		for(int j = 0; j < 1500; j++){
    			move_value[i] += monte_descent(state, best_move[i]);
    		}
    	}
    	
    	
    	for(int i = 0; i < 3; i++){
    		if(move_value[i] > max){
    			max = move_value[i];
    			max_move = i;
    		}
    	}
    	
    	for(int i = 0; i < 3; i++)
    		System.out.println("Testing " + move_value[i]);
    	
    	return best_move[max_move];
    }
    
}
