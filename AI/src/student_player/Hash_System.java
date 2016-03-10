package student_player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

public class Hash_System {
	/*
	 * The Hash System, uses java hashtable as basis
	 * Allows me to drop moves in the system and check values
	 */
	private static final int table_size = 65536; //Hue
	private static final String disk_file_name = "table.txt";
	private static double capacity = 0;
	private static Hashtable<Hash_Key, Hash_Value> table;
	
	public Hash_System(boolean load_file){
		if(load_file)
			read_table();
		else
			table = new Hashtable<Hash_Key, Hash_Value>(table_size);
	}
	
	public int[][] get_values(int[][] key, int side){
		Hash_Value val = table.get(new Hash_Key(key, side));
		if(val == null)
			return null;
		return val.moves;
	}
	
	public int get_move(int[][] key, int side){
		Hash_Value val = table.get(new Hash_Key(key, side));
		if(val == null)
			return -1;
		else
			return val.get_best_move();
	}
	
	Hash_Value get_hash_value(int[][] key, int side){
		return table.get(new Hash_Key(key, side));
	}
	
	public void add_move(int[][] key, int move, int side, boolean outcome){
		Hash_Key key_val = new Hash_Key(key, side);
		Hash_Value value = table.get(key_val);
		if(value == null){
			value = new Hash_Value();
			value.increment(move, outcome);
		}
		else
			value.increment(move, outcome);
			
		table.put(key_val, value);
	}
	//Reads the current HashTable to disk
	public void read_table(){
        try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(disk_file_name));
			table = (Hashtable)ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			table = new Hashtable<Hash_Key, Hash_Value>(table_size);
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
	
	//Writes the current HashTable to disk
	public void write_table(){
			try {
			FileOutputStream fos = new FileOutputStream(disk_file_name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(table);
			oos.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
	public double update_capacity(){
		capacity = ((double)table.size()/(double)table_size);
		return capacity;
	}

}
