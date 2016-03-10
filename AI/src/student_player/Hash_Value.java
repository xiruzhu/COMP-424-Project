package student_player;

import java.io.Serializable;

public class Hash_Value implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1302687655950642987L;
	public int[][] moves = new int[32][2];
	
	public Hash_Value(){
	}
	
	public double get_move_value(int move){
		if(moves[move][1] == 0)
			return 0;
		return moves[move][0]/moves[move][1];
	}
	
	public int get_best_move(){
		double opt = 0;
		int index = 0;
		for(int i = 0; i < 32; i++){
			if(get_move_value(i) > opt){
				opt = get_move_value(i);
				index = i;
			}
		}
		return index;
	}
	
	public void increment(int move, boolean outcome){
		moves[move][1]++;
		if(outcome)
			moves[move][0]++;
	}
}
