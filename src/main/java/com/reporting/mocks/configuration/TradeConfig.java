package com.reporting.mocks.configuration;

import com.reporting.mocks.model.underlying.OtcUnderlying;
import com.reporting.mocks.model.underlying.SecurityStatic;
import com.reporting.mocks.model.underlying.Underlying;

import java.util.List;

public class TradeConfig {
    protected int startingTradeCount = 100;
    protected int newTradeStart = 0;
    protected int newTradePeriodicity = 10 * 1000;        // number of milliseconds between new trades (default: 10s)

    protected int modifiedTradeStart = 30 * 1000;
    protected int modifiedTradePeriodicity = 30 * 1000;    // number of milliseconds between trade modifications (default: 30s)

    protected int deleteTadeStart = 60 * 1000;
    protected int deleteTradePeriodicity = 60 * 1000;     // number of milliseconds between deletion of trades (default: 60s)

    List<String> books;
    List<OtcUnderlying> otcUnderlying;
    List<SecurityStatic> securityStatic;

    public TradeConfig() {
    }

    public TradeConfig(List<String> books, List<OtcUnderlying> otcUnderlying, List<SecurityStatic> securityStatic) {
        this.books = books;
        this.otcUnderlying = otcUnderlying;
        this.securityStatic = securityStatic;
    }

    public int getStartingTradeCount() {
        return startingTradeCount;
    }

    public void setStartingTradeCount(int startingTradeCount) {
        this.startingTradeCount = startingTradeCount;
    }

    public List<String> getBooks() {
        return books;
    }

    public List<OtcUnderlying> getOtcUnderlying() {
        return otcUnderlying;
    }

    public List<SecurityStatic> getSecurityStatic() {
        return securityStatic;
    }

    public int getNewTradePeriodicity() {
        return newTradePeriodicity;
    }

    public int getModifiedTradePeriodicity() {
        return modifiedTradePeriodicity;
    }

    public int getDeleteTradePeriodicity() {
        return deleteTradePeriodicity;
    }

    public int getNewTradeStart() {
        return newTradeStart;
    }

    public int getModifiedTradeStart() {
        return modifiedTradeStart;
    }

    public int getDeleteTadeStart() {
        return deleteTadeStart;
    }
}
