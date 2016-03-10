package student_player;

import hus.HusBoardState;
import hus.HusMove;

public class Node implements Comparable{

	public HusBoardState state;
	public HusMove move;
	public int value;
	public int move_num;

	public Node(HusBoardState state, int heur){
		this.state = state;
		this.value = heur;
	}
	
	public Node(HusBoardState state, int heur, int move_num){
		this.state = state;
		this.value = heur;
		this.move_num = move_num;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		if(arg0 == null)
			return 1;
		else if(this.value < ((Node)arg0).value)
			return 1;
		return -1;
	}
	
	
	

}
