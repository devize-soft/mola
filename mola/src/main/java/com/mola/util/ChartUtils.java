package com.mola.util;

import com.mola.charts.BaseChart;
import com.mola.instruments.Quote;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.util.Date;
import java.util.Map;

public class ChartUtils {

    @SuppressWarnings("unchecked")
    public void saveChart(BaseChart chart) {
        try {
            DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
            Map.Entry<Date, Quote<String, Double>>[] rangedMap = (Map.Entry<Date, Quote<String, Double>>[]) new Map.Entry[chart
                    .getPrice().size()];
            chart.getPrice().entrySet().toArray(rangedMap);
            for (int i = 0; i < rangedMap.length; ++i) {
                line_chart_dataset
                        .addValue((rangedMap[i].getValue().get("closeAsk"))
                                .doubleValue(), "price", rangedMap[i].getKey());
            }

            JFreeChart lineChartObject = ChartFactory.createLineChart(
                    "Instrument Chart", "Date", "Price", line_chart_dataset,
                    PlotOrientation.VERTICAL, true, true, false);
            CategoryPlot plot = (CategoryPlot) lineChartObject.getPlot();
            ValueAxis yAxis = plot.getRangeAxis();
            yAxis.setRange(1.250000, 1.260000);
            CategoryAxis xAxis = plot.getDomainAxis();
            xAxis.setFixedDimension(100);
            xAxis.setCategoryMargin(1.5);
            // xAxis.setMaximumCategoryLabelLines(1);
            int width = 640; /* Width of the image */
            int height = 240; /* Height of the image */
            File lineChart = new File("line_Chart_example.png");
            ChartUtilities.saveChartAsPNG(lineChart, lineChartObject, width,
                    height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}