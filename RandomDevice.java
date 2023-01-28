
import java.util.Random;

public class RandomDevice implements Device {
    Random array [] = new Random[10];
    Random random;
    int seed;
   
    public int Open(String s) {
        int index=-1;
        if(s.equals(null)||s.isEmpty())
            { 
                random = new Random();
                for(int x=0;x<10;x++){
                    if(array[x]==null){
                        array[x]=random;
                        index=x;
                        x=10;
                    }
                    else{
                        
                    }
                }
            }
        else{
        seed=Integer.parseInt(s);
        random = new Random(seed);

        for(int x=0;x<10;x++){
            if(array[x]==null){
                array[x]=random;
                index=x;
                x=10;
            }
            else{
                
            }
        }
     }
       return index;
    }

  
    public void Close(int id) {
        array[id]=null;
        random = null;
    }

 
    public byte[] Read(int id, int size) {
        byte[] b = new byte[size];
        array[id].nextBytes(b);
        return b;
    }


    public void Seek(int id, int to) {
        Read(id, to);
    }

    public int Write(int id, byte[] data) {
        return 0;
    }
   
    
    
}
