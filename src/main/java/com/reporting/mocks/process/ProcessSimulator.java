package com.reporting.mocks.process;

import com.reporting.mocks.configuration.ApplicationConfig;
import com.reporting.mocks.configuration.PricingGroupConfig;
import com.reporting.mocks.endpoints.JavaQueue.RiskRunConsumerThread;
import com.reporting.mocks.generators.process.minibatch.RiskRunGeneratorThread;
import com.reporting.mocks.generators.TradeGenerator;
import com.reporting.mocks.generators.process.streaming.StreamRiskResultPublisherThread;
import com.reporting.mocks.generators.process.streaming.StreamRiskResultSetPublisherThread;
import com.reporting.mocks.generators.process.streaming.StreamRiskRunGeneratorThread;
import com.reporting.mocks.interfaces.persistence.ICalculationContextStore;
import com.reporting.mocks.interfaces.persistence.IMarketStore;
import com.reporting.mocks.interfaces.persistence.IRiskResultStore;
import com.reporting.mocks.interfaces.persistence.ITradeStore;
import com.reporting.mocks.interfaces.publishing.IResultPublisher;
import com.reporting.mocks.model.*;
import com.reporting.mocks.model.id.TradePopulationId;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.process.endofday.EndofDayRiskEventProducerThread;
import com.reporting.mocks.process.intraday.IntradayRiskEventProducerThread;
import com.reporting.mocks.process.markets.MarketEventProducerThread;
import com.reporting.mocks.process.trades.TradePopulationProducerThread;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.UUID;

public class ProcessSimulator {
    protected UUID id;
    protected ApplicationConfig appConfig;
    protected PricingGroupConfig config;
    protected MarketEventProducerThread marketEventProducerThread;
    protected IntradayRiskEventProducerThread intradayRiskEventProducerThread;
    protected RiskRunGeneratorThread riskRunGeneratorThread;
    protected StreamRiskRunGeneratorThread streamRiskRunGeneratorThread;
    protected StreamRiskResultSetPublisherThread streamRiskResultSetPublisherThread;
    protected StreamRiskResultPublisherThread streamRiskResultPublisherThread;



    protected ITradeStore tradeStore;
    protected ICalculationContextStore calculationContextStore;
    protected IMarketStore marketStore;
    protected TradeGenerator tradeGenerator;
    protected TradePopulationProducerThread tradePopulationProducerThread;
    protected IRiskResultStore riskResultStore;

    IResultPublisher resultPublisher;

    ProcessEventQueues processEventQueues;

    protected ThreadGroup threadGroup;

    public ProcessSimulator(PricingGroupConfig config,
                            ApplicationConfig appConfig,
                            ICalculationContextStore calculationContextStore,
                            IMarketStore marketStore,
                            ITradeStore tradeStore,
                            IRiskResultStore riskResultStore,
                            IResultPublisher resultPublisher) {
        this.id = UUID.randomUUID();
        this.appConfig = appConfig;
        this.config = config;
        this.tradeStore = tradeStore;
        this.calculationContextStore = calculationContextStore;
        this.marketStore = marketStore;
        this.riskResultStore = riskResultStore;
        this.resultPublisher = resultPublisher;
        this.tradeGenerator = new TradeGenerator(config.getTradeConfig());
        this.processEventQueues = new JavaProcessEventQueues();
    }

    public Collection<TradePopulation> getTradePopulations() {
        return this.tradeStore.getAllTradePopulation();
    }

    public TradePopulation getTradePopulation(TradePopulationId tradePopulationId) {
        return this.tradeStore.getTradePopulation(tradePopulationId);
    }

    public PricingGroup getPricingGroupId() {
        return this.config.getPricingGroupId();
    }

