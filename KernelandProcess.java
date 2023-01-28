import java.util.ArrayList;
import java.util.Arrays;


public class KernelandProcess {
    VirtualToPhysicalMapping virtualMap[]  = new VirtualToPhysicalMapping [1024]; 

    public KernelandProcess(){
        Arrays.fill(virtualMap, -1);
    }

    public VirtualToPhysicalMapping[] getVirtualMap() {
        return virtualMap;
    }

    public ArrayList<Integer> getSpots(){
        ArrayList<Integer> arr = new ArrayList<Integer>();
        for(int x=0;x<virtualMap.length;x++){
            if(virtualMap[x]!=null){
                arr.add(x);
            }
        }
        return arr;
    }

    public void setSpots(int from,int to,int last){
        VirtualToPhysicalMapping filledMapping = new VirtualToPhysicalMapping();
        filledMapping.setPhyiscal(-1);
        filledMapping.setDisk(-1);
        for(int x=from;x<to;x++){
            virtualMap[x]=filledMapping;
        }
        filledMapping.setPhyiscal(last);
        virtualMap[to+1]=filledMapping;
    }

    public void setSpot(int spot,int phyiscal,int disk,boolean dirty){
        VirtualToPhysicalMapping temp = new VirtualToPhysicalMapping();
        temp.setDisk(disk);
        temp.setPhyiscal(phyiscal);
        temp.setDirty(dirty);
        virtualMap[spot]=temp;
    }


    int pid;
    UserlandProcess process;
    ArrayList<Integer> vfsids = new ArrayList<Integer>();
   
    public void addDevices(int id){
        vfsids.add(id);
    }

    public ArrayList<Integer> getVfsids() {
        return vfsids;
    }

    public void setVfsids(ArrayList<Integer> vfsids) {
        this.vfsids = vfsids;
    }


    public int getSleep(){
       return process.getSleep();
    }
    
    public void setSleep(int x){
        process.setSleep(x);
    }

    public int getPid() {
        return pid;
    }
    public UserlandProcess getProcess() {
        return process;
    }

    public void setPid(int pid){
        this.pid =pid;
    }

    public void setUserlandProcess(UserlandProcess process){
        this.process =process;
    }

    
}
