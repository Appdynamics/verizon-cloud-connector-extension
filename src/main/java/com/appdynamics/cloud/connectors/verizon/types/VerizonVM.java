package com.appdynamics.cloud.connectors.verizon.types;

public class VerizonVM extends Entity {
    private String name;
    private String description;
    private int processorCores;
    private int processorSpeed;
    private int memory;
    private String os;
    private String arch;
    private String status;
    private Group<Vnic> vnics;
    private Group<VdiskMount> vdiskMounts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProcessorCores() {
        return processorCores;
    }

    public void setProcessorCores(int processorCores) {
        this.processorCores = processorCores;
    }

    public int getProcessorSpeed() {
        return processorSpeed;
    }

    public void setProcessorSpeed(int processorSpeed) {
        this.processorSpeed = processorSpeed;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Group<Vnic> getVnics() {
        return vnics;
    }

    public void setVnics(Group<Vnic> vnics) {
        this.vnics = vnics;
    }

    public Group<VdiskMount> getVdiskMounts() {
        return vdiskMounts;
    }

    public void setVdiskMounts(Group<VdiskMount> vdiskMounts) {
        this.vdiskMounts = vdiskMounts;
    }
}