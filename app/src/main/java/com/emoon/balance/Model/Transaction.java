package com.emoon.balance.Model;

import org.parceler.Parcel;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.TransactionRealmProxy;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {TransactionRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {Transaction.class})
public class Transaction extends RealmObject{

    @PrimaryKey
    private String id;
    private Date date;
    private EarnBurn earnBurn;

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
}
