package objects;

public class Map < KeyType, ItemType >{
	
	private KeyType[] keys;
	private ItemType[] items;
	
	@SuppressWarnings("unchecked")
	public Map(){
		keys =  (KeyType[]) new Object[0];
		items = (ItemType[]) new Object[0];
	}
	
	@SuppressWarnings("unchecked")
	public void add(KeyType key, ItemType item){
		if (!contains(key)){
			keys = (KeyType[]) augment(keys, key);
			items = (ItemType[]) augment(items, item);
		}
		else {
			set(key, item);
		}
	}
	
	public ItemType get(KeyType key){
		for (int x = 0; x < keys.length; x++){
			if (key.equals(keys[x])){
				return items[x];
			}
		}
		return null;
	}
	
	public void set(KeyType key, ItemType item){
		int x = 0;
		while (x < keys.length){
			if (keys[x].equals(key)){
				items[x] = item;
				break;
			}
			x++;
		}
	}
	
	public boolean contains(KeyType key){
		int x = 0;
		while (x < keys.length){
			if (keys[x].equals(key)){
				return true;
			}
			x++;
		}
		return false;
	}
	
	public KeyType[] getKeys(){
		return keys;
	}
	
	public ItemType[] getValues(){
		return items;
	}
	
	@SuppressWarnings("unchecked")
	public void remove(KeyType key){
		try {
			items = (ItemType[]) remove(items, get(key));
			keys = (KeyType[]) remove(keys, key);
		} catch (NullPointerException e) {}
	}
	
	public int size(){
		return items.length;
	}
	
	public boolean isEmpty(){
		return (items.length == 0);
	}
	
	private Object[] augment(Object[] array, Object val){
		Object[] out = new Object[array.length + 1];
		for (int x = 0; x < array.length; x++){
			out[x] = array[x];
		}
		out[array.length] = val;
		return out;
	}
	
	private Object[] remove(Object[] array, Object val) {
		Object[] out = new Object[array.length - 1];
		int x = 0;
		while (x < out.length && !array[x].equals(val)){
			out[x] = array[x];
			x++;
		}
		if (x != out.length){
			while (x < out.length){
				out[x] = array[x+1];
				x++;
			}
		}
		else {
			return array;
		}
		return out;
	}

	@Override
	public String toString(){
		String out = "<";
		int x = 0;
		while (x < items.length-1){
			out += keys[x] + ":" + items[x] + ", ";
			x++;
		}
		out += keys[x] + ":" + items[x] + ">";
		return out;
	}
	
}