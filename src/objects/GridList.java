package objects;

import java.util.ArrayList;
import java.util.TreeMap;

public class GridList<T> {

	private ArrayList<TreeMap<Integer, ArrayList<TreeMap<Integer, T>>>> data;
	private int size;
	private int stored;
	
	private boolean expanding = false;
	
	public GridList(){
		size = 10;
		data = new ArrayList<TreeMap<Integer, ArrayList<TreeMap<Integer, T>>>>();
		for (int i = 0; i < size; i++){
			data.add(new TreeMap<Integer, ArrayList<TreeMap<Integer, T>>>());
		}
		stored = 0;
	}
	
	private void put(T val, int x, int y, boolean allowExpand){
		try {
			data.get(x%size).hashCode();
		}
		catch (NullPointerException e){
			data.add(x%size, new TreeMap<Integer, ArrayList<TreeMap<Integer, T>>>());
		}
		finally {
			try {
				data.get(x%size).get(x).hashCode();
			}
			catch (NullPointerException e){
				data.get(x%size).put(x, new ArrayList<TreeMap<Integer, T>>());
				for (int i = 0; i < size; i++){
					data.get(x%size).get(x).add(new TreeMap<Integer, T>());
				}
			}
			finally{
				if (!data.get(x%size).get(x).get(y%size).keySet().contains(y)){
					stored++;
				}
				data.get(x%size).get(x).get(y%size).put(y, val);
				if (stored/(double)(size*size) > 5 && allowExpand){
					expand();
				}				
			}
		}
	}
	
	public void put(T val, int x, int y){
		put(val, x, y, true);
	}
	
	public T get(int x, int y){
		try {
			return data.get(x%size).get(x).get(y%size).get(y);			
		}
		catch (NullPointerException e){
			return null;
		}
	}
	
	public int size(){
		return stored;
	}
	
	private void expand(){
		GridList<T> org = clone();
		data = new ArrayList<TreeMap<Integer, ArrayList<TreeMap<Integer, T>>>>();
		size *= 2;
		for (int i = 0; i < size; i++){
			data.add(new TreeMap<Integer, ArrayList<TreeMap<Integer, T>>>());
		}
		for (int i = 0; i < org.data.size(); i++){
			for (int x : org.data.get(i).keySet()){
				for (int j = 0; j < org.data.get(i).get(x).size(); j++){
					for (int y : org.data.get(i).get(x).get(j).keySet()){
						put(org.data.get(i).get(x).get(j).get(y), x, y, false);
					}
				}
			}
		}
	}
	
	public GridList<T> clone(){
		// ArrayList<TreeMap<Integer, ArrayList<TreeMap<Integer, T>>>>
		GridList<T> out = new GridList<T>();
		out.size = size;
		for (int i = 0; i < size; i++){
			out.data.add(new TreeMap<Integer, ArrayList<TreeMap<Integer, T>>>());
		}
		for (int i = 0; i < data.size(); i++){
			for (int x : data.get(i).keySet()){
				for (int j = 0; j < data.get(i).get(x).size(); j++){
					for (int y : data.get(i).get(x).get(j).keySet()){
						out.put(data.get(i).get(x).get(j).get(y), x, y, false);
					}
				}
			}
		}
		return out;
	}
	
	public String genorateList(){
		String out = "";
		for (int i = 0; i < data.size(); i++){
			for (int x : data.get(i).keySet()){
				for (int j = 0; j < data.get(i).get(x).size(); j++){
					for (int y : data.get(i).get(x).get(j).keySet()){
						out += x + "\t" + y + "\n";
					}
				}
			}
		}
		return out;
	}
	
}
