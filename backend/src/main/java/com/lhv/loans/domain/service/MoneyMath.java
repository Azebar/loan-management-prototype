package com.lhv.loans.domain.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

final class MoneyMath {

    static final MathContext RATE_MATH = MathContext.DECIMAL64;
    static final int MONEY_SCALE = 2;
    static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    private MoneyMath() {}

    static BigDecimal monthlyRate(BigDecimal annualPercent) {
        return annualPercent
                .divide(BigDecimal.valueOf(100), RATE_MATH)
                .divide(BigDecimal.valueOf(12), RATE_MATH);
    }

    static BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }
}
