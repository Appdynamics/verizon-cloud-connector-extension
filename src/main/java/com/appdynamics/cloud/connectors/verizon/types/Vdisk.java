package com.appdynamics.cloud.connectors.verizon.types;

public class Vdisk extends Entity {
    private String name;
    private String description;
    private VdiskTemplate fromVdiskTemplate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
