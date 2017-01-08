package com.mola.charts.util;

import com.mola.charts.BaseChart;
import com.mola.charts.ChartType;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChartFetcher {

    // Logger logger = Logger.getLogger(ChartFetcher.class);

    private ChartType chartType = ChartType.candles;
    private String pair;
    private Granularity granularity = Granularity.M1;
    private URL url;

    public ChartFetcher() {

    }

    public ChartFetcher(Pair pair, Granularity granularity) {
        this.pair = pair.toString();
        this.granularity = granularity;
    }

    public URL buildUrl(String oandaUrl,Date start, Date end, int lookbackMax) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'");
        String startString = sdf.format(start);
        String endString = sdf.format(end);
        String dateParams;
        String encodedDateParams;
        try {
            dateParams = "&start=" + startString + "&end=" + endString;
            encodedDateParams = URLEncoder.encode(dateParams, "UTF-8");
            encodedDateParams = "start="
                    + URLEncoder.encode(startString, "UTF-8") + "&end="
                    + URLEncoder.encode(endString, "UTF-8");
            encodedDateParams = "";
            url = new URL(oandaUrl +"/v1/"+ chartType + "?instrument=" + pair
                    + "&granularity=" + granularity.name()
                    + encodedDateParams);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public void fetchChart(String oandaUrl, String oandaApiToken, BaseChart chart, Date start, Date end,
                           int lookbackMax) {
        URL url = buildUrl(oandaUrl, start, end, lookbackMax);
        JSONArray results = null;

        URLConnection conn = null;
        try {
            conn = url.openConnection();
            conn.setRequestProperty("Authorization",
                    "Bearer " +  oandaApiToken);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream is = conn.getInputStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is,
                    Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            results = json.getJSONArray("candles");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject[] list = new JSONObject[results.length()];
        try {
            for (int i = 0; i < results.length(); i++) {
                list[i] = (JSONObject) results.get(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        chart.setQuotes(list);
        chart.setUrl(url);
        chart.render();
        // TODO persistChart(chart);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
