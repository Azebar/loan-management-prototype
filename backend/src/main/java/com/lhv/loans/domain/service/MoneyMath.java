package com.lhv.loans.domain.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Shared numeric helpers for schedule calculations.
 *
 * <p>Convention:
 * <ul>
 *   <li>Money is rounded to 2 decimals using {@link RoundingMode#HALF_UP}.</li>
 *   <li>Intermediate rate math uses {@link MathContext#DECIMAL64}.</li>
 * </ul>
 */
final class MoneyMath {

    static final MathContext MC = MathContext.DECIMAL64;
    static final int MONEY_SCALE = 2;
    static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    private MoneyMath() {}

    /** Annual % rate (e.g. 5.25) → monthly fractional rate (e.g. 0.004375). */
    static BigDecimal monthlyRate(BigDecimal annualPercent) {
        return annualPercent
                .divide(BigDecimal.valueOf(100), MC)
                .divide(BigDecimal.valueOf(12), MC);
    }

    static BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }
}
