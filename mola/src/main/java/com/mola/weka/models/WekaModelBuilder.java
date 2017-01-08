package com.mola.weka.models;

import com.mola.charts.BaseChart;
import com.mola.charts.averages.EMAChart;
import com.mola.charts.averages.SMAChart;
import com.mola.charts.oscillators.StochasticOscillator;
import com.mola.model.AbstractModel;
import com.mola.model.builder.ModelBuilder;
import com.mola.persistence.managers.PersistenceManager;
import com.mola.util.MathUtils;
import com.mola.weka.managers.WekaTimeSeriesManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.Map.Entry;

public class WekaModelBuilder implements ModelBuilder {

    private Set<String> keyData = new LinkedHashSet<>();
    private static Set<String> preCombos = new LinkedHashSet<String>();

    @Autowired
    private PersistenceManager manager;

    {
        preCombos.add("StochasticOscilator-14:3:3");
        preCombos.add("StochasticOscilator-5:3:3");
        preCombos.add("EMAChart-30");
        preCombos.add("EMAChart-15");
        preCombos.add("SMAChart-30");
        preCombos.add("SMAChart-15");
    }

    public static Map<Integer, Map<String, int[]>> generateCombos() {
        Map<String, int[]> temp = new HashMap<>();
        Map<Integer, Map<String, int[]>> processedCombos = new HashMap<>();
        temp.put("stoch", new int[]{14, 3, 3});
        temp.put("ema", new int[]{30});
        temp.put("sma", new int[]{30});
        processedCombos.put(0, temp);
        temp = new HashMap<>();
        temp.put("stoch", new int[]{14, 3, 3});
        temp.put("ema", new int[]{15});
        temp.put("sma", new int[]{15});
        processedCombos.put(1, temp);
        temp = new HashMap<>();
        temp.put("stoch", new int[]{14, 3, 3});
        temp.put("ema", new int[]{30});
        temp.put("sma", new int[]{15});
        processedCombos.put(2, temp);
        temp = new HashMap<>();
        temp.put("stoch", new int[]{14, 3, 3});
        temp.put("ema", new int[]{15});
        temp.put("sma", new int[]{30});
        processedCombos.put(3, temp);
        temp = new HashMap<>();
        temp.put("stoch", new int[]{5, 3, 3});
        temp.put("ema", new int[]{30});
        temp.put("sma", new int[]{30});
        processedCombos.put(4, temp);
        temp = new HashMap<>();
        temp.put("stoch", new int[]{5, 3, 3});
        temp.put("ema", new int[]{15});
        temp.put("sma", new int[]{15});
        processedCombos.put(5, temp);
        temp = new HashMap<>();
        temp.put("stoch", new int[]{5, 3, 3});
        temp.put("ema", new int[]{30});
        temp.put("sma", new int[]{15});
        processedCombos.put(6, temp);
        temp = new HashMap<>();
        temp.put("stoch", new int[]{5, 3, 3});
        temp.put("ema", new int[]{15});
        temp.put("sma", new int[]{30});
        processedCombos.put(7, temp);
        return processedCombos;
    }

