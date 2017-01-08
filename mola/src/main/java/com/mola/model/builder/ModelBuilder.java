package com.mola.model.builder;

import com.mola.charts.BaseChart;
import com.mola.model.AbstractModel;

import java.util.List;

public interface ModelBuilder {
    public List<AbstractModel> buildModel(BaseChart chart);

    public BaseChart addAuxilaryChart(BaseChart chart);

    public AbstractModel getHighestScoringModel(List<AbstractModel> abstractModels);
}