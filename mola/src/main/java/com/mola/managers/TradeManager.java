package com.mola.managers;

import com.mola.charts.BaseChart;
import com.mola.services.ticker.Tick;

public interface TradeManager {
    public Object executeLongOrder(Tick tick, BaseChart chart);

    public Object executeShortOrder(Tick tick, BaseChart chart);

    public Object getAccountById(int id);
    public Object getPositions();
    public Object getOpenTrades();
    public Object getPositionForPair(String pair);
    public int countOpenTrades();

    public void checkOrUpdateExistingTrades(Tick tick, BaseChart baseChart);
}