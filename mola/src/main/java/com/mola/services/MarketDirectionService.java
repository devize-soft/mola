package com.mola.services;

import com.mola.charts.Granularity;
import com.mola.charts.Pair;

/**
 * Created by bilgi on 4/8/15.
 */

public interface MarketDirectionService extends ChartService{

    public boolean getMarketDirection(Pair p, Granularity g);
}