package student_player;

import hus.HusBoardState;

public class Stack_Unit {
	//Hue my struct
	HusBoardState state;
	int max_depth;
	int depth;
	
	short plays[];
	int heuristic;
	int player;
	
	
	Stack_Unit(int m_d, int depth, HusBoardState s){
		state = s;
		player = s.getTurnPlayer();
		max_depth = m_d;
		depth = 0;
		plays = new short[max_depth];
	}
	
	public Stack_Unit stack_cpy(int new_index){
		Stack_Unit ret = new Stack_Unit(max_depth, depth+1, (HusBoardState)state.clone());
		for(int i = 0; i < depth; i++)
			ret.plays[i] = plays[i];
		ret.plays[depth] = (short)new_index;
		return ret;
	}
}
