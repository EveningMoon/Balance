package com.emoon.balance.Model;

import org.parceler.Parcel;

import io.realm.EarnBurnRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {EarnBurnRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {EarnBurn.class})
public class EarnBurn extends RealmObject {
    @PrimaryKey
    private String id;

    private String name;
    private String type;
    private int icon;
    private float cost;
    private String unit;

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

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
