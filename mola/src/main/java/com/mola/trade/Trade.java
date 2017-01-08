package com.mola.trade;

import com.mola.charts.BaseChart;
import com.mola.services.ticker.Tick;
import com.oanda.fxtrade.api.StopLossOrder;
import com.oanda.fxtrade.api.TakeProfitOrder;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by bilgi on 3/21/15.
 */
@Entity
@Table(name = "trade")
public class Trade {

    public Trade(){

    }

    @Id
    @GeneratedValue
    Long id;

    // long trade = true, short trade = false
    @Column
    boolean position;

    @Column
    boolean active;

    @Column
    String provider;

    @Column
    double entry;

    @Column
    double stoploss;

    @Column
    double takeProfit;

    @Column
    String granularity;

    @Column
    String pair;

    @Column
    Date tickTime;

    @Column
    long orderId;

    public Trade(Tick tick, int transactionNumber, double price, TakeProfitOrder takeProfit, BaseChart chart, StopLossOrder stopLoss, String provider, boolean position) {
        this.orderId = transactionNumber;
        this.entry = price;
        this.takeProfit = takeProfit == null? 0.0: takeProfit.getPrice();
        this.pair = chart.getPair().getNameFormatted();
        this.stoploss = stopLoss.getPrice();
        this.active = true;
        this.granularity = chart.getGranularity().name();
        this.provider = provider;
        this.position = position;
        this.tickTime = tick.getTime();
    }

    public boolean isPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getEntry() {
        return entry;
    }

    public void setEntry(double entry) {
        this.entry = entry;
    }

    public double getStoploss() {
        return stoploss;
    }

    public void setStoploss(double stoploss) {
        this.stoploss = stoploss;
    }

    public double getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(double takeProfit) {
        this.takeProfit = takeProfit;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public Date getTickTime() {
        return tickTime;
    }

    public void setTickTime(Date tickTime) {
        this.tickTime = tickTime;
    }
}
