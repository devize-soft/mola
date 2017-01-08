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

public class SMAChart extends BaseChart implements AbstractAuxillaryChart {

    private Double[] smAverage;
    private int periods;

    public SMAChart(BaseChart base, int p) {
        this.baseChart = base;
        periods = p > 0 ? p : Integer.MIN_VALUE;
        setChartType(ChartType.sma);
    }

    @SuppressWarnings("unchecked")
    public void render() {

        Map.Entry<Date, Quote<String, Double>>[] rangedMap = (Map.Entry<Date, Quote<String, Double>>[]) new Map.Entry[baseChart
                .getPrice().size()];
        baseChart.getPrice().entrySet().toArray(rangedMap);
        double[] closePrice = new double[rangedMap.length];
        double[] tempOut = new double[rangedMap.length];
        smAverage = new Double[rangedMap.length];

        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        for (int i = 0; i < rangedMap.length; ++i) {
            closePrice[i] = (rangedMap[i].getValue().get("closeAsk"))
                    .doubleValue();
        }

        Core core = new Core();

        RetCode retCode = core.sma(0, closePrice.length - 1, closePrice,
                periods, begin, length, tempOut);
        System.arraycopy(ArrayUtils.toObject(tempOut), 0, smAverage,
                begin.value, smAverage.length - (begin.value));

        putRenderedData(this.getClass().getSimpleName(), smAverage);
        String[] arff = new String[1];
        arff[0] = String.valueOf(periods);
        putRenderedData("arff", arff);
        setLength(length.value);
        setOffset(begin.value);
//        logger.log(Level.INFO, this.getClass().getName() + " render: "
//                + retCode.name());
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }
}
