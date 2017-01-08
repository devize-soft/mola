package com.mola.model;

import java.util.ArrayList;
import java.util.List;

public class AbstractModel {
    private Boolean enterTrade;

    private boolean direction;

    private double score;

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public List<Double> getPredictedClose() {
        return new ArrayList<>();
    }

    public void setPredictedClose(double predictedClose) {

    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public Boolean isEnterTrade() {
        return enterTrade;
    }

    public void setEnterTrade(Boolean enterTrade) {
        this.enterTrade = enterTrade;
    }
}
