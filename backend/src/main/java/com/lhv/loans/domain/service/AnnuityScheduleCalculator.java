package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.domain.model.ScheduleInstallment;
import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AnnuityScheduleCalculator implements RepaymentScheduleCalculator {

    @Override
    public ScheduleType supports() {
        return ScheduleType.ANNUITY;
    }

    @Override
    public RepaymentSchedule calculate(Loan loan) {
        BigDecimal principal = loan.amount();
        int termMonths = loan.termMonths();
        BigDecimal monthlyRate = MoneyMath.monthlyRate(loan.annualInterestRatePercent());

        BigDecimal annuityPayment = computeAnnuity(principal, monthlyRate, termMonths);

        List<ScheduleInstallment> installments = new ArrayList<>(termMonths);
        BigDecimal balance = principal;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        LocalDate dueDate = loan.startDate();

        for (int period = 1; period <= termMonths; period++) {
            dueDate = dueDate.plusMonths(1);
            BigDecimal interest = MoneyMath.money(balance.multiply(monthlyRate, MoneyMath.RATE_MATH));
            BigDecimal principalPart;
            BigDecimal payment;

            if (period == termMonths) {
                principalPart = balance;
                payment = MoneyMath.money(principalPart.add(interest));
            } else {
                principalPart = MoneyMath.money(annuityPayment.subtract(interest));
                if (principalPart.compareTo(balance) > 0) {
                    principalPart = balance;
                }
                payment = MoneyMath.money(principalPart.add(interest));
            }

            balance = balance.subtract(principalPart);
            totalPrincipal = totalPrincipal.add(principalPart);
            totalInterest = totalInterest.add(interest);
            totalPaid = totalPaid.add(payment);

            installments.add(ScheduleInstallment.builder()
                    .periodNumber(period)
                    .dueDate(dueDate)
                    .principalAmount(principalPart)
                    .interestAmount(interest)
                    .totalPayment(payment)
                    .remainingBalance(MoneyMath.money(balance))
                    .build());
        }

        return RepaymentSchedule.builder()
                .loanId(loan.id())
                .scheduleType(ScheduleType.ANNUITY)
                .installments(installments)
                .totalPrincipal(MoneyMath.money(totalPrincipal))
                .totalInterest(MoneyMath.money(totalInterest))
                .totalPaid(MoneyMath.money(totalPaid))
                .build();
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
