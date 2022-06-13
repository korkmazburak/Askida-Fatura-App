package com.example.askidafatura.models;

public class Bills {
    public String tc;
    public String no;
    public String city;
    public String type;
    public String cUid;
    public int cost;
    public int askingPay;
    public int payState;
    public boolean activeState;

    public Bills(String tc,String no,String city,String type,String cUid,int cost,int askingPay,int payState,boolean activeState) {
        this.tc = tc;
        this.no = no;
        this.city = city;
        this.type = type;
        this.cUid = cUid;
        this.cost = cost;
        this.askingPay = askingPay;
        this.payState = payState;
        this.activeState = activeState;
    }
    public Bills() {}
}
