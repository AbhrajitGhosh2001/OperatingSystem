
import java.util.HashMap;

public class MutexObject implements Mutex{

    String name;
    boolean isHeld;
    HashMap<Integer, Integer> process = new HashMap<>();
    //chamge back to hash map becaise must unheld when released
    int current;

    @Override
    public int AttachToMutex(String name) {
        setName(name);
        return 0;
    }

    @Override
    public boolean Lock(int mutexId) {
        if(isHeld==true){   
            return false;
        }
        else{
            isHeld=true;
            process.put(current, 1);
            return true;
        }
    }

    @Override
    public void Unlock(int mutexId) {
        if(isHeld==true){
            isHeld=false;
        }
    }

    @Override
    public void ReleaseMutex(int mutexId) {
        if(process.get(current)==1){
            isHeld=false;
        }
       process.remove(current);
       if(process.size()==0){
        name=null;
       }
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void add(int pid){
        process.put(pid, 0);
        current=pid;
    }

    public void remove(int pid){
        if(process.get(pid)==1){
            isHeld=false;
        }
        process.remove(pid);
    }

}
