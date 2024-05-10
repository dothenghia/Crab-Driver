package com.example.crab_driver.Object;

import java.io.Serializable;

public class VehicleType implements Serializable {
    private String ID;
    private String name;
    private int capacity;
    private float fee_1;
    private float fee_2;
    private float fee_3;
    public VehicleType() {

    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getFee_1() {
        return fee_1;
    }

    public void setFee_1(float fee_1) {
        this.fee_1 = fee_1;
    }

    public float getFee_2() {
        return fee_2;
    }

    public void setFee_2(float fee_2) {
        this.fee_2 = fee_2;
    }

    public float getFee_3() {
        return fee_3;
    }

    public void setFee_3(float fee_3) {
        this.fee_3 = fee_3;
    }
}
