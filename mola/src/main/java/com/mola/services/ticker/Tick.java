package com.mola.services.ticker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Tick {

    @Id
    @GeneratedValue
    private long id;

    @Column
    private String instrument;

    @Column
    private Date time;

    @Column
    private double bid;

    @Column
    private double ask;

    public Tick(String instrument, String time, double bid, double ask) {
//        SimpleDateFormat sdf = new SimpleDateFormat(
//                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        try {
            this.time = sdf.parse(time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        this.instrument = instrument;
        this.bid = bid;
        this.ask = ask;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return instrument + " - " + time + " - [ask:" + ask + "] - [bid:" + bid +"]";
    }
}