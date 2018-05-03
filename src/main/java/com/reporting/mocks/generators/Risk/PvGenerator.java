package com.reporting.mocks.generators.Risk;

import com.reporting.mocks.generators.IRiskGenerator;
import com.reporting.mocks.process.risks.requests.RiskRunRequest;
import com.reporting.mocks.model.trade.Trade;
import com.reporting.mocks.model.risks.Pv;
import com.reporting.mocks.model.risks.Risk;
import com.reporting.mocks.model.risks.RiskType;
import com.reporting.mocks.model.underlying.Currency;

import static com.reporting.mocks.model.risks.RiskType.PV;


public class PvGenerator implements IRiskGenerator {
    @Override
    public Risk generate(RiskRunRequest riskRun, Trade trade) {
        return new Pv(riskRun.getId(), trade.getTcn(), trade.getUnderlying().getUnderlyingCurrency());
    }

    @Override
    public RiskType getRiskType() {
        return PV;
    }
}
