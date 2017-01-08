package com.mola.oanda.managers.impl;

import com.mola.charts.BaseChart;
import com.mola.managers.TradeManager;
import com.mola.persistence.managers.PersistenceManager;
import com.mola.services.ticker.Tick;
import com.mola.trade.Trade;
import com.mola.util.MathUtils;
import com.oanda.fxtrade.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class OandaTradeManagerImpl implements TradeManager {

    //TODO this client should be wrapped because its always disconnecting and throwing exceptions
    // SessionDisconnectedException Session not logged in
    @Autowired
    FXClient client;

    @Autowired
    PersistenceManager persistenceManager;

    @Autowired
    String oandaAccount;

    int accountId= 0; // TODO put in config

    private Set shortOrders = new LinkedHashSet();
    private Set longOrders = new LinkedHashSet();

    @PostConstruct
    public void initializeProviders(){
        login();
    }

    public OandaTradeManagerImpl() {

    }

    public Object executeLongOrder(Tick tick, BaseChart chart){
        // TODO really? persist trades, don't allow trading in the opposite dir of open trades
        // causes big losses
        Boolean positionForPair = getPositionForPair(chart.getPair().getNameFormatted());
        if(positionForPair != null && positionForPair == false){
            return false;
        }

        if(!client.isLoggedIn()){
            login();
        }
        // TODO get account by id set in admin panel


        Account accountById = getAccountById(accountId);
        MarketOrder order = API.createMarketOrder();
        order.setPair(API.createFXPair(chart.getPair().getNameFormatted()));
        order.setUnits(50);
        double delta = (tick.getAsk() - tick.getBid()) * 5.0;
        StopLossOrder stopLossOrder = API.createStopLossOrder(MathUtils.round(tick.getAsk() - delta, 5));
        order.setStopLoss(stopLossOrder);
//        order.setTrailingStopLoss(5.0);
        order.setTakeProfit(API.createTakeProfitOrder(tick.getAsk() + 0.0009));
        try {//Your Stop Loss must be a non-zero positive value and below the bid price when entering a long position or above the ask price when entering a short position.
            accountById.execute(order);
        } catch (OAException e) {
            e.printStackTrace();
        }
        saveTrade(order, tick, true, chart);
        System.out.println("going long" + tick);
        return order;
    }

    private void saveTrade(MarketOrder order, Tick tick, boolean longFlag, BaseChart chart) {
        Trade trade = new Trade(tick, order.getTransactionNumber(), order.getPrice(), order.getTakeProfit(), chart, order.getStopLoss(), "oanda", longFlag);
        persistenceManager.saveOrUpdate(trade);
    }

    public Object executeShortOrder(Tick tick, BaseChart chart){
        // TODO really? persist trades, don't allow trading in the opposite dir of open trades
        // causes big losses
        Boolean positionForPair = getPositionForPair(chart.getPair().getNameFormatted());
        if(positionForPair != null && positionForPair == true){
            return false;
        }

        System.out.println("going short " + tick);
        if(!client.isLoggedIn()){
            login();
        }

        Account accountById = getAccountById(accountId);
        MarketOrder order = API.createMarketOrder();
        order.setPair(API.createFXPair(chart.getPair().getNameFormatted()));

        // TODO this is taken from sample code, simple stop loss should do for now.
        // TODO need a better stop loss strategy.
        double delta = (tick.getAsk() - tick.getBid()) * 5.0;
        StopLossOrder stopLossOrder = API.createStopLossOrder(MathUtils.round(tick.getAsk() + delta, 5));
        order.setStopLoss(stopLossOrder);
//        order.setTrailingStopLoss(5.0);
        order.setTakeProfit(API.createTakeProfitOrder(tick.getAsk() - 0.0009));
        order.setUnits(-50);
        try {
            accountById.execute(order);
            shortOrders.add(order);
        } catch (OAException e) {
            e.printStackTrace();
        }
        saveTrade(order, tick, false, chart);
        return order;
    }

    public Account getAccountById(int id){
        List<Account> accounts=null;
        Account account=null;
        if(!client.isLoggedIn()){
            if(!login()){
                return null;
            }
        }

        try {
            accounts = client.getUser().getAccounts();
        } catch (SessionException e) {

        }

        for(Account acc:accounts){
            if(acc.getAccountId() == id){
                account = acc;
            }
        }
        return account;
    }

    private boolean login(){
        try {
            client.login("","");
        } catch (InvalidUserException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (SessionException e) {
            e.printStackTrace();
        } catch (MultiFactorAuthenticationException e) {
            e.printStackTrace();
        }

        return client.isLoggedIn();
    }

    public Vector<? extends MarketOrder> getOpenTrades() {

	if (!client.isLoggedIn()) {
		login();
	}
        Account account = getAccountById(accountId);
        // request the the current account's MarketOrders
        Vector<? extends MarketOrder> trades = null;
        try {
            if(account != null) {
                trades = account.getTrades();
            }
            if (trades != null) {
                for (int i = 0; i < trades.size(); i++) {
                    System.out.println((i + 1) + ": " + trades.elementAt(i));
                }
            }
        }
        catch (AccountException ae) {
            ae.printStackTrace();
        }
        return trades;
    }

    public int countOpenTrades(){
        Vector<? extends MarketOrder> openTrades = getOpenTrades();
        int count =0;
        if (openTrades != null) {
            count = openTrades.size();
        }
        return count;
    }

    @Override
    public void checkOrUpdateExistingTrades(Tick tick, BaseChart baseChart) {
        Vector<? extends MarketOrder> openTrades = getOpenTrades();
        for(MarketOrder order: openTrades){

        }
    }

    public Map<String, Boolean> getPositions() {

        if (!client.isLoggedIn()) {
            login();
        }
        Account account = getAccountById(accountId);
        Map<String, Boolean> positionsMap = new LinkedHashMap<>();
        try {
            Vector<Position> positions = account.getPositions();
            if (positions != null) {
                for (int i = 0; i < positions.size(); i++) {
                    if(positions.get(0).getUnits() < 0){
                        positionsMap.put(positions.get(i).getPair().getPair(), false);
                    }else{
                        positionsMap.put(positions.get(i).getPair().getPair(), true);
                    }
                }
            }
        }
        catch (AccountException ae) {
            ae.printStackTrace();
        }
        return positionsMap;
    }

    public Boolean getPositionForPair(String pair){
        Map<String, Boolean> positions = getPositions();
        return positions.get(pair);
    }

}