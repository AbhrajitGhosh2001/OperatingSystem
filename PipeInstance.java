import java.util.ArrayList;

public class PipeInstance {
    String name;
    int counter=0;
    int index=0;
    ArrayList<byte[]> arr;
    byte[] data;


    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
    
    public PipeInstance(String s){
        name=s;
    }
   
    public String getName() {
        return name;
    }

    public byte getData(int i) {
        return data[i];
    }

    public void setData(byte[] data) {
        this.data = data;
        arr.add(data);
    }

    public void detach(){
        arr.remove(counter);
        counter=counter-1;
    }

    public void attach(){
        counter++;
        data=null;
    }



}
