package com.appdynamics.cloud.connectors.verizon.types;

public class VdiskMount extends Entity {

    private int index;
    private int diskOps;
    private Vdisk vdisk;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDiskOps() {
        return diskOps;
    }

    public void setDiskOps(int diskOps) {
        this.diskOps = diskOps;
    }

    public Vdisk getVdisk() {
        return vdisk;
    }

    public void setVdisk(Vdisk vdisk) {
        this.vdisk = vdisk;
    }
}
