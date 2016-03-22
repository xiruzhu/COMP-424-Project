package student_player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import hus.HusBoardState;
import hus.HusMove;

public class Sandbox {
	final int MAX_TURN = 1000;
	HusBoardState simulation;
	AI learning_AI;
	AI model_AI;
	int learner_id;
	int model_id;
	int turns;
	
	double[][] optimal_weights;	
	double optimal_eval = 0;
	
	double[][] current_weights;
	double current_eval = 0;
	
	public Sandbox(){
		simulation = new HusBoardState();
		learner_id = 0;
		model_id = 1;
		learning_AI = new AI(learner_id, model_id);
		model_AI = new AI(model_id, learner_id);	
		current_weights = learning_AI.weights;
	}
	
	public void init_set(int random_games, int model_games){
		
	}
	
	public void play_round(){
		for(int turns = 0; turns < MAX_TURN; turns++){
			if(simulation.gameOver()){
				print_final_state();
				break;
			}
			
			if(turns % 2 == learner_id){
				simulation.move(learning_AI.get_move(simulation));
			}else{
				simulation.move(model_AI.get_move(simulation));
			}
		}
	}
	
	public double evaluation_function(int turns, boolean win){
		//The formula is as follows
		/*
		Obj(weights, outcome) = 1000 - 1000/(1 + e^(turn_number/100 - 5)), if win
				   = 1000 + 1000/(1 + e^(-turn_number/100 + 5)), if lose
		*/	
		if(win){
			return 1000 - 1000/(1 + Math.pow(Math.E, (double)turns/100.0 - 5.0));
		}else{
			return 1000 + 1000/(1 + Math.pow(Math.E, (double)turns/100.0 - 5.0));
		}
	}
	
	void print_final_state(){
		int[][] pits = simulation.getPits();
		System.out.println("\n-------------------------Results------------------------\n");
		for(int i = 15; i >= 0; i--){
			System.out.print("[" + i + "]: " + pits[0][i] + " ");
		}
		for(int i = 16; i < 32; i++){
			System.out.print("[" + i + "]: " + pits[0][i] + " ");
		}
		System.out.println("\n-------------------------RIVER------------------------\n");
		for(int i = 15; i >= 0; i--){
			System.out.print("[" + i + "]: " + pits[1][i] + " ");
		}
		for(int i = 0; i < 16; i++){
			System.out.print("[" + i + "]: " + pits[1][i] + " ");
		}
		System.out.println("\n-------------------------END------------------------\n");
	}
	
	private class AI{
		final int MAX_DEPTH = 6;
		final int VICTORY = 2000;
		final String FILE_PATH = "weights.txt";
		double[][] weights;
		boolean learner;
		int player_id;
		int opponent_id;
		
		AI(int player_id, int opponent_id){
			 this.player_id = player_id;
			 this.opponent_id = opponent_id;
			 this.weights = new double[2][32];
			 this.learner = true;
			 for(int i = 0; i < this.weights.length; i++){
				 this.weights[0][player_id] = 1; 
				 this.weights[0][player_id] = 0;
			 }
		}
		
		AI(boolean learner, int player_id, int opponent_id){
			this.learner = learner;
			 this.player_id = player_id;
			 this.opponent_id = opponent_id;
			 read_weights();
		}
		
		public void read_weights(){
	        try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH));
				weights = (double[][])ois.readObject();
				ois.close();
			} catch (FileNotFoundException e) {
				weights = new double[2][32];
				for(int i = 0; i < this.weights.length; i++){
					 this.weights[0][player_id] = 1; 
					 this.weights[0][player_id] = 0;
				 }
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void write_weights(){
			try {
			FileOutputStream fos = new FileOutputStream(FILE_PATH);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(weights);
			oos.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
		
		HusMove get_move(HusBoardState state){
			if(learner){
					ArrayList<HusMove> moves = new ArrayList<HusMove>();
					move_value[] result =  init_traverse(state);
					for(int i = 0; i < result.length; i++){
						if(result[i].value == result[0].value)
							moves.add(result[i].move);
					}
					return moves.get( (int)(Math.random() * moves.size()) );
				}
			else{
				ArrayList<HusMove> list = state.getLegalMoves();
				return list.get( (int)(Math.random() * list.size()) );
			}
		}
		
		double heuristic(int[][] pits){
			double ret = 0;
			for(int i = 0; i < pits.length; i++){
				ret += weights[i][0] * pits[0][i] + weights[1][i] * pits[1][i]; 
			}
			return ret;
		}
		
	    double traverse(HusBoardState state, int depth, double prune){
	    	ArrayList<HusMove> list;
	    	double opt = 0;
	    	double temp;
	    	if(state.gameOver() || depth == MAX_DEPTH){
				return heuristic(state.getPits());
	    	}
	    	list = state.getLegalMoves();
	    	if(depth % 2 != 0)
	    		opt = VICTORY;
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
	    
	    move_value[] init_traverse(HusBoardState state){
	    	ArrayList<HusMove> list = state.getLegalMoves();
	    	move_value[] ret = new move_value[list.size()];
	    	double max = 0;
	    	for(int i = 0; i < list.size(); i++){
	    		HusBoardState cpy = (HusBoardState)state.clone();
	    		cpy.move(list.get(i));
	    		ret[i] = new move_value(traverse(cpy, 1, max), list.get(i));
	    		if(ret[i].value > max)
	    			max = (int)ret[i].value;
	    	}
	    	Arrays.sort(ret);
	    	return ret;
	    }
	}
	
	public class move_value implements Comparable<Object>{
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
}
