package com.mola.weka.models;

import com.mola.charts.BaseChart;
import com.mola.charts.ChartType;
import com.mola.charts.oscillators.StochasticOscillator;
import com.mola.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class WekaArffModel extends AbstractModel {

    @Column
    boolean direction;

    @Lob
    List<Double> predictedClose = new ArrayList<>();

    @Column
    double loss;

    @Column
    double profit;

    @Id
    Long id;

    @Column
    String arff;

    @Column
    String instrument;

    @Column
    String granularity;

    @Column
    String ema;

    @Column
    String sma;

    @Column
    String stoch;

    @Column
    Date start;

    @Column
    Date end;

    @Lob
    Object quotes;

    public WekaArffModel(BaseChart chart) {
        quotes = chart.getQuotes();
        for (BaseChart c : chart.getAuxillaryCharts()) {
            if (c.getChartType() == ChartType.ema) {
                ema = String.valueOf(c.getPeriods());
            }
            if (c.getChartType() == ChartType.sma) {
                sma = String.valueOf(c.getPeriods());
            }
            if (c.getChartType() == ChartType.stoch) {
                stoch = ((StochasticOscillator) c).getFastK() + "," + ((StochasticOscillator) c).getSlowK() + "," + ((StochasticOscillator) c).getSlowD();
            }
        }
    }

    public String getArff() {
        return arff;
    }

    public void setArff(String arff) {
        this.arff = arff;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getEma() {
        return ema;
    }

    public void setEma(String ema) {
        this.ema = ema;
    }

    public String getSma() {
        return sma;
    }

    public void setSma(String sma) {
        this.sma = sma;
    }

    public String getStoch() {
        return stoch;
    }

    public void setStoch(String stoch) {
        this.stoch = stoch;
    }

    public Object getQuotes() {
        return quotes;
    }

    public void setQuotes(Object quotes) {
        this.quotes = quotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public List<Double> getPredictedClose() {
        return predictedClose;
    }

    public void setPredictedClose(double predictedClose) {
        this.predictedClose.add(predictedClose);
    }

    public double getLoss() {
        return loss;
    }

    public void setLoss(double loss) {
        this.loss = loss;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public void setPredictedClose(double[] predictions) {
        for (int i = 0; i < predictions.length; ++i) {
            this.predictedClose.add(predictions[i]);
        }
    }
}