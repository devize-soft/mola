package com.mola.model.filters;

import com.mola.charts.BaseChart;
import com.mola.model.AbstractModel;
import com.mola.services.ticker.Tick;

/**
 * Created by bilgi on 3/14/15.
 */
public abstract class AbstractFilter {
    int weight = 0;

    double score = 0;

    boolean direction;

    public Object doFilter() {
        return null;
    }

    public Object doFilter(Tick tick) {
        return null;
    }

    public AbstractModel doFilter(Tick tick, BaseChart chart) {
        return null;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }
}
