package com.mola.oanda.services.impl;

import com.mola.managers.TradeManager;
import com.mola.persistence.managers.PersistenceManager;
import com.mola.services.ticker.Tick;
import com.mola.services.ticker.TickerObserver;
import com.mola.services.ticker.TickerService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bilgi on 4/7/15.
 */
@Service
public class OandaEventsServiceImpl extends TickerService implements Runnable {

    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    private Set<TickerObserver> managers = new LinkedHashSet<TickerObserver>();

    @Autowired
    String oandaAccount;

    @Autowired
    String oandaStreamUrl;

    @Autowired
    String oandaApiToken;

    @Autowired
    PersistenceManager persistenceManager;

    @Autowired
    TradeManager tradeManager;

    @Override
    public void startTicker(){
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.execute(this);
    }

    public void run() {
        try {
            HttpUriRequest httpGet = new HttpGet(oandaStreamUrl
                    + "/v1/events?accountIds=" + oandaAccount);
            httpGet.setHeader(new BasicHeader("Authorization", "Bearer "
                    + oandaApiToken));

            System.out
                    .println("Executing request: " + httpGet.getRequestLine());

            HttpResponse resp = null;
            try {
                resp = httpClient.execute(httpGet);
            } catch (ClientProtocolException e2) {
                e2.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            HttpEntity entity = resp.getEntity();

            if (resp.getStatusLine().getStatusCode() == 200 && entity != null) {
                InputStream stream = null;
                try {
                    stream = entity.getContent();
                } catch (IllegalStateException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        stream));

                try {
                    while ((line = br.readLine()) != null) {
                        Object obj = JSONValue.parse(line);
                        JSONObject jsonObj = (JSONObject) obj;
                        // unwrap if necessary
                        if (jsonObj.containsKey("tick")) {
                            jsonObj = (JSONObject) jsonObj.get("tick");
                        }

                        // ignore heartbeats
                        if (!jsonObj.containsKey("heartbeat")) {
//                                System.out.print("---");

                            String instrument = jsonObj.get("instrument")
                                    .toString();
                            String time = jsonObj.get("time").toString();
                            double bid = Double.parseDouble(jsonObj.get("bid")
                                    .toString());
                            double ask = Double.parseDouble(jsonObj.get("ask")
                                    .toString());
                            Tick tick = new Tick(instrument, time, bid, ask);
                            if (!isPaused()) {
                                notifyObservers(tick);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                String responseString = null;
                try {
                    responseString = EntityUtils.toString(entity, "UTF-8");
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(responseString);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
