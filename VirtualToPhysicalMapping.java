public class VirtualToPhysicalMapping {
    int phyiscal;
    int disk;
    boolean isDirty;

    public void set(){
        phyiscal=-1;
        disk=-1;
        isDirty=false;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    public void setPhyiscal(int phyiscal) {
        this.phyiscal = phyiscal;
    }

    public int getDisk() {
        return disk;
    }

    public int getPhyiscal() {
        return phyiscal;
    }

    public boolean getDirty() {
        return isDirty;
    }

}