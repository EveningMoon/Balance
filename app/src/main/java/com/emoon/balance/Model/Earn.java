package com.emoon.balance.Model;

import org.parceler.Parcel;

import java.util.Date;

import io.realm.EarnRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {EarnRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Earn.class})
public class Earn extends RealmObject {

    @PrimaryKey
    private String id;
    private String name;
    private Date date;

    private float points;

    public Earn(){

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


    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }
}
