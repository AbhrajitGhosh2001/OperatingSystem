


public class Interactive extends UserlandProcess{
    int sleep;

    public RunResult run() {
        System.out.println("Interactive");
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
