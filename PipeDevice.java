import java.util.HashMap;
import java.util.Map;

public class PipeDevice implements Device{
    Map<Integer,  PipeInstance> arr = new HashMap<Integer,  PipeInstance>();
    int index;
    PipeInstance buffer;
    int off=0;

    @Override
    public int Open(String s) {
        boolean bool =false;
       for (Map.Entry<Integer, PipeInstance> set :arr.entrySet()){
        if(set.getValue().getName()==s){
          index=set.getKey();
          bool=true;
        } 
        else{
            buffer= new PipeInstance(s);
            off++;
            arr.put(off, buffer);
        }
       }
       if(bool){
        buffer=arr.get(index);
        buffer.attach();
        arr.put(index, buffer);
        bool=false;
       }
        return index;
    }

    @Override
    public void Close(int id) {
        buffer=arr.get(id);
        buffer.detach();
        arr.put(index, buffer);
    }

    @Override
    public byte[] Read(int id, int size) {
        byte [] b = new byte[size];
        buffer=arr.get(id);
        int place = buffer.getIndex();
        for(int x=place;x<size+place;x++){
            b[x]=arr.get(id).getData(x);
        }
        buffer.setIndex(size);
        return b;
    }

    @Override
    public void Seek(int id, int to) {
        Read(id, to);
    }

    @Override
    public int Write(int id, byte[] data) {
        buffer=arr.get(id);
        buffer.setData(data);
        arr.put(id, buffer);
        return 0;
    }
    
}
