package com.mola.charts.oscillators;

import com.mola.charts.BaseChart;
import com.mola.charts.ChartType;
import com.mola.instruments.Quote;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.commons.lang.ArrayUtils;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

//@Entity
public class StochasticOscillator extends BaseChart {

    Logger logger = Logger
            .getLogger(StochasticOscillator.class.getSimpleName());

    private Double[] outSlowK;
    private Double[] outSlowD;
    private int fastK;
    private int slowK;
    private int slowD;

    public StochasticOscillator(BaseChart base, int fk, int sk, int sd) {
        this.baseChart = base;
        fastK = fk > 0 ? fk : Integer.MIN_VALUE;
        slowK = sk > 0 ? sk : Integer.MIN_VALUE;
        slowD = sd > 0 ? sd : Integer.MIN_VALUE;
        setChartType(ChartType.stoch);
    }

    public StochasticOscillator(Object[] quotes) {
        this.quotes = quotes;
    }

    public StochasticOscillator(Object[] quotes, int lookback) {
        this.quotes = quotes;
    }

    @SuppressWarnings("unchecked")
    public void render() {
        Map.Entry<Date, Quote<String, Double>>[] rangedMap = (Map.Entry<Date, Quote<String, Double>>[]) new Map.Entry[baseChart
                .getPrice().size()];
        baseChart.getPrice().entrySet().toArray(rangedMap);
        double[] closePrice = new double[rangedMap.length];
        double[] highPrice = new double[rangedMap.length];
        double[] lowPrice = new double[rangedMap.length];

        double[] tempOutSlowK = new double[rangedMap.length];
        double[] tempOutSlowD = new double[rangedMap.length];
        outSlowK = new Double[rangedMap.length];
        outSlowD = new Double[rangedMap.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        for (int i = 0; i < rangedMap.length; ++i) {
            closePrice[i] = (rangedMap[i].getValue().get("closeAsk"))
                    .doubleValue();
            highPrice[i] = (rangedMap[i].getValue().get("highAsk"))
                    .doubleValue();
            lowPrice[i] = (rangedMap[i].getValue().get("lowAsk")).doubleValue();
        }

        Core core = new Core();

        RetCode retCode = core.stoch(0, rangedMap.length - 1, highPrice,
                lowPrice, closePrice, fastK, slowK, MAType.Sma, slowD,
                MAType.Sma, begin, length, tempOutSlowK, tempOutSlowD);

        System.arraycopy(ArrayUtils.toObject(tempOutSlowK), 0, outSlowK,
                begin.value, outSlowK.length - (begin.value));
        System.arraycopy(ArrayUtils.toObject(tempOutSlowD), 0, outSlowD,
                begin.value, outSlowD.length - (begin.value));

        putRenderedData(this.getClass().getSimpleName() + ".slowK", outSlowK);
        putRenderedData(this.getClass().getSimpleName() + ".slowD", outSlowD);

        String[] arff = new String[3];
        arff[0] = String.valueOf(fastK);
        arff[1] = String.valueOf(slowK);
        arff[2] = String.valueOf(slowD);
        putRenderedData("arff", arff);

        setLength(length.value);
        setOffset(begin.value);
//        logger.info(this.getClass().getName() + " render: " + retCode.name());
    }

    // @OneToMany(targetEntity = BaseChart.class, fetch = FetchType.EAGER)
    public Double[] getOutSlowK() {
        return outSlowK;
    }

    public void setOutSlowK(Double[] outSlowK) {
        this.outSlowK = outSlowK;
    }

    // @OneToMany(targetEntity = BaseChart.class, fetch = FetchType.EAGER)
    public Double[] getOutSlowD() {
        return outSlowD;
    }

    public void setOutSlowD(Double[] outSlowD) {
        this.outSlowD = outSlowD;
    }

    public int getPeriods() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setPeriods(int periods) {
        // TODO Auto-generated method stub

    }

    public int getFastK() {
        return fastK;
    }

    public void setFastK(int fastK) {
        this.fastK = fastK;
    }

    public int getSlowK() {
        return slowK;
    }

    public void setSlowK(int slowK) {
        this.slowK = slowK;
    }

    public int getSlowD() {
        return slowD;
    }

    public void setSlowD(int slowD) {
        this.slowD = slowD;
    }
}
