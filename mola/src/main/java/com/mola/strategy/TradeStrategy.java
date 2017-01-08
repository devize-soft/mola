package com.mola.strategy;

import com.mola.charts.BaseChart;
import com.mola.charts.Granularity;
import com.mola.managers.TradeManager;
import com.mola.model.AbstractModel;
import com.mola.model.DirectionalModel;
import com.mola.model.filters.AbstractFilter;
import com.mola.services.MarketDirectionService;
import com.mola.services.ticker.Tick;
import com.mola.util.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bilgi on 3/24/15.
 */
@Entity(name = "strategies")
public class TradeStrategy implements Strategy{

    @Autowired
    TradeManager tradeManager;

    @Autowired
    MarketDirectionService marketDirectionService;

    // TODO change this to dynamically set filters via gui
    @Resource(name = "chartFilters")
    Set<AbstractFilter> filters;
    Set<AbstractModel> directionalModels;

    List<AbstractModel> models;

    @Override
    public TradeStrategy doFilter(Tick tick, BaseChart chart) {
        models = new ArrayList<>();
        directionalModels = new LinkedHashSet<>();
        for(AbstractFilter filter:filters){
            AbstractModel model = filter.doFilter(tick, chart);
            if(model instanceof DirectionalModel){
                directionalModels.add(model);
            }else{
                models.add(model);
            }
        }
        analyzeModels(tick,chart);
        return this;
    }

    private void analyzeModels(Tick tick, BaseChart chart) {
        double predClose = Double.MIN_VALUE;

        List<Double> predictions = new ArrayList<>();
        for(AbstractModel model: models){
            predictions.addAll(model.getPredictedClose());
        }
        double stdDev = MathUtils.stdDev(predictions.toArray(new Double[predictions.size()]));
        double mean = MathUtils.mean((Double[])predictions.toArray(new Double[predictions.size()]));

        Boolean[] decisions = new Boolean[directionalModels.size()+1];
        if(mean+stdDev > tick.getAsk()){
            decisions[0] = true;
        }else if(mean+stdDev < tick.getAsk()){
            decisions[0] = false;
        }else{
            decisions[0] = null;
        }

        int count=1;
        boolean enterTrade = false;
        for(AbstractModel model: directionalModels){
            if(model.isEnterTrade() != null){
                enterTrade = true;
                decisions[count] = model.isEnterTrade();
                ++count;
            }
        }

        int longCount=0;
        int shortCount=0;
        for(int i=0; i < decisions.length; ++i){
            if(decisions[i] == null){
                continue;
            }

            if(decisions[i] == true){
                ++longCount;
            }else if(decisions[i] == false){
                ++shortCount;
            }
        }

        boolean marketDirection = marketDirectionService.getMarketDirection(chart.getPair(), Granularity.W);
        if(enterTrade && tradeManager.countOpenTrades() < 8) {
            if (marketDirection) {
                // go long
                tradeManager.executeLongOrder(tick, chart);
            } else if (shortCount > longCount) {
                // go short
                tradeManager.executeShortOrder(tick, chart);
            }
        }
    }

    public Set<AbstractFilter> getFilters() {
        return filters;
    }

    public void setFilters(Set<AbstractFilter> filters) {
        this.filters = filters;
    }

    public List<AbstractModel> getModels() {
        return models;
    }

    public void setModels(List<AbstractModel> models) {
        this.models = models;
    }

    public Set<AbstractModel> getDirectionalModels() {
        return directionalModels;
    }

    public void setDirectionalModels(Set<AbstractModel> directionalModels) {
        this.directionalModels = directionalModels;
    }
}
