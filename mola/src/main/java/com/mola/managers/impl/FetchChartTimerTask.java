package com.mola.managers.impl;

import com.mola.charts.BaseChart;
import com.mola.charts.ChartType;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.charts.util.ChartFetcher;
import com.mola.model.AbstractModel;
import com.mola.oanda.OandaCandleStickChart;
import com.mola.services.ticker.Tick;
import com.mola.util.BeanUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class FetchChartTimerTask implements Callable {

    private String url;
    private String oandaApiToken;
    private Pair pair;
    private Granularity granularity;
    private ChartType[] chartTypes;
    private Tick tick;
    private List<AbstractModel> models;
    boolean building = false;
    private Date lastUpdate;

    public FetchChartTimerTask() {

    }

    public FetchChartTimerTask(Pair p, Granularity g, String url, String oandaApiToken) {
        pair = p;
        granularity = g;
        this.url = url;
        this.oandaApiToken = oandaApiToken;
    }

    public FetchChartTimerTask(Pair p, Granularity g, ChartType... charts) {
        pair = p;
        granularity = g;
        chartTypes = charts;
    }

    public BaseChart fetchChartRest(String instrument, String granularity,
                                    Date start, Date end) {
        ChartFetcher fetcher = BeanUtils.createObjectFromId(ChartFetcher.class, Pair.valueOf(instrument), Granularity.valueOf(granularity));
        BaseChart baseChart = new OandaCandleStickChart();
        fetcher.fetchChart(url, oandaApiToken, baseChart, start, end, 100);
        return baseChart;
    }

    public List<AbstractModel> getModels() {
        return models;
    }

    public void setModels(List<AbstractModel> models) {
        this.models = models;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public Pair getPair() {
        return pair;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    public Granularity getGranularity() {
        return granularity;
    }

    public void setGranularity(Granularity granularity) {
        this.granularity = granularity;
    }

    public ChartType[] getChartTypes() {
        return chartTypes;
    }

    public void setChartTypes(ChartType[] chartTypes) {
        this.chartTypes = chartTypes;
    }

    public Tick getTick() {
        return tick;
    }

    public void setTick(Tick tick) {
        this.tick = tick;
    }


    @Override
    public BaseChart call() throws Exception {

        Calendar today = Calendar.getInstance();
        Date start = new Date(Granularity.getMaxStartDate(granularity,
                today.getTime()));
        BaseChart baseChart = fetchChartRest(pair.name(), granularity.name(), start,
                today.getTime());

        if(baseChart !=null) {
            baseChart.setPair(pair);
            baseChart.setGranularity(granularity);
        }
        return baseChart;

    }

    private boolean isUpdatable() {
        if (lastUpdate != null) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - granularity.getValue()) >= lastUpdate.getTime()) {
                lastUpdate = new Date(System.currentTimeMillis());
                return true;
            } else {
                return false;
            }
        }

        if (lastUpdate == null) {
            lastUpdate = new Date(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
