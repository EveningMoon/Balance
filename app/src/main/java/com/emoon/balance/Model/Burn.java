package com.emoon.balance.Model;

import org.parceler.Parcel;

import java.util.Date;

import io.realm.BurnRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {BurnRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Burn.class})
public class Burn extends RealmObject {
    @PrimaryKey
    private String id;

    private String name;
    private Date date;

	private float cost;

    public Burn(){
        
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
}
