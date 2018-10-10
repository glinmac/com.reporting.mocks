package com.reporting.mocks.configuration;

import com.reporting.mocks.configuration.defaults.FXOptionDeskDefaultPricingGroupConfig;
import com.reporting.mocks.configuration.defaults.FXSpotDeskDefaultPricingGroupConfig;
import com.reporting.mocks.configuration.defaults.FXSwapsDeskDefaultPricingGroupConfig;

public class ConfigurationManager {
    protected static ConfigurationManager configManager = null;

    public static ConfigurationManager getConfigurationManager() {
        if (ConfigurationManager.configManager == null) {
            ConfigurationManager.configManager = new ConfigurationManager();
            Config config = new Config();
            ConfigurationManager.configManager.setConfig(config);


            config.addPricingGroup(new FXSpotDeskDefaultPricingGroupConfig());
            config.addPricingGroup(new FXOptionDeskDefaultPricingGroupConfig());
            config.addPricingGroup(new FXSwapsDeskDefaultPricingGroupConfig());

        }
        return ConfigurationManager.configManager;
    }


    protected Config config;

    protected ConfigurationManager() {
    }

    public PricingGroupConfig getPriceingGroupConfig(String name) {
        return config.getPricingGroup(name);
    }

    public Config getConfig() {
        return this.config;
    }

    public Config setConfig(Config config) {
        this.config = config;
        return this.config;
    }
}

