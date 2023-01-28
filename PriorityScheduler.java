import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PriorityScheduler implements OSInterface{

    MemoryManagement management = new MemoryManagement();

    int tlbVirtual=-1, tlbPhysical=-1;


    MutexObject[] muteList= new MutexObject[10];
    
    //move from wait que to non wait que
    @Override
    public int AttachToMutex(String name) {
        for(int x=0;x<10;x++){
            if(muteList[x].getName().equals(name)){
                muteList[x].add(pid);
                return x;
            }
            else if(muteList[x].getName()==null){
                muteList[x]= new MutexObject();
                muteList[x].AttachToMutex(name);
                muteList[x].add(pid);
                return x;
            }
        }
        return 0;
    }

    //if false put in wait list
    @Override
    public boolean Lock(int mutexId) {
        boolean locked=muteList[mutexId].Lock(mutexId);
        if(locked==false){
            int where = whichList.get(pid);
            if(where==1){
                current=realTimeList.get(pid);
                waitque.put(pid, current);
                realTimeList.remove(pid);
            }
    
            if(where==2){
                current=interactiveList.get(pid);
                waitque.put(pid, current);
                interactiveList.remove(pid);
            }
            if(where==3){
                current=backgroundList.get(pid);
                waitque.put(pid, current);
                backgroundList.remove(pid);
            }
        }
        return locked;
    }


    //move item to wait list
    @Override
    public void Unlock(int mutexId) {
       muteList[mutexId].Unlock(mutexId); 
       int where = whichList.get(pid);
    if(where==1){
        current=waitque.get(pid);
        realTimeList.put(pid, current);
        waitque.remove(pid);
    }

    if(where==2){
        current=waitque.get(pid);
        interactiveList.put(pid, current);
        waitque.remove(pid);
    }
    if(where==3){
        current=waitque.get(pid);
        backgroundList.put(pid, current);
        waitque.remove(pid);
    }
    }

    @Override
    public void ReleaseMutex(int mutexId) {
        muteList[mutexId].ReleaseMutex(mutexId);
    }



    public void tlbInvalidater(){
        tlbVirtual=Integer.MAX_VALUE;
        tlbPhysical=Integer.MAX_VALUE;
    }

    @Override
    public void WriteMemory(int address, byte value) throws RescheduleException {
    
        if(address>1024*1024){
            DeleteCurrent();
            throw new RescheduleException();
        }
        int where = whichList.get(pid);


        if(where==1){
            current=realTimeList.get(pid);
            management.sendCurrent(current);
        }

        if(where==2){
            current=interactiveList.get(pid);
            management.sendCurrent(current);
           
        }

        if(where==3){
            current=backgroundList.get(pid);
            management.sendCurrent(current);
        }

        if (tlbVirtual>address/1024){
            management.WriteMemory(address,value);
            }
            
        else{
                if(tlbPhysical>=address%1024){
                management.WriteMemory(address,value);
                }
                else{ 
                    DeleteCurrent();
                    tlbInvalidater();
                 throw new RescheduleException();
                }
            }
            if(where==1){
                realTimeList.put(pid, management.getPass());
            }
            if(where==2){
                interactiveList.put(pid, management.getPass());
            }
            if(where==3){
                backgroundList.put(pid, management.getPass());
            }
    }


    @Override
    public byte ReadMemory(int address) throws RescheduleException {
        //Out of bounds memory access  kills process appropriately
        if(address>1024*1024){
            DeleteCurrent();
             throw new RescheduleException();
        }

        byte bite=-1;
        if (tlbVirtual>=address/1024){
            bite =management.ReadMemory(address);
            }
            else{
                if(tlbPhysical>=address%1024){
                bite= management.ReadMemory(address);
                }
                else{ 
                    DeleteCurrent();
                    tlbInvalidater();
                 throw new RescheduleException();
                }
            }
        return bite;
    }


    @Override
    public int sbrk(int amount) {

        //pick random victim
        int randm=0;
        Random rn = new Random();
        randm = rn.nextInt(pid) + 1;
        int wher = whichList.get(randm);
        if(wher==1){
            management.pickVictim(realTimeList.get(randm));
        }
        if(wher==2){
            management.pickVictim(interactiveList.get(randm));
        }
        if(wher==3){
            management.pickVictim(backgroundList.get(randm));
        }



        if(amount>1024*1024){
            DeleteCurrent();
            throw new OutOfMemoryError();
        }


        int where = whichList.get(pid);
       
        if(tlbVirtual==-1&&tlbPhysical==-1){
            tlbVirtual=0;
            tlbPhysical=0;
        }
        int pastVirtual=tlbVirtual;
        tlbVirtual=amount/1024+tlbVirtual;
        tlbPhysical=amount%1024;
        if(tlbPhysical>1){
            tlbVirtual++;
        }

        //setting spots to-1-1 for amount sbrkd

        if(where==1){
            current=realTimeList.get(pid);
            current.setSpots(pastVirtual, tlbVirtual,amount%1024);
            realTimeList.put(pid, current);
        }

        if(where==2){
            current=interactiveList.get(pid);
            current.setSpots(pastVirtual, tlbVirtual,amount%1024);
            interactiveList.put(pid, current);
        }

        if(where==3){
            current=backgroundList.get(pid);
            current.setSpots(pastVirtual, tlbVirtual,amount%1024);
            backgroundList.put(pid, current);
        }

        return management.sbrk(amount);
    }


    public void DeleteCurrent(){
        DeleteProcess(pid);
        pid=pid-1;
    }

    @Override
    public int Open(String s) {

        return vfs.Open(s);
    }


    @Override
    public void Close(int id) {
        
        vfs.Close(id);
    }

    @Override
    public byte[] Read(int id, int size) {
        
        return vfs.Read(id, size);
    }

    @Override
    public void Seek(int id, int to) {
        vfs.Seek(id, to);
        
    }

    @Override
    public int Write(int id, byte[] data) {
        
        return vfs.Write(id, data);
    }


    VFS vfs = new VFS();
    int vfsid=0;
    ArrayList<Integer> vfsids;
    KernelandProcess passinids;

    Map<Integer, KernelandProcess> realTimeList = new HashMap<Integer, KernelandProcess>();
    Map<Integer, KernelandProcess> interactiveList = new HashMap<Integer, KernelandProcess>();
    Map<Integer, KernelandProcess> backgroundList = new HashMap<Integer, KernelandProcess>();

    //waitque
    Map<Integer, KernelandProcess> waitque = new HashMap<Integer, KernelandProcess>();

    //for sleep tracking
    ArrayList<Integer> remove = new ArrayList<Integer>();
    Map<Integer, Integer> sleeptime = new HashMap<Integer, Integer>();
    Map<Integer, KernelandProcess> sleepList = new HashMap<Integer, KernelandProcess>();
    int timeused=0;

    //for downgrade tracking
    Map<Integer, Integer> downgrade = new HashMap<Integer, Integer>();
    ArrayList<Integer> moveddown = new ArrayList<Integer>();
    int lastpid = 0;

    //for list tracking
    Map<Integer, Integer> whichList = new HashMap<Integer, Integer>();

    //for itterating list
    int realtimetrack=0;
    int backgroundtrack=0;
    int interactivetrack=0;
    int pid=0;

    //list track insert
    int realTimeInsert=0;
    int backgroundInsert=0;
    int interactiveInsert=0;

    //remove to run
    KernelandProcess current = new KernelandProcess();


    
    //done
    @Override
    public boolean DeleteProcess(int processId) {
    
        for(int x=0;x<1;x++){
            muteList[x].remove(processId);
        }

        boolean bool=false;
        ArrayList<Integer> Spots = new ArrayList<Integer>();
        //freespace when process is deleted
        if(whichList.get(processId)==1){
           Spots = realTimeList.get(processId).getSpots();
        }
        if(whichList.get(processId)==2){
            Spots = interactiveList.get(processId).getSpots();
        }
        if(whichList.get(processId)==3){
            Spots = backgroundList.get(processId).getSpots();
        }

        management.freeList(Spots.get(0), Spots.get(Spots.size()));



        for (Map.Entry<Integer, KernelandProcess> set :realTimeList.entrySet()){
                if (set.getValue().getPid()==processId){
                    passinids=set.getValue();
                realTimeList.remove(set.getKey());
                bool=true;
                }
        }
        for (Map.Entry<Integer, KernelandProcess> set :interactiveList.entrySet()){
            if (set.getValue().getPid()==processId){
                passinids=set.getValue();
            interactiveList.remove(set.getKey());
            bool=true;
            }
        }
        for (Map.Entry<Integer, KernelandProcess> set :backgroundList.entrySet()){
            if (set.getValue().getPid()==processId){
                passinids=set.getValue();
            backgroundList.remove(set.getKey());
            bool=true;
            }
        }
        for (Map.Entry<Integer, KernelandProcess> set :sleepList.entrySet()){
            if (set.getValue().getPid()==processId){
                passinids=set.getValue();
            sleepList.remove(set.getKey());
            bool=true;
            }
        }

        //close all devices connected
        ArrayList<Integer> removefromvfs = new ArrayList<Integer>();
        removefromvfs=passinids.getVfsids();
        for(int x=0;x<removefromvfs.size();x++){
            if(removefromvfs.get(x)>=20){
                pick(3);
                vfs.Close(removefromvfs.get(x)-20);
            }
            else if(removefromvfs.get(x)>=10){
                pick(2);
                vfs.Close(removefromvfs.get(x)-10);
            }
            else{
                pick(1);
                vfs.Close(removefromvfs.get(x));
            }
        }

        

        return bool;
    }

    public void pick(int i){
        vfs.pick(i);
    }


    public void connection(int processId,ArrayList<Integer> vfsid2){
        for (Map.Entry<Integer, KernelandProcess> set :realTimeList.entrySet()){
                if (set.getValue().getPid()==processId){
                passinids=set.getValue();
                passinids.setVfsids(vfsid2);
                realTimeList.put(set.getKey(), passinids);
                }
        }
        for (Map.Entry<Integer, KernelandProcess> set :interactiveList.entrySet()){
            if (set.getValue().getPid()==processId){
                passinids=set.getValue();
                passinids.setVfsids(vfsid2);
               interactiveList.put(set.getKey(), passinids);

            }
        }
        for (Map.Entry<Integer, KernelandProcess> set :backgroundList.entrySet()){
            if (set.getValue().getPid()==processId){
                passinids=set.getValue();
                passinids.setVfsids(vfsid2);
                backgroundList.put(set.getKey(), passinids);
            }
        }
        for (Map.Entry<Integer, KernelandProcess> set :sleepList.entrySet()){
            if (set.getValue().getPid()==processId){
                passinids=set.getValue();
                passinids.setVfsids(vfsid2);
                sleepList.put(set.getKey(), passinids);
            }
        }
    }




    

    @Override
    public void Sleep() {
        for (int i = 0; i < remove.size(); i++){
            for (Map.Entry<Integer, KernelandProcess> set :realTimeList.entrySet()){
                if (set.getValue().getPid()==remove.get(i)){
                sleepList.put(remove.get(i),set.getValue());
                realTimeList.remove(set.getKey());
                }
             }
            for (Map.Entry<Integer, KernelandProcess> set :interactiveList.entrySet()){
                if (set.getValue().getPid()==remove.get(i)){
                sleepList.put(remove.get(i),set.getValue());
                interactiveList.remove(set.getKey());
                }
            }
            for (Map.Entry<Integer, KernelandProcess> set :backgroundList.entrySet()){
                if (set.getValue().getPid()==remove.get(i)){
                sleepList.put(remove.get(i),set.getValue());
                backgroundList.remove(set.getKey());
                }
            }
        }

        for(Map.Entry<Integer, KernelandProcess> set :sleepList.entrySet()){
            
            if(sleeptime.containsKey(set.getKey())){
                sleeptime.put(set.getKey(),sleeptime.get(set.getKey())-timeused);
            }
            else{
                sleeptime.put(set.getKey(), set.getValue().getSleep()-timeused);
            }

            for(Map.Entry<Integer, Integer> sett :sleeptime.entrySet()){
            if(sett.getValue()<=0){
                if(whichList.get(sett.getKey())==1){
                    realTimeList.put(realTimeList.size()+1, sleepList.get(sett.getKey()));
                }
                
                if(whichList.get(sett.getKey())==2){
                    interactiveList.put(interactiveList.size()+1, sleepList.get(sett.getKey()));
                }
                
                
                if(whichList.get(sett.getKey())==3){
                    backgroundList.put(backgroundList.size()+1, sleepList.get(sett.getKey()));
                }
                sleepList.remove(sett.getKey());
                sleeptime.remove(sett.getKey());
                for (int i = 0; i < remove.size(); i++){
                    if(sett.getKey()==remove.get(i)){
                        remove.remove(i);
                    }
                }
            }
            }
        }
    }



    @Override
    public void run() {
        while(1>0){
            timeused=0;
            int randm=0;
            Random rn = new Random();
            randm = rn.nextInt(10) + 1;
            if(randm>=5&&randm<=10){
                if(realTimeList.isEmpty()){
                    if(interactiveList.isEmpty()){
                        upBackgroundtime();
                    }
                    else{upInteractivetime();}
                }
                else{
                    upRealtime();}
            }
            
            if(randm>=2&&randm<=4){
              
                if(interactiveList.isEmpty()){
                    upBackgroundtime();
                }
                else{
                    upInteractivetime();
                }
            }

            if(randm==1){

                upBackgroundtime();
            }

            Sleep();
            
        
        }
    }
    
 

    @Override
    public int CreateProcess(UserlandProcess myNewProcess, PriorityEnum.Level eLevel) {
        pid++;
        KernelandProcess myKernal = new KernelandProcess();
        myKernal.setPid(pid);
        myKernal.setUserlandProcess(myNewProcess);

        if(eLevel==PriorityEnum.Level.REALTIME){
            whichList.put(pid,1);
            downgrade.put(pid,0);
            realTimeInsert++;
            realTimeList.put(realTimeInsert, myKernal);
        }
        else if(eLevel==PriorityEnum.Level.INTERACTIVE){
            whichList.put(pid,2);
            downgrade.put(pid,0);
            interactiveInsert++;
            interactiveList.put(interactiveInsert, myKernal);
        }
        else if(eLevel==PriorityEnum.Level.BACKGROUND){
            whichList.put(pid,3);
            downgrade.put(pid,0);
            backgroundInsert++;
            backgroundList.put(backgroundInsert, myKernal);
        }
        return pid;
    }
    


    public void upRealtime(){
       
        realtimetrack++;
        if(realTimeList.containsKey(realtimetrack)){
            current=realTimeList.get(realtimetrack);
            if(current.getPid()==lastpid){
                downgrade.put(lastpid, downgrade.get(lastpid)+1);
            }
            else{
                for (Map.Entry<Integer, Integer> set :downgrade.entrySet()){
                    downgrade.put(set.getKey(), 0);
                }
            }
            timeused=current.process.run().getMillisecondsUsed()+timeused;
            lastpid=current.getPid();
            if(current.getSleep()==0){
            }
            else{
               remove.add(current.getPid());
            }

            
        }
    
        if(realtimetrack==realTimeList.size()){
            realtimetrack=0;
        }
    }



    public void upInteractivetime(){
        interactivetrack++;
        if(interactiveList.containsKey(interactivetrack)){
            current=interactiveList.get(interactivetrack);
            if(current.getPid()==lastpid){
                downgrade.put(lastpid, downgrade.get(lastpid)+1);
            }
            else{
                for (Map.Entry<Integer, Integer> set :downgrade.entrySet()){
                    downgrade.put(set.getKey(), 0);
                }
            }
            timeused=current.process.run().getMillisecondsUsed()+timeused;
            lastpid=current.getPid();
            if(current.getSleep()==0){
            }
            else{
                remove.add(current.getPid());
            }

            
        }
    
        if(interactivetrack==interactiveList.size()){
            interactivetrack=0;
        }
    }


    
    public void upBackgroundtime(){
        backgroundtrack++;
        if(backgroundList.containsKey( backgroundtrack)){
            current=backgroundList.get( backgroundtrack);
            timeused=current.process.run().getMillisecondsUsed()+timeused;
            lastpid=current.getPid();

            if(current.getSleep()==0){
            }
            else{
                remove.add(current.getPid());
            }
        }
    
        if( backgroundtrack==backgroundList.size()){
            backgroundtrack=0;
        }

    }

    KernelandProcess temp = new KernelandProcess();

    public void downgrade(){

        for (Map.Entry<Integer, Integer> set :downgrade.entrySet()){
            //if pid = 5 times in a row
            if(set.getValue()==5){
                int index =0;
                int bool=0;
                downgrade.put(set.getKey(), 0);
                System.out.println("A5"+realTimeList);
                for (Map.Entry<Integer, KernelandProcess> sett :realTimeList.entrySet()){
                   if(sett.getValue().getPid()==set.getKey()){
                    bool=1;
                    index=sett.getKey();
                    whichList.put(set.getKey(), whichList.get(set.getKey())-1);
                   }
                 }
                 System.out.println("B5"+realTimeList);
                 
                 System.out.println("A4"+interactiveList);
                 for (Map.Entry<Integer, KernelandProcess> sett :interactiveList.entrySet()){
                    if(sett.getValue().getPid()==set.getKey()){
                     bool=2;
                     index=sett.getKey();
                     whichList.put(set.getKey(),  whichList.get(set.getKey())-1);
                    }
                  }
                  System.out.println("B4"+interactiveList);
                  if(bool==1){
                    interactiveList.put(interactiveList.size()+1, realTimeList.get(index));
                    realTimeList.remove(index);
                    bool=0;
                 }
                  if(bool==2){
                    backgroundList.put(backgroundList.size()+1, realTimeList.get(index));
                     interactiveList.remove(index);
                    bool=0;
                 }
                 System.out.println("C4"+interactiveList);
                 System.out.println("C5"+realTimeList);
            }
        }
    }










}
