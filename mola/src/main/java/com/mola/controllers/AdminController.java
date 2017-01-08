package com.mola.controllers;

import com.mola.account.AccountType;
import com.mola.managers.TradeManager;
import com.mola.services.ChartService;
import com.mola.services.ticker.TickerService;
import com.oanda.fxtrade.api.FXClient;
import com.oanda.fxtrade.api.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by bilgi on 3/22/15.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    TickerService tickerService;

    @Autowired
    TradeManager tradeManager;

    @Autowired
    ChartService chartService;
    @Autowired
    FXClient client;

    @RequestMapping("")
    public String admin(Model model){
        // TODO find existing providers from db.
        model.addAttribute("tickerStatus", tickerService.isPaused());
        model.addAttribute("accountTypes", AccountType.getTypes());
        return "/admin/index";
    }

    @RequestMapping("/providers")
    public String providers(Model model){
        model.addAttribute("accountTypes", AccountType.getTypes());
        return "/admin/index";
    }

    @RequestMapping("/oanda")
    public String oanda(Model model){
        Vector accounts = null;
        if(client.isLoggedIn()){
            try {
                accounts = client.getUser().getAccounts();
            } catch (SessionException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("accounts", accounts);
        return "/admin/accounts";
    }

    @RequestMapping("/pause.json")
    @ResponseBody
    public Map<String, Object> pause(Model model){
        Map<String, Object> ret = new HashMap<>();
        if(tickerService.isPaused()){
            chartService.unpause();
        }
        tickerService.setPaused(!tickerService.isPaused());
        model.addAttribute("paused", tickerService.isPaused());
        return ret;
    }

    @RequestMapping("/positions")
    public String openPositions(Model model){
        tradeManager.getPositions();
        tradeManager.getOpenTrades();
        return "/admin/positions";
    }
}
