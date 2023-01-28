import java.util.ArrayList;

public class OS implements OSInterface{

    


     private static PriorityScheduler schedule = new PriorityScheduler();
    ArrayList<Integer> vfsid = new ArrayList<Integer>();

    @Override
    public void WriteMemory(int address, byte value) throws RescheduleException {
        schedule.WriteMemory(address, value);
        
    }


    @Override
    public byte ReadMemory(int address) throws RescheduleException {
        
        return schedule.ReadMemory(address);
    }


    @Override
    public int sbrk(int amount) {
       
        return schedule.sbrk(amount);
    }


   
    public int CreateProcess(UserlandProcess myNewProcess, PriorityEnum.Level eLevel) {
        return schedule.CreateProcess(myNewProcess, eLevel);
    }

   
    public boolean DeleteProcess(int processId) {
       return schedule.DeleteProcess(processId);
    }

    public void connection(int processId){
        schedule.connection(0, vfsid);
    }

    
    public void Sleep() {
        schedule.Sleep();
    }

    
    public void run() {
            schedule.run();
       
    }


    @Override
    public int Open(String s) {
        vfsid.add(schedule.Open(s));
        return 0;
    }


    @Override
    public void Close(int id) {
       schedule.Close(id);
    }


    @Override
    public byte[] Read(int id, int size) {
        return schedule.Read(id, size);
    }


    @Override
    public void Seek(int id, int to) {
        schedule.Seek(id, to);
    }


    @Override
    public int Write(int id, byte[] data) {
        
        return schedule.Write(id, data);
    }

    public void pick(int i){
        schedule.pick(i);
    }


    @Override
    public int AttachToMutex(String name) {
        
        return schedule.AttachToMutex(name);
    }


    @Override
    public boolean Lock(int mutexId) {
       
        return schedule.Lock(mutexId);
    }


    @Override
    public void Unlock(int mutexId) {
        Unlock(mutexId);
        
    }


    @Override
    public void ReleaseMutex(int mutexId) {
        ReleaseMutex(mutexId);
    }


    

 
    
}
