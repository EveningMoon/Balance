package com.emoon.balance.Model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EarnBurn extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String type;
    private String icon;
    private String unit;

    private RealmList<Cost> costList;

    public EarnBurn(){
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public RealmList<Cost> getCostList() {
        return costList;
    }

    public void setCostList(RealmList<Cost> costList) {
        this.costList = costList;
    }
}
