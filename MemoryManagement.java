
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;


public class MemoryManagement implements MemoryInterface{
    ArrayList<int[]> mem = new ArrayList<int[]>();
    BitSet bits = new BitSet(1024);
    int page=1;
    int offset=0; 
    int past=0;
    FakeFileSystem disk = new FakeFileSystem();
    int pagenumtrack=0;
    KernelandProcess pass;
    KernelandProcess victim;

    public void pickVictim(KernelandProcess victim){
        this.victim=victim;
    }

    public MemoryManagement(){
        
        disk.Open("swapfile");
    }

    public void sendCurrent(KernelandProcess current){
        pass=current;
        
    }

    public KernelandProcess getPass(){
        return pass;
    }

    
    public void freeList(int from, int to){
        bits.clear(from,to);
        for(int x=from;x<to;x++){
            mem.remove(x);
        }
    }


    @Override
    public void WriteMemory(int address, byte value) throws RescheduleException {
        ArrayList<Integer> bite = new ArrayList<Integer>();
        bits.set(0,(address/1024));

        int emptyspot=-1;
        int []temper = new int[1024];
        
                for(int x=0;x<1024;x++){
                    if(mem.get(x)==null){
                        temper=mem.get(x);
                        emptyspot=x;
                        x=1025;
                    }
                }
                if(emptyspot>=0){
                    temper[address%1024]=value;
                    mem.add(emptyspot, temper);
                }
            
            //random steal if full cause empty spot not found
                if(emptyspot==-1){
                    bite=victim.getSpots();
                    int randm=0;
                    Random rn = new Random();
                    randm = rn.nextInt(bite.size()) + 1;
                    //pick a page from bite array spots from victim process
                    randm=bite.get(randm);
                    byte[] b= new byte[1024];
                    int [] i=mem.get(randm);
                    for(int x=0;x<1024;x++){
                        b[x]=((byte)i[x]);
                    }

                
                    //write out 
                        disk.Write(pagenumtrack, b);
                        pagenumtrack++;
                        victim.setSpot(randm, address%1024, pagenumtrack, false);
                    if(bits.get(randm)){
                        //clear
                        int [] j=new int[1024];
                        mem.add(randm,j);
                        //write
                        mem.add(randm, i);
                    }
                }
                else{
                    pass.setSpot(address/1024, address%1024, pagenumtrack, true);
                    int []temp = mem.get(address/1024);
                    temp[address%1024]=value;
                    mem.add(address/1024, temp);
                }
                

    }



    
    @Override
    public byte ReadMemory(int address) throws RescheduleException {

        int []temp = mem.get(address/1024);
        Byte byt=(byte) temp[address%1024];
        return byt;
    }

    

//real mapping in priority scheduler this still bit sets
    @Override
    public int sbrk(int amount) {


        int temppast = past;
        if (amount>1024){
                 page=(amount/1024)+page;
                 offset= amount%1024;
            }
           
        else{
                 offset=amount;

            }
        bits.set(0,past+(amount/1024));
        for(int x=0;x<page;x++){
            mem.add(new int[1024]);
        }
        mem.add(new int[offset]);
        past=past+amount;
        return temppast ;
    }
}