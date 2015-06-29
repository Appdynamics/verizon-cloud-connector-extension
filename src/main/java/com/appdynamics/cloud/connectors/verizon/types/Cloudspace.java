package com.appdynamics.cloud.connectors.verizon.types;


import java.util.List;

public class Cloudspace extends Entity {

    private List<CloudspaceItem> items;

    public List<CloudspaceItem> getItems() {
        return items;
    }

    public void setItems(List<CloudspaceItem> items) {
        this.items = items;
    }
}