    @Override
    public List<AbstractModel> buildModel(BaseChart chart) {
        Map<Integer, Map<String, int[]>> processedCombos = generateCombos();
        List<BaseChart> auxilarryCharts = new ArrayList<>();
        List<AbstractModel> ret = new ArrayList<>();
//        addAuxilaryCharts(chart);
        for (Entry<Integer, Map<String, int[]>> entry : processedCombos
                .entrySet()) {
            keyData.clear();
            auxilarryCharts = new ArrayList<>();
            Map<String, int[]> value = entry.getValue();
            for (Entry<String, int[]> entry2 : value.entrySet()) {
                String key = entry2.getKey();
                int[] values = entry2.getValue();
                switch (key) {
                    case "ema":
                        auxilarryCharts.add(new EMAChart(chart, values[0]));
                        break;
                    case "sma":
                        auxilarryCharts.add(new SMAChart(chart, values[0]));
                        break;
                    case "stoch":
                        auxilarryCharts.add(new StochasticOscillator(chart,
                                values[0], values[1], values[2]));
                        break;
                }
            }
            chart.clearAuxillaryCharts();
            chart.setAuxillaryCharts(auxilarryCharts);
            chart.renderAuxillaryCharts();
            List<BaseChart> auxillaryCharts = chart.getAllCharts();

            int auxChartCount = chart.countRenderedData();
            int maxRows = chart.getMaxRows();
            Object[][] grid = new Object[auxChartCount][maxRows];

            if (auxillaryCharts.size() > 0) {
                int currentColumn = 0;
                int tempColumn = 0;
                int resetColumn = 0;

                for (int i = 0; i < auxillaryCharts.size(); ++i) {
                    BaseChart auxChart = auxillaryCharts.get(i);
                    Map<String, Object[]> data = auxChart.getRenderedData();
                    int startIndex = chart.getLength() - chart.getMaxRows();
                    // get key strings
                    List<String> keys = getKeys(auxChart);
                    List<Object[]> tempKeyData = new ArrayList<>();
                    for (String key : keys) {
                        tempKeyData.add(data.get(key));
                    }
                    tempColumn = currentColumn;
                    for (int j = 0; j < maxRows; ++j) {
                        for (int k = 0; k < tempKeyData.size(); ++k) {
                            grid[tempColumn][j] = tempKeyData.get(k)[startIndex];
                            if (tempColumn < keyData.size()) {
                                tempColumn++;
                            }
                            if (k >= keys.size() - 1) {
                                currentColumn = tempColumn;
                                tempColumn = resetColumn;
                            }
                        }
                        ++startIndex;
                    }
                    resetColumn = currentColumn;
                }
            }
            ret.add(buildArff(keyData, grid, chart));
        }
        return ret;
    }

    private BaseChart addAuxilaryCharts(BaseChart chart) {
        BaseChart aux = new StochasticOscillator(chart, 14, 3, 3);
        chart.setOffset(100);
        chart.addChart(aux);
        chart.addChart(new SMAChart(chart, 30));
        chart.addChart(new EMAChart(chart, 30));
        chart.render();
        chart.renderAuxillaryCharts();
        return chart;
    }

    @Override
    public BaseChart addAuxilaryChart(BaseChart baseChart) {

        return null;
    }

    public List<String> getKeys(BaseChart baseChart) {
        Map<String, Object[]> data = baseChart.getRenderedData();
        List<String> keys = new ArrayList<String>();
        for (Map.Entry<String, Object[]> entry : data.entrySet()) {
            if (!"arff".equals(entry.getKey())) {
                keys.add(entry.getKey());
            }
        }
        keyData.addAll(keys);
        return keys;
    }

    public AbstractModel buildArff(Set<String> keys, Object[][] grid,
                                   BaseChart chart) {

        StringBuilder builder = new StringBuilder();
        StringBuilder backTestBuilder = null;
        StringBuilder header = new StringBuilder();
        header.append("%URL: " + chart.getUrl().toString() + "\r\n\r\n");
        header.append("@relation " + chart.getPair().name() + "."
                + System.currentTimeMillis());
        header.append("\r\n");
        header.append("\r\n");

        for (String key : keys) {
            String type = "numeric";
            String[] attr = key.split(":");
            if (attr.length > 1) {
                type = attr[1];
            }
            header.append("@attribute " + key + " " + type);
            if ("date".equalsIgnoreCase(type)) {
                header.append("  \"dd.MM.yyyy HH:mm:ss.SSS\"");
            }
            header.append("\r\n");
        }

        header.append("\r\n");
        header.append("@data");
        header.append("\r\n");

        for (int i = (chart.getLength() - chart.getMaxRows()); i < chart
                .getMaxRows(); ++i) {
            for (int j = 0; j < grid.length; ++j) {
                if (grid[j][i] != null) {
                    builder.append(grid[j][i]);
                    if (j != (grid.length - 1)) {
                        builder.append(",");
                    }
                }
                if (j == (grid.length - 1)) {
                    builder.append("\r\n");
                }
            }
            if (i >= chart.getMaxRows() - 10 && backTestBuilder == null) {
                backTestBuilder = new StringBuilder();
                backTestBuilder.append(builder);
            }
        }

        StringBuilder arff = new StringBuilder();
        arff.append(header);
        arff.append(builder);
        AbstractModel model = runBackTest(arff, chart);

//        System.out.println(arff.toString());
//		manager.persistModel(model);
        return model;
    }