    protected void init() {
        if (this.threadGroup == null || this.threadGroup.isDestroyed()) {
                this.threadGroup = new ThreadGroup("PricingGroup: " + config.getPricingGroupId());

                // initialize the start calculation context
                CalculationContext cc = this.calculationContextStore.getCurrentContext();
                if (cc == null) {
                    cc = this.calculationContextStore.create();
                    cc.update(this.config.findAllRiskTypes(), marketStore.create(DataMarkerType.SOD));
                    this.calculationContextStore.setCurrentContext(cc);
                }

                RiskRunConsumerThread riskRunThread = new RiskRunConsumerThread(this.processEventQueues.getRiskResultSetQueue());
                new Thread(threadGroup, riskRunThread, "RiskRunConsumer").start();

//                this.riskRunGeneratorThread = new RiskRunGeneratorThread(
//                        this.processEventQueues.getRiskRunRequestQueue(),
//                        config,
//                        this.calculationContextStore,
//                        this.tradeStore,
//                        this.riskRunPublisher,
//                        this.riskResultStore
//                );
//            new Thread(threadGroup, this.riskRunGeneratorThread, "RiskRunGeneratorThread").start();

                this.streamRiskRunGeneratorThread = new StreamRiskRunGeneratorThread(this.processEventQueues.getRiskRunRequestQueue(),
                        this.processEventQueues.getRiskStreamMessageQueue(),
                        this.config,
                        this.calculationContextStore,
                        this.tradeStore,
                        this.resultPublisher);

//                this.streamRiskResultSetPublisherThread = new StreamRiskResultSetPublisherThread(this.processEventQueues.getRiskStreamMessageQueue(),
//                    this.config,
//                    this.resultPublisher,
//                    this.riskResultStore);

                this.streamRiskResultPublisherThread = new StreamRiskResultPublisherThread(
                        this.processEventQueues.getRiskStreamMessageQueue(),
                        this.config,
                        this.calculationContextStore,
                        this.resultPublisher,
                        this.riskResultStore
                );

                //new Thread(threadGroup, this.streamRiskResultSetPublisherThread, "StreamRiskResultSetPublisherThread").start();
                new Thread(threadGroup, this.streamRiskResultPublisherThread, "StreamRiskResultPublisherThread").start();
                new Thread(threadGroup, this.streamRiskRunGeneratorThread, "StreamRiskRunGeneratorThread").start();




                // kick-off end-of-day

                EndofDayRiskEventProducerThread eodThread = new EndofDayRiskEventProducerThread(
                        this.config.getPricingGroupId(),
                        this.config.getEndofdayConfig(),
                        this.tradeStore,
                        this.marketStore,
                        this.calculationContextStore,
                        this.processEventQueues.getRiskRunRequestQueue(),
                        this.resultPublisher);
                new Thread(threadGroup, eodThread, "EndofDayRiskEvent").start();

                // kick-off start-of-day

                // start intra-day process
                // initiate market environment change process
                this.marketEventProducerThread = new MarketEventProducerThread(
                        this.config.getPricingGroupId(),
                        this.marketStore,
                        this.resultPublisher,
                        this.config.getMarketPeriodicity(),
                        this.processEventQueues.getIntradayEventQueue());
                // start the market event thread
                new Thread(threadGroup, this.marketEventProducerThread, "MarketEventProducer").start();


                // initiate intra-day risk jobs
                this.intradayRiskEventProducerThread = new IntradayRiskEventProducerThread(
                        this.config.getPricingGroupId(),
                        this.config.getIntradayConfig(),
                        this.tradeStore,
                        this.marketStore,
                        this.calculationContextStore,
                        this.processEventQueues.getIntradayEventQueue(),
                        this.processEventQueues.getRiskRunRequestQueue(),
                        this.resultPublisher,
                        new MarketEnv(this.config.getPricingGroupId(), DataMarkerType.IND));

                new Thread(threadGroup, this.intradayRiskEventProducerThread, "IntradayRiskEvent").start();

                //TradeConfig tradeConfig, TradeStore tradeStore, BlockingQueue<Trade> tradeQueue
                this.tradePopulationProducerThread = new TradePopulationProducerThread(this.config.getTradeConfig(),
                        this.tradeStore,
                        this.tradeGenerator,
                        this.processEventQueues.getIntradayEventQueue(),
                        this.resultPublisher);
                new Thread(threadGroup, this.tradePopulationProducerThread, "TradePopulationProducer").start();

        }
    }

    public void stop() {
        if (this.threadGroup != null && !this.threadGroup.isDestroyed()) {
            //this.threadGroup.interrupt();
            this.threadGroup.interrupt();;
        }
    }

    public PricingGroupConfig start() {
        return this.start(this.config);
    }

    public PricingGroupConfig start(PricingGroupConfig config) {
        if (this.threadGroup == null || this.threadGroup.isDestroyed()) {
            if (this.config.getPricingGroupId() == config.getPricingGroupId()) {
                this.config = config;

                // initiate construction of initial trade population
                for (int i = 0; i < config.getTradeConfig().getStartingTradeCount(); i++) {
                    Trade newTrade = this.tradeGenerator.generateOneOtc();
                    this.tradeStore.add(newTrade);
                }

                this.init();

                return config;
            }
        }
        return null;
    }
}
