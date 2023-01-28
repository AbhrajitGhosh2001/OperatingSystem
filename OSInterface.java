public interface OSInterface extends ProcessInterface, Device, MemoryInterface, Mutex {
    int CreateProcess(UserlandProcess myNewProcess,PriorityEnum.Level eLevel );
    boolean DeleteProcess(int processId);
    void Sleep();
    void run();
}

