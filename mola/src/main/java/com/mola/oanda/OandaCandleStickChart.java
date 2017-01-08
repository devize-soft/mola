package com.mola.oanda;

import com.mola.charts.BaseChart;
import com.mola.charts.ChartType;
import com.mola.instruments.Quote;
import org.codehaus.jettison.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class OandaCandleStickChart extends BaseChart {

    private Double[] openAsk;
    private Double[] highAsk;
    private Double[] lowAsk;
    private Double[] closeAsk;
    private String[] time;

    static {
        entries.add("time");
        entries.add("openBid");
        entries.add("openAsk");
        entries.add("highBid");
        entries.add("highAsk");
        entries.add("lowBid");
        entries.add("lowAsk");
        entries.add("closeBid");
        entries.add("closeAsk");
        entries.add("volume");
        entries.add("complete");
    }

    public OandaCandleStickChart() {
        setChartType(ChartType.candles);
    }

    public void buildChart() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        for (int i = 0; i < quotes.length; ++i) {
            Quote<String, Object> q = buildQuote((JSONObject) quotes[i]);
            try {
                Date date = new Date(sdf.parse((String) q.get("time"))
                        .getTime());
                getPrice().put(date, q);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void render() {
        buildChart();
        Map.Entry<Date, Quote<String, Object>>[] rangedMap = (Map.Entry<Date, Quote<String, Object>>[]) new Map.Entry[getPrice()
                .size()];
        getPrice().entrySet().toArray(rangedMap);

        openAsk = new Double[rangedMap.length];
        highAsk = new Double[rangedMap.length];
        lowAsk = new Double[rangedMap.length];
        closeAsk = new Double[rangedMap.length];
        time = new String[rangedMap.length];
        String[] direction = new String[rangedMap.length];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // yyyy-MM-dd HH:mm:ss
        SimpleDateFormat newFormat = new SimpleDateFormat(
                "dd.MM.yyyy HH:mm:ss.SSS");
        try {
            for (int i = 0; i < rangedMap.length; ++i) {
                openAsk[i] = (Double) rangedMap[i].getValue().get("openAsk");
                highAsk[i] = (Double) rangedMap[i].getValue().get("highAsk");
                lowAsk[i] = (Double) rangedMap[i].getValue().get("lowAsk");
                closeAsk[i] = (Double) rangedMap[i].getValue().get("closeAsk");
//                time[i] = String.valueOf("\"" + sdf.parse((String) rangedMap[i].getValue().get("time")) + "\"");
                time[i] = "\""
                        + newFormat.format(sdf.parse((String) (rangedMap[i]
                        .getValue().get("time")))) + "\"";
//                if (time[i].contains("12:00:00")) {
//                    time[i] = time[i].replaceAll("12:00:00", "00:00:00");
//                } else if (time[i].contains("12:30:00")) {
//                    time[i] = time[i].replaceAll("12:30:00", "00:30:00");
//                }
                direction[i] = closeAsk[i].compareTo(openAsk[i]) == 1 ? "1"
                        : "0";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        putRenderedData(this.getClass().getSimpleName() + ".openAsk", openAsk);
        putRenderedData(this.getClass().getSimpleName() + ".highAsk", highAsk);
        putRenderedData(this.getClass().getSimpleName() + ".lowAsk", lowAsk);
        putRenderedData(this.getClass().getSimpleName() + ".closeAsk", closeAsk);
        putRenderedData(this.getClass().getSimpleName() + ".time:date", time);
        putRenderedData(this.getClass().getSimpleName() + ".direction",
                direction);

        setLength(rangedMap.length);
        setOffset(0);
        System.out.println(this.getClass().getName() + " render: Success");
    }

    public Double[] getOpenAsk() {
        return openAsk;
    }

    public void setOpenAsk(Double[] openAsk) {
        this.openAsk = openAsk;
    }

    public Double[] getHighAsk() {
        return highAsk;
    }

    public void setHighAsk(Double[] highAsk) {
        this.highAsk = highAsk;
    }

    public Double[] getLowAsk() {
        return lowAsk;
    }

    public void setLowAsk(Double[] lowAsk) {
        this.lowAsk = lowAsk;
    }

    public Double[] getCloseAsk() {
        return closeAsk;
    }

    public void setCloseAsk(Double[] closeAsk) {
        this.closeAsk = closeAsk;
    }

    public String[] getTime() {
        return time;
    }

    public void setTime(String[] time) {
        this.time = time;
    }

    public int getPeriods() {
        return 0;
    }

    public void setPeriods(int periods) {

    }
}
