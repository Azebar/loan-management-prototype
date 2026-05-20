package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class EqualPrincipalScheduleCalculator extends AbstractRepaymentScheduleCalculator {

    @Override
    public ScheduleType supports() {
        return ScheduleType.EQUAL_PRINCIPAL;
    }

    @Override
    protected PeriodFormula formulaFor(BigDecimal principal, int termMonths, BigDecimal monthlyRate) {
        BigDecimal flatPrincipal = MoneyMath.money(principal.divide(BigDecimal.valueOf(termMonths), MoneyMath.RATE_MATH));
        return (balance, isFinalPeriod) -> {
            BigDecimal interest = MoneyMath.money(balance.multiply(monthlyRate, MoneyMath.RATE_MATH));
            BigDecimal principalShare = isFinalPeriod ? balance : flatPrincipal;
            return new InstallmentParts(interest, principalShare);
        };
    }
}
