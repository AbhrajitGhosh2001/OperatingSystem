import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device {
    RandomAccessFile[] array = new RandomAccessFile[10];
    int index=-1;
    

    @Override
    public int Open(String s)  {
        try {
            for(int x=0;x<10;x++){
                if(array[x]==null){
                    array[x]=new RandomAccessFile(s, "rw");
                    index=x;
                    x=10;
                }
                else{
                    
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return index;
    }

    @Override
    public void Close(int id) {
        try {
            array[id].close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] Read(int id, int size) {
        byte[] b= new byte[size];
        try {
            for(int x=0;x<size;x++){
                b[x]=array[id].readByte();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    @Override
    public void Seek(int id, int to) {
        for(int x=0;x<to;x++){
            try {
                array[id].seek(x);
            } catch (IOException e) {       
                e.printStackTrace();
            }
        }
    }

    @Override
    public int Write(int id, byte[] data) {
        try {
            array[id].write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
