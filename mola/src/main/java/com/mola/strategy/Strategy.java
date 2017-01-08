package com.mola.strategy;

import com.mola.charts.BaseChart;
import com.mola.services.ticker.Tick;

/**
 * Created by bilgi on 3/24/15.
 */
public interface Strategy {
    public Strategy doFilter(Tick tick, BaseChart chart);

}
