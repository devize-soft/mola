package com.mola.weka.managers;

import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Instances;

import java.io.*;
import java.util.List;

public class WekaTimeSeriesManager {

    public double[] process(ByteArrayInputStream stream) {
        PrintStream originalStream = System.out;
        PrintStream dummyStream    = new PrintStream(new OutputStream(){
            public void write(int b) {
                //NO-OP
            }
        });
        System.setOut(dummyStream);
        double[] predictions = null;
        try {
            // load the wine data
            Instances wine = new Instances(new BufferedReader(
                    new InputStreamReader(stream)));

            // new forecaster
            WekaForecaster forecaster = new WekaForecaster();

            forecaster.setFieldsToForecast("OandaCandleStickChart.closeAsk");

            // default underlying classifier is SMOreg (SVM) - we'll use
            // gaussian processes for regression instead
            // forecaster.setBaseForecaster(new GaussianProcesses());

            forecaster.getTSLagMaker().setTimeStampField(
                    "OandaCandleStickChart.time:date"); // date time stamp
            forecaster.getTSLagMaker().setMinLag(1);
            forecaster.getTSLagMaker().setMaxLag(1); // monthly data

            // add a month of the year indicator field
            forecaster.getTSLagMaker().setAddMonthOfYear(true);

            // add a quarter of the year indicator field
            forecaster.getTSLagMaker().setAddQuarterOfYear(true);

            // build the model
            forecaster.buildForecaster(wine, System.out);

            // prime the forecaster with enough recent historical data
            // to cover up to the maximum lag. In our case, we could just supply
            // the 12 most recent historical instances, as this covers our
            // maximum
            // lag period
            forecaster.primeForecaster(wine);

            // forecast for 12 units (months) beyond the end of the
            // training data
            List<List<NumericPrediction>> forecast = forecaster.forecast(5,
                    System.out);

            // output the predictions. Outer list is over the steps; inner list
            // is over
            // the targets
            predictions = new double[forecast.size()];
            for (int i = 0; i < forecast.size(); i++) {
                List<NumericPrediction> predsAtStep = forecast.get(i);
                for (int j = 0; j < predsAtStep.size(); j++) {
                    NumericPrediction predForTarget = predsAtStep.get(j);
                    predictions[i] = predForTarget.predicted();
//                    System.out.print("" + predForTarget.predicted() + " ");
                }
//                System.out.print(", ");
            }

            // we can continue to use the trained forecaster for further
            // forecasting
            // by priming with the most recent historical data (as it becomes
            // available).
            // At some stage it becomes prudent to re-build the model using
            // current
            // historical data.

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.setOut(originalStream);
        return predictions;
    }
}
