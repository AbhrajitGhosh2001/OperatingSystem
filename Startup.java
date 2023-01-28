

public class Startup {
    public static void main (String[] args){
     OS os = new OS();
     byte b[];
     Realtime real = new Realtime();
     real.setSleep(0);
     os.CreateProcess(real, PriorityEnum.Level.REALTIME);
     os.Open("random 100");
     b =os.Read(0, 1022);
     for(int x=0;x<b.length;x++){
        System.out.println(b[x]);
     }
     os.sbrk(1023);

     for(int x=0;x<b.length-1;x++){
        try {
            os.WriteMemory(x, b[x]);
        } catch (RescheduleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     }
        try {
            System.out.println(os.ReadMemory(0)); ;
        } catch (RescheduleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
