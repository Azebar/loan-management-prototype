package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class BulletScheduleCalculator extends AbstractRepaymentScheduleCalculator {

    @Override
    public ScheduleType supports() {
        return ScheduleType.BULLET;
    }

    @Override
    protected PeriodFormula formulaFor(BigDecimal principal, int termMonths, BigDecimal monthlyRate) {
        BigDecimal periodicInterest = MoneyMath.money(principal.multiply(monthlyRate, MoneyMath.RATE_MATH));
        return (balance, isFinalPeriod) -> {
            BigDecimal principalShare = isFinalPeriod ? balance : BigDecimal.ZERO;
            return new InstallmentParts(periodicInterest, principalShare);
        };
    }
}
