package com.mola.oanda.services.impl;

import com.mola.charts.BaseChart;
import com.mola.charts.ChartType;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.managers.TradeManager;
import com.mola.managers.impl.FetchChartTimerTask;
import com.mola.model.AbstractModel;
import com.mola.model.Pipeline;
import com.mola.services.ChartService;
import com.mola.services.ticker.Tick;
import com.mola.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OandaChartServiceImpl implements ChartService {

    @Autowired
    Pipeline pipeline;

    @Autowired
    TradeManager tradeManager;

    @Autowired
    String oandaRatesUrl;

    @Autowired
    String oandaApiToken;

    Set<FetchChartTimerTask> chartFetchers;
    ExecutorService chartFetcherPool;
    Set<Future<BaseChart>> fetcherFutures = new LinkedHashSet<>();

    @PostConstruct
    public void initialize() {
        chartFetchers = new LinkedHashSet<>();
        chartFetcherPool = Executors.newFixedThreadPool(10);
        initializeFetchers();
    }

    @Override
    public List<BaseChart> fetchChartsRest(String chartType,
                                           String granularity, Date start, Date end) {
        return null;
    }

    @Override
    public BaseChart updateChart(Tick tick) {
        return null;
    }

    @Override
    public List<AbstractModel> fetchAndUpdateChart(Pair pair, Granularity granularity, ChartType[] chartTypes) {
        return null;
    }

    @Override
    public BaseChart fetchChartRest(String instrument, String granularity,
                                    Date start, Date end) {
        return null;
    }

    @Override
    public void updateCharts(Tick tick) {
        for (FetchChartTimerTask task : chartFetchers) {
            if (isUpdatable(task)) {
                runFetcher(task, tick);
                Iterator<Future<BaseChart>> iterator = fetcherFutures.iterator();
                while(iterator.hasNext()){
                    Future<BaseChart> future = iterator.next();
                    if(future.isDone()){
                        iterator.remove();
                        continue;
                    }
                    try {
                        BaseChart baseChart = future.get();
                        if (baseChart != null) {
                            pipeline.doFilter(tick, baseChart);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void unpause() {
        fetcherFutures = new LinkedHashSet<>();
    }

    private boolean isUpdatable(FetchChartTimerTask task) {
        if (task.getLastUpdate() != null) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - (task.getGranularity().getValue())) >= task.getLastUpdate().getTime()) {
                task.setLastUpdate(new Date(System.currentTimeMillis()));
                return true;
            } else {
                return false;
            }
        }

        if (task.getLastUpdate() == null) {
            task.setLastUpdate(new Date(System.currentTimeMillis()));
            return true;
        }
        return false;
    }

    public void runFetcher(FetchChartTimerTask fetchChartTask, Tick tick) {
        if (fetchChartTask.getTick() == null) {
            fetchChartTask.setTick(tick);
        } else {

        }
        Future<BaseChart> future = chartFetcherPool.submit(fetchChartTask);
        fetcherFutures.add(future);
    }

    private void initializeFetchers() {
        for (Granularity g : Granularity.values()) {
            for (Pair p : Pair.values()) {
                chartFetchers.add(BeanUtils.<FetchChartTimerTask>createObjectFromId(FetchChartTimerTask.class, p, g,oandaRatesUrl, oandaApiToken));
            }
        }
    }
}
