package com.mola.persistence.managers;

import com.mola.charts.ChartType;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.model.AbstractModel;
import com.mola.trade.Trade;

import java.util.List;

public interface PersistenceManager {

    public void saveOrUpdate(Object object);

    public void persistModel(AbstractModel model);

    public Object getChart(Pair pair, Granularity granularity,
                           ChartType[] chartTypes);

    public Object getTrade();

    public List<Trade> getOpenTrades();

}
