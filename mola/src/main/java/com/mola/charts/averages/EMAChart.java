package com.mola.charts.averages;

import com.mola.charts.AbstractAuxillaryChart;
import com.mola.charts.BaseChart;
import com.mola.charts.ChartType;
import com.mola.instruments.Quote;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.commons.lang.ArrayUtils;

import java.util.Date;
import java.util.Map;

public class EMAChart extends BaseChart implements AbstractAuxillaryChart {

    private Double[] ema;
    private int periods;

    public EMAChart(BaseChart base, int p) {
        this.baseChart = base;
        periods = p > 0 ? p : Integer.MIN_VALUE;
        setChartType(ChartType.ema);
    }

    @SuppressWarnings("unchecked")
    public void render() {

        Map.Entry<Date, Quote<String, Double>>[] rangedMap = (Map.Entry<Date, Quote<String, Double>>[]) new Map.Entry[baseChart
                .getPrice().size()];
        baseChart.getPrice().entrySet().toArray(rangedMap);
        double[] closePrice = new double[rangedMap.length];
        double[] tempOut = new double[rangedMap.length];
        ema = new Double[rangedMap.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        for (int i = 0; i < rangedMap.length; ++i) {
            closePrice[i] = (rangedMap[i].getValue().get("closeAsk"))
                    .doubleValue();
        }

        Core core = new Core();

        RetCode retCode = core.ema(0, closePrice.length - 1, closePrice,
                periods, begin, length, tempOut);
        System.arraycopy(ArrayUtils.toObject(tempOut), 0, ema,
                begin.value, ema.length - (begin.value));

        putRenderedData(this.getClass().getSimpleName(), ema);
        String[] arff = new String[1];
        arff[0] = String.valueOf(periods);
        putRenderedData("arff", arff);
        setLength(length.value);
        setOffset(begin.value);
//        logger.log(Level.INFO, this.getClass().getName() + " render: "
//                + retCode.name());
    }

    public Double[] getEma() {
        return ema;
    }

    public void setEma(Double[] ema) {
        this.ema = ema;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }
}