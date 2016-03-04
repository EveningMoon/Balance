package com.emoon.balance.Model;

import org.parceler.Parcel;

import io.realm.CostRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.Required;

@Parcel(implementations = {CostRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Cost.class})
public class Cost extends RealmObject {

    @Required
    private String id;

    private int pointsEarnPer;
    private int unitCost;
    private String unitType;

    public Cost(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPointsEarnPer() {
        return pointsEarnPer;
    }

    public void setPointsEarnPer(int pointsEarnPer) {
        this.pointsEarnPer = pointsEarnPer;
    }

    public int getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(int unitCost) {
        this.unitCost = unitCost;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }
}
