package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class AnnuityScheduleCalculator extends AbstractRepaymentScheduleCalculator {

    @Override
    public ScheduleType supports() {
        return ScheduleType.ANNUITY;
    }

    @Override
    protected PeriodFormula formulaFor(BigDecimal principal, int termMonths, BigDecimal monthlyRate) {
        BigDecimal annuityPayment = computeAnnuity(principal, monthlyRate, termMonths);
        return (balance, isFinalPeriod) -> {
            BigDecimal interest = MoneyMath.money(balance.multiply(monthlyRate, MoneyMath.RATE_MATH));
            if (isFinalPeriod) {
                return new InstallmentParts(interest, balance);
            }
            BigDecimal principalShare = MoneyMath.money(annuityPayment.subtract(interest));
            if (principalShare.compareTo(balance) > 0) {
                principalShare = balance;
            }
            return new InstallmentParts(interest, principalShare);
        };
    }

    private static BigDecimal computeAnnuity(BigDecimal principal, BigDecimal monthlyRate, int termMonths) {
        if (monthlyRate.signum() == 0) {
            return MoneyMath.money(principal.divide(BigDecimal.valueOf(termMonths), MoneyMath.RATE_MATH));
        }
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal factor = onePlusRate.pow(termMonths, MoneyMath.RATE_MATH);
        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(factor, MoneyMath.RATE_MATH));
        return MoneyMath.money(principal.multiply(monthlyRate, MoneyMath.RATE_MATH).divide(denominator, MoneyMath.RATE_MATH));
    }
}
