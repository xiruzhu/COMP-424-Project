package student_player;

import java.io.Serializable;

public class Hash_Key implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1138749438965802693L;
	int[][] values;
	int side;
	final int length = 32;
	
	//Our hash function
	public Hash_Key(int[][] key, int side){
		this.side = side;
		values = key;
	}
    public int hashCode(){
	   int result = 0;
	   int shift = 0;
	   if(values == null)
		   return 0;
	   for (int i = 0; i < 32; i++)
	   {
	      shift = (shift + 11) % 21;
	      result ^= (values[0][i]+1024) << shift;
	   }
	   for (int i = 0; i < 32; i++)
	   {
	      shift = (shift + 11) % 21;
	      result ^= (values[0][i]+1024) << shift;
	   }
	      shift = (shift + 11) % 21;
	      result ^= (side+1024) << shift;
	   return result;
	}
    
    //The equality function
    public boolean equals(Object arg0){
    	if(arg0 == null)
    		return false;
    	int[][] pits = ((Hash_Key)arg0).values;
    	for(int i = 0; i < length; i++){
    		if(pits[0][i] != values[0][i] || pits[1][i] != values[1][i])
    			return false;
    	}
    	if(((Hash_Key)arg0).side != side){
    		return false;
    	}
    	return true;
    }
}
