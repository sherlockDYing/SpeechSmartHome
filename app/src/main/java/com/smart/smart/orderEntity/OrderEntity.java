package com.smart.smart.orderEntity;

//
// Created by dingying on 2019/4/6.
//
public class OrderEntity {
    private String equi;
    private String act;
    private String value;
    private String mode;

    public OrderEntity(String equi,String act,String value,String mode){
        this.equi=equi;
        this.act=act;
        this.value=value;
        this.mode=mode;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public void setEqui(String equi) {
        this.equi = equi;
    }

    public String getMode() {
        return mode;
    }

    public String getValue() {
        return value;
    }

    public String getEqui() {
        return equi;
    }

    public String getAct() {
        return act;
    }
}
