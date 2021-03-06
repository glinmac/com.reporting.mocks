package com.reporting.mocks.controllers;

import com.reporting.mocks.interfaces.persistence.IPersistenceStoreFactory;
import com.reporting.mocks.interfaces.persistence.ITradeStore;
import com.reporting.mocks.model.PricingGroup;
import com.reporting.mocks.model.TradePopulation;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
public class TradeController {
    @Autowired
    IPersistenceStoreFactory<ITradeStore> mongoTradeStoreFactory;

    @Autowired
    public TradeController(IPersistenceStoreFactory<ITradeStore> mongoTradeStoreFactory) {
        this.mongoTradeStoreFactory = mongoTradeStoreFactory;
    }

    @GetMapping("/tradepopulations/{pricingGroupName}")
    public List<TradePopulationId> tradePopulations(@PathVariable String pricingGroupName) {
        ITradeStore store = this.mongoTradeStoreFactory.get(new PricingGroup(pricingGroupName));
        if (store != null) {
            return store.getTradePopulationsIds();
        } else {
            return null;
        }
    }

    @GetMapping("/tradepopulation/{pricingGroupName}/{id}")
    public Collection<Trade> tradePopulation(@PathVariable String pricingGroupName, @PathVariable UUID id) {
        ITradeStore store = this.mongoTradeStoreFactory.get(new PricingGroup(pricingGroupName));
        if (store != null) {
            TradePopulation tradePopulation = store.getTradePopulation(new TradePopulationId(pricingGroupName,id));
            if (tradePopulation != null)
                return tradePopulation.getAllTrades();
        }
        return null;
    }
}
