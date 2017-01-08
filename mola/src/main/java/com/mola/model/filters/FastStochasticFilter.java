package com.mola.model.filters;

import com.mola.charts.BaseChart;
import com.mola.charts.Granularity;
import com.mola.charts.Period;
import com.mola.charts.oscillators.StochasticOscillator;
import com.mola.model.AbstractModel;
import com.mola.model.FastStochasticModel;
import com.mola.oanda.OandaCandleStickChart;
import com.mola.services.ticker.Tick;
import com.mola.util.MathUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by bilgi on 3/26/15.
 */
public class FastStochasticFilter extends AbstractFilter{

    private Granularity granularity;

    @Override
    public AbstractModel doFilter(Tick tick, BaseChart chart) {
        granularity = chart.getGranularity();
        StochasticOscillator stochChart = new StochasticOscillator(chart, 5,3,3);
        stochChart.setPeriod(Period.MINUTE);
        stochChart.render();
        Double[] temp = stochChart.getOutSlowK();
        double[] tempOutSlowk = new double[temp.length];
        Map<Date, Double> tempOutSlowkMap = new LinkedHashMap<Date, Double>();
        int outSlowKIndex = 0;
        String[] time = ((OandaCandleStickChart) stochChart.getParentChart()).getTime();
        DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        for(int i=0; i < temp.length; ++i){
            if(temp[i] == null){
                continue;
            }
            try {
                tempOutSlowkMap.put(simpleDateFormat.parse(time[i].replaceAll("\"","")), temp[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tempOutSlowk[outSlowKIndex] = temp[i];
            ++outSlowKIndex;
        }

        double[] outSlowK = new double[outSlowKIndex];
        for(int i=0; i < outSlowKIndex; ++i){
            outSlowK[i] = tempOutSlowk[i];
        }

        double stdDev = MathUtils.stdDev(outSlowK);
        double mean = MathUtils.mean(outSlowK);

        double[] aboveMeanArray = new double[outSlowK.length];
        double[] belowMeanArray = new double[outSlowK.length];
        Map<Date, Double> aboveMeanMap = new LinkedHashMap<Date, Double>();
        Map<Date, Double> belowMeanMap = new LinkedHashMap<Date, Double>();
        int aboveMeanIndex = 0;
        int belowMeanIndex = 0;

        int count=0;
        for (Map.Entry<Date, Double> dateDoubleEntry : tempOutSlowkMap.entrySet()) {
            if(outSlowK[count] >= mean){
                aboveMeanArray[aboveMeanIndex] = outSlowK[count];
                aboveMeanMap.put(dateDoubleEntry.getKey(), outSlowK[count]);
                ++aboveMeanIndex;
            }else{
                belowMeanArray[belowMeanIndex] = outSlowK[count];
                belowMeanMap.put(dateDoubleEntry.getKey(), outSlowK[count]);
                ++belowMeanIndex;
            }
            ++count;
        }

        ArrayList<Double> upperPeakList = new ArrayList<>();
        Map<Date, Double> upperPeakMap = new LinkedHashMap<>();
        double upperPeakTemp = -999;
        boolean up = true;
        count = 0;
        Map.Entry<Date, Double> aboveMeanMapEntry = null;
        // get peaks
        for (Map.Entry<Date, Double> dateDoubleEntry : aboveMeanMap.entrySet()) {
            if(upperPeakMap.size() ==0 && upperPeakTemp == -999){
                upperPeakTemp = aboveMeanArray[count];
                aboveMeanMapEntry = dateDoubleEntry;
                ++count;
                continue;
            }

            if(upperPeakTemp > aboveMeanArray[count] && up) {
                upperPeakList.add(new Double(upperPeakTemp));
                upperPeakMap.put(aboveMeanMapEntry.getKey(), aboveMeanMapEntry.getValue());
            }

            if(upperPeakTemp < aboveMeanArray[count]){
                up = true;
            }else{
//                upperPeakMap.put(aboveMeanMapEntry.getKey(), aboveMeanMapEntry.getValue());
                up = false;
            }

            upperPeakTemp = aboveMeanArray[count];
            aboveMeanMapEntry = dateDoubleEntry;
            ++count;
        }
        Map.Entry o = (Map.Entry)aboveMeanMap.entrySet().toArray()[aboveMeanMap.size() - 1];
        upperPeakMap.put((Date)o.getKey(), (Double) o.getValue());

        // get valleys
        ArrayList<Double> lowerPeakList = new ArrayList<>();
        Map<Date, Double> lowerPeakMap = new LinkedHashMap<>();
        double lowerPeakTemp = -999;
        boolean down = true;
        count=0;
        Map.Entry<Date, Double> lowerPeakTempEntry = null;
        for (Map.Entry<Date, Double> dateDoubleEntry : belowMeanMap.entrySet()) {
            if(lowerPeakMap.size() ==0 && lowerPeakTemp == -999){
                lowerPeakTemp = belowMeanArray[count];
                lowerPeakTempEntry = dateDoubleEntry;
                ++count;
                continue;
            }

            if(lowerPeakTemp < belowMeanArray[count] && down) {
                lowerPeakList.add(new Double(lowerPeakTemp));
                lowerPeakMap.put(lowerPeakTempEntry.getKey(), lowerPeakTempEntry.getValue());
            }

            if(lowerPeakTemp > belowMeanArray[count]){
                down = true;
            }else{
//                lowerPeakMap.put(lowerPeakTempEntry.getKey(), lowerPeakTempEntry.getValue());
                down = false;
            }
            lowerPeakTempEntry = dateDoubleEntry;
            lowerPeakTemp = belowMeanArray[count];
            ++count;
        }

        o = (Map.Entry)belowMeanMap.entrySet().toArray()[belowMeanMap.size() - 1];
        lowerPeakMap.put((Date)o.getKey(), (Double) o.getValue());

        // calculate mean time spent above upper trendline per peak
        Iterator<Map.Entry<Date, Double>> tempOutSlowKMapIterator = tempOutSlowkMap.entrySet().iterator();
        double upperTrendline = mean + stdDev;
        double lowerTrendLine = mean - stdDev;
        int unitsAboveUpperTrendline = 0;
        int unitsBelowLowerTrendline = 0;
        while(tempOutSlowKMapIterator.hasNext()){
            Map.Entry<Date, Double> next = tempOutSlowKMapIterator.next();
            if(next.getValue() >= upperTrendline){
                ++unitsAboveUpperTrendline;
            }
            if(next.getValue()<= lowerTrendLine){
                ++unitsBelowLowerTrendline;
            }
        }

        // calculate mean time spent above/below trendlines per valley
        double averageTimeAboveTrendline = unitsAboveUpperTrendline/(double)upperPeakMap.size();
        double averageTimeBelowTrendline = unitsBelowLowerTrendline/(double)lowerPeakMap.size();

        AbstractModel model = new FastStochasticModel();
        if(determineDirection(tick, stdDev,mean,lowerPeakMap,upperPeakMap)){
            model.setEnterTrade(decideLongTrade(tick, averageTimeBelowTrendline, lowerPeakMap));
        }else{
            model.setEnterTrade(decideShortTrade(tick, averageTimeAboveTrendline, upperPeakMap));
        }

        return model;
    }

    private boolean determineDirection(Tick tick, double stddev, double mean, Map<Date, Double> lowerPeakMap, Map<Date, Double> upperPeakMap){
        Map.Entry lowerPeakEntry = (Map.Entry) lowerPeakMap.entrySet().toArray()[lowerPeakMap.size() - 1];
        Date lowerPeakEntryDate = (Date) lowerPeakEntry.getKey();

        Map.Entry upperPeakEntry = (Map.Entry) upperPeakMap.entrySet().toArray()[upperPeakMap.size() - 1];
        Date upperPeakEntryDate= (Date) upperPeakEntry.getKey();

        // true for long false for short
        boolean longShortFlag = lowerPeakEntryDate.getTime() > upperPeakEntryDate.getTime() ? true : false;
        return longShortFlag;
    }

    private Boolean decideLongTrade(Tick tick, double averageTimeBelowTrendline, Map<Date, Double> lowerPeakMap){
        Map.Entry upperPeakEntry = (Map.Entry) lowerPeakMap.entrySet().toArray()[lowerPeakMap.size() - 1];
        double millisecsFromFraction = granularity.getMillisecsFromFraction(averageTimeBelowTrendline);
        if(tick.getTime().getTime() - ((Date)upperPeakEntry.getKey()).getTime() <= millisecsFromFraction){
            return true;
        }
        return null;
    }

    private Boolean decideShortTrade(Tick tick, double averageTimeAboveTrendline, Map<Date, Double> upperPeakMap){
        Map.Entry upperPeakEntry = (Map.Entry) upperPeakMap.entrySet().toArray()[upperPeakMap.size() - 1];
        double millisecsFromFraction = granularity.getMillisecsFromFraction(averageTimeAboveTrendline);
        if(tick.getTime().getTime() - ((Date)upperPeakEntry.getKey()).getTime() <= millisecsFromFraction){
            return false;
        }
        return null;
    }
}