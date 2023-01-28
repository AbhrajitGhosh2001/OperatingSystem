    public class GoodbyeWorldProcess extends UserlandProcess{
        int sleep;

        public RunResult run() {
            System.out.println("GoodbyeWorld");
            RunResult runReslt = new RunResult();
            return runReslt;
        }
        
        public void setSleep(int sleep) {
            this.sleep = sleep;
        }

        public int getSleep() {
            return sleep;
        }

    }