package com.mola.model;

import com.mola.charts.BaseChart;
import com.mola.managers.TradeManager;
import com.mola.services.ChartService;
import com.mola.services.ticker.Tick;
import com.mola.services.ticker.TickerService;
import com.mola.strategy.TradeStrategy;
import com.mola.trade.Trade;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by bilgi on 3/14/15.
 */
public abstract class Pipeline {

    @Autowired
    TickerService tickerService;

    @Autowired
    TradeManager tradeManager;

    @Autowired
    ChartService chartService;

    //TODO trade strategies should be dynamic and persisted
    @Resource(name="tradeStrategies")
    List<TradeStrategy> tradeStrategies;

    public List<TradeStrategy> doFilter(Tick tick1, BaseChart tick) {
        return null;
    }

    public List<TradeStrategy> getTradeStrategies() {
        return tradeStrategies;
    }

    public void setTradeStrategies(List<TradeStrategy> tradeStrategies) {
        this.tradeStrategies = tradeStrategies;
    }
}
