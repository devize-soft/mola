package com.mola.model;

import com.mola.charts.BaseChart;
import com.mola.managers.TradeManager;
import com.mola.services.ticker.Tick;
import com.mola.strategy.TradeStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by bilgi on 3/14/15.
 */
public class DefaultPipeline extends Pipeline {

    @Autowired
    TradeManager tradeManager;

    @Override
    public List<TradeStrategy> doFilter(Tick tick, BaseChart baseChart) {
        tradeManager.checkOrUpdateExistingTrades(tick, baseChart);
        // TODO Iterate through trade strategies.
        List<TradeStrategy> filterResults = new ArrayList<>();
        for (TradeStrategy strategy : tradeStrategies) {
            filterResults.add(strategy.doFilter(tick, baseChart));
        }

        // TODO perhaps consider other types of filters
        // TODO for example news event filters should weigh into TA filters
        // TODO Likewise Dual Momentum timeframe could be considered a filter.
        for(TradeStrategy strategy: tradeStrategies){
            Set<AbstractModel> directionalModels = strategy.getDirectionalModels();
        }

        return filterResults;
    }
}
