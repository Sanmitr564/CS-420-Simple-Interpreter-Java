import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScopeList<E> extends ArrayList<HashMap<Identifier, E>> {

    private boolean initialScopeLocked;

    public ScopeList(){
        super();
        add(new HashMap<>());
        initialScopeLocked = false;
    }

    public E get(Identifier key) throws Exception {
        for(int i = size() - 1; i >= 0; i--){
            Map<Identifier, E> map = get(i);
            if(map.containsKey(key)){
                return map.get(key);
            }
        }
        throw new Exception("Identifier not found in scopeList");
    }

    public boolean containsKey(Identifier key){
        for(int i = size() - 1; i >= 0; i--){
            Map<Identifier, E> map = get(i);
            if(map.containsKey(key)){
                return true;
            }
        }
        return false;
    }

    public boolean keyReserved(Identifier key){
        boolean reserved = false;
        if(initialScopeLocked){
            reserved = get(0).containsKey(key);
        }
        return reserved | get(size() - 1).containsKey(key);
    }

    public E put(Identifier key, E value) throws Exception {
        if(initialScopeLocked && get(0).containsKey(key)){
            throw new Exception("Reserved identifier");
        }
        return get(size() - 1).put(key, value);
    }

    public void increaseScope(){
        add(new HashMap<>());
    }

    public void decreaseScope() throws Exception {
        if(size() == 1){
            throw new Exception("Cannot remove initial scope");
        }
        remove(size() - 1);
    }

    public void lockInitialScope(){
        this.initialScopeLocked = true;
    }
}
