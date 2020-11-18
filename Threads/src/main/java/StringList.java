
import java.util.List;
import java.util.ArrayList;

class StringList {
    
    List<String> strings = new ArrayList<String>();

    public void add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
        }
    }

    public boolean contains(String str) {
        return strings.indexOf(str) >= 0;
    }
    
    public String getIndex(int i) {
    	if(i<size()) {
    		return strings.get(i);	
    	}
    	return null;
    }
    
    public boolean remove(int i) {
    	if(i<size()) {
    		strings.remove(i);
    		return true;
    	}
    	return false;
    }
    
    public boolean set(int i, String in) {
    	if(i<size() && in.length() > 0) {
    		strings.set(i, in);
    		return true;
    	}
    	return false;
    }

    public int size() {
        return strings.size();
    }

    public String toString() {
        return strings.toString();
    }
}