package com.cumt.blue;

import java.io.Serializable;

public class BluetoothDevicBean implements Serializable {
    String name;
    String address;
    int bondState;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBondState() {
        return bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }
}
