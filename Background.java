public class Background extends UserlandProcess{
    int sleep;

    public RunResult run() {
        System.out.println("Background");
        RunResult runReslt = new RunResult();
        return runReslt;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

}