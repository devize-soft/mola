package com.mola.model.filters;

import com.mola.charts.BaseChart;
import com.mola.model.AbstractModel;
import com.mola.model.builder.ModelBuilder;
import com.mola.services.ticker.Tick;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by bilgi on 3/14/15.
 */
public class WekaFilter extends AbstractFilter {

    @Autowired
    ModelBuilder modelBuilder;

    @Override
    public Object doFilter(Tick tick) {
        return super.doFilter(tick);
    }

    @Override
    public AbstractModel doFilter(Tick tick,BaseChart chart) {
        List<AbstractModel> abstractModels = modelBuilder.buildModel(chart);
        return modelBuilder.getHighestScoringModel(abstractModels);
    }
}
