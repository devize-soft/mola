package com.mola.services;

import com.mola.charts.BaseChart;
import com.mola.charts.ChartType;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.model.AbstractModel;
import com.mola.services.ticker.Tick;

import java.util.Date;
import java.util.List;

public interface ChartService {

    public BaseChart updateChart(Tick tick);

    public List<AbstractModel> fetchAndUpdateChart(Pair pair,
                                                   Granularity granularity, ChartType[] chartTypes);

    public List<BaseChart> fetchChartsRest(String chartType,
                                           String granularity, Date start, Date end);

    public BaseChart fetchChartRest(String instrument, String granularity,
                                    Date start, Date end);

    public void updateCharts(Tick tick);

    public void pause();
    public void unpause();
}