    private AbstractModel runBackTest(StringBuilder arff, BaseChart chart) {
        String[] split = arff.toString().split("\\r");
        WekaTimeSeriesManager timeSeriesManager = new WekaTimeSeriesManager();
//        System.out.println(arff);
        double totalCorrect = 0.0;
        int offset = 10; // TODO chart.getGranualirty.lookback; because each granularity may have diff lookbacks

        // Backtest with using known closing price to rate how well our time series classifier performs
        // Eventually the highest scoring dataset will be promoted, all others ignored.
        for (int i = offset; i > 0; --i) {
            String backTestString = Arrays.toString(Arrays.copyOfRange(split, 0, split.length - i));
            backTestString = backTestString.replaceAll("\\n", "\r\n").replace("[", "").replace("]", "");
//            System.out.println(backTestString);
            double[] predictions = timeSeriesManager.process(new ByteArrayInputStream(backTestString.toString()
                    .getBytes()));
            totalCorrect += computeScore(null, chart, predictions, split, offset);
        }

        // Predict future values for unknown prices
        WekaArffModel model = new WekaArffModel(chart);
        double[] predictions = timeSeriesManager.process(new ByteArrayInputStream(arff.toString()
                .getBytes()));

//        computeScore(model, chart, predictions, split, 0);
        model.setArff(arff.toString());
        model.setPredictedClose(predictions);
        model.setScore(totalCorrect);
        return model;
    }

    private double computeScore(WekaArffModel model, BaseChart chart, double[] predictions, String[] arff, int offset) {
        double totalCorrect = 0.0;
        double totalIncorrect = 0.0;
        int count = 1;
        double distance = 1.0;
        double stdDev = MathUtils.stdDev(predictions);
        for (double prediction : predictions) {
            Double actualCloseAsk = chart.getCloseAskForIndexJson((chart.getLength() - offset) + count);
            Double prevCloseAsk = chart.getCloseAskForIndexJson((chart.getLength() - offset) + (count - 2));

            // Reached the end of backtest, now predicting real future values.
            // So of course actualCloseAsk will be null.
            if (actualCloseAsk == null) {
                break;
            }

            if (prediction > prevCloseAsk && actualCloseAsk > prevCloseAsk) {
                prediction = prediction - stdDev;
                distance += Math.abs(actualCloseAsk - prevCloseAsk);
                ++totalCorrect;
            } else if (prediction < prevCloseAsk && actualCloseAsk < prevCloseAsk) {
                prediction = prediction + stdDev;
                distance += Math.abs(actualCloseAsk - prevCloseAsk);
                ++totalCorrect;
            } else {
                ++totalIncorrect;
            }
            if (model != null) {
                model.setPredictedClose(prediction);
            }
            ++count;
        }

        if (totalIncorrect == 0 || totalCorrect == 0) {
            return 0;
        }

        return (predictions.length / Math.abs(totalCorrect - totalIncorrect) * distance);
    }

    public AbstractModel getHighestScoringModel(List<AbstractModel> models) {
        AbstractModel tempModel = null;
        if (models != null && models.size() > 0) {
            for (AbstractModel m : models) {
                if (tempModel == null) {
                    tempModel = m;
                }
                if (m.getScore() > tempModel.getScore()) {
                    tempModel = m;
                }
            }
        }
        return tempModel;
    }
}
