package com.appdynamics.cloud.connectors.verizon.types;

import java.util.List;

public class Group<T> {
    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
