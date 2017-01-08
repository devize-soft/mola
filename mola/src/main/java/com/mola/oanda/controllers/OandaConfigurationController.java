package com.mola.oanda.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by bilgi on 3/22/15.
 */
@Controller
public class OandaConfigurationController {

    @RequestMapping("/oanda/configuration")
    public String configure(@RequestParam(value = "name", required = false, defaultValue = "World") String name){

        return "oanda-conf";
    }
}
