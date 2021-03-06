package com.emoon.balance.Model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Transaction extends RealmObject{

    @PrimaryKey
    private String id;
    private Date date;
    private EarnBurn earnBurn;
    private int unitCost;
    private String costType;


    public Transaction(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EarnBurn getEarnBurn() {
        return earnBurn;
    }

    public void setEarnBurn(EarnBurn earnBurn) {
        this.earnBurn = earnBurn;
    }

    public int getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(int unitCost) {
        this.unitCost = unitCost;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }
}
