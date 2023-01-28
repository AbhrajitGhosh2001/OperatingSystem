import java.util.HashMap;
import java.util.Map;

public class VFS implements Device{
    Map<Integer, Device> devices = new HashMap<Integer, Device>();
  
    Device dev = null;
    RandomDevice randomDevice = new RandomDevice();
    FakeFileSystem fakeFileSystem = new FakeFileSystem();
    PipeDevice pipeDevice = new PipeDevice();
    int vfsid=0;


    public VFS(){
        devices.put(1, randomDevice);
        devices.put(2, fakeFileSystem);
        devices.put(3, pipeDevice);
    }


    @Override
    public int Open(String s) {
        if(s.contains("random")){
            s = s.replace("random ", "");
            
                vfsid=devices.get(1).Open(s);
                dev = devices.get(1);
            
        }

        if(s.contains("file")){
            s = s.replace("file ", "");     
            vfsid=devices.get(2).Open(s);
            dev = devices.get(2);
        }

        if(s.contains("pipe")){
            s = s.replace("pipe ", "");
            vfsid=devices.get(3).Open(s);
            dev = pipeDevice;
        }
        return vfsid;
    }

    public void pick(int i){
        dev=devices.get(i);
    }

    @Override
    public void Close(int id) {
        dev.Close(id);
    }

    @Override
    public byte[] Read(int id, int size) {
        return dev.Read(id, size);
    }

    @Override
    public void Seek(int id, int to) {
        dev.Seek(id, to);;
        
    }

    @Override
    public int Write(int id, byte[] data) {
        dev.Write(id, data);
        return 0;
    }
    
}
