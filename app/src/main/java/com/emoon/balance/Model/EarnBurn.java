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
	private float cost;
    private String type;

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
}
