package com.mola.services.ticker;

import javax.annotation.PostConstruct;

public abstract class TickerService {
    boolean paused = true;

    @PostConstruct
    public void startTicker() {

    }

    public void notifyObservers(Tick tick) {

    }

    public void addObserver(TickerObserver observer) {

    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}