package com.reporting.mocks.configuration.defaults;

import com.reporting.mocks.configuration.*;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.risks.IntradayRiskType;
import com.reporting.mocks.model.risks.RiskType;
import com.reporting.mocks.model.trade.TradeType;
import com.reporting.mocks.model.underlying.SecurityStatic;

import java.util.*;

public class FXSpotDeskDefaultPricingGroupConfig extends PricingGroupConfig {
    protected final int startingTradeCount = 100;
    protected final int newTradeStart = 0;
    protected final int newTradePeriodicity = 100;        // number of milliseconds between new tcnTrades (default: 10s)

    protected final int modifiedTradeStart = 1000;
    protected final int modifiedTradePeriodicity = 1000;    // number of milliseconds between trade modifications (default: 30s)

    protected final int deleteTadeStart = 6 * 1000;
    protected final int deleteTradePeriodicity = 6 * 1000;

    public FXSpotDeskDefaultPricingGroupConfig() {
        ArrayList<String> books = new ArrayList<>();
        UnderlyingConfig underlyings = new UnderlyingConfig();
        Map<TradeType, List<RiskType>> tradeTypeAndRisks = new HashMap<>();


        this.pricingGroupId = new PricingGroup("fxspotdesk");
        // - URN: book:<department pricingGroup>:<desk pricingGroup>:<book pricingGroup>
        //   e.g., book:fxdesk:fxspots:bookname
        books.add("bank:fxdesk:fxspot:Book1");
        books.add("bank:fxdesk:fxspot:Book2");
        books.add("bank:fxdesk:fxspot:Book3");


        underlyings.addSet("EUR", Arrays.asList("USD", "CHF", "GBP", "MXN", "JPY", "AUD", "RBL"));
        underlyings.addSet("USD", Arrays.asList("CHF", "GBP", "MXN", "JPY", "AUD", "RBL"));

        tradeTypeAndRisks.put(TradeType.Payment, Arrays.asList(RiskType.PV));
        tradeTypeAndRisks.put(TradeType.Spot, Arrays.asList(RiskType.PV, RiskType.DELTA));
        tradeTypeAndRisks.put(TradeType.Forward, Arrays.asList(RiskType.PV, RiskType.DELTA));
        tradeTypeAndRisks.put(TradeType.Swap, Arrays.asList(RiskType.PV, RiskType.DELTA));


        this.tradeConfig = new TradeConfig(books, underlyings, tradeTypeAndRisks);
        this.tradeConfig.setStartingTradeCount(startingTradeCount);
        this.tradeConfig.setNewTradeStart(newTradeStart);
        this.tradeConfig.setNewTradePeriodicity(newTradePeriodicity);
        this.tradeConfig.setModifiedTradeStart(modifiedTradeStart);
        this.tradeConfig.setModifiedTradePeriodicity(modifiedTradePeriodicity);
        this.tradeConfig.setDeleteTadeStart(deleteTadeStart);
        this.tradeConfig.setDeleteTradePeriodicity(deleteTradePeriodicity);

        ArrayList<RiskType> eodr = new ArrayList<>();
        eodr.add(RiskType.PV);
        eodr.add(RiskType.DELTA);
        this.endofdayConfig = new EndofDayConfig(eodr, 5 * 60 * 1000);

        ArrayList<IntradayRiskType> indr = new ArrayList<>();
        indr.add(new IntradayRiskType(RiskType.PV, 1));
        indr.add(new IntradayRiskType(RiskType.DELTA, 3));
        this.intradayConfig = new IntradayConfig(indr);


    }
}
