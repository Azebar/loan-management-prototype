package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.domain.model.ScheduleInstallment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRepaymentScheduleCalculator implements RepaymentScheduleCalculator {

    @Override
    public final RepaymentSchedule calculate(Loan loan) {
        BigDecimal principal = loan.amount();
        int termMonths = loan.termMonths();
        BigDecimal monthlyRate = MoneyMath.monthlyRate(loan.annualInterestRatePercent());
        PeriodFormula formula = formulaFor(principal, termMonths, monthlyRate);

        List<ScheduleInstallment> installments = new ArrayList<>(termMonths);
        BigDecimal balance = principal;
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        LocalDate dueDate = loan.startDate();

        for (int period = 1; period <= termMonths; period++) {
            dueDate = dueDate.plusMonths(1);
            InstallmentParts parts = formula.compute(balance, period == termMonths);
            BigDecimal interest = parts.interest();
            BigDecimal principalShare = parts.principalShare();
            BigDecimal payment = MoneyMath.money(principalShare.add(interest));

            balance = balance.subtract(principalShare);
            totalPrincipal = totalPrincipal.add(principalShare);
            totalInterest = totalInterest.add(interest);
            totalPaid = totalPaid.add(payment);

            installments.add(ScheduleInstallment.builder()
                    .periodNumber(period)
                    .dueDate(dueDate)
                    .principalAmount(principalShare)
                    .interestAmount(interest)
                    .totalPayment(payment)
                    .remainingBalance(MoneyMath.money(balance))
                    .build());
        }

        return RepaymentSchedule.builder()
                .loanId(loan.id())
                .scheduleType(supports())
                .installments(installments)
                .totalPrincipal(MoneyMath.money(totalPrincipal))
                .totalInterest(MoneyMath.money(totalInterest))
                .totalPaid(MoneyMath.money(totalPaid))
                .build();
    }

    protected abstract PeriodFormula formulaFor(BigDecimal principal, int termMonths, BigDecimal monthlyRate);

    @FunctionalInterface
    protected interface PeriodFormula {
        InstallmentParts compute(BigDecimal balance, boolean isFinalPeriod);
    }

    protected record InstallmentParts(BigDecimal interest, BigDecimal principalShare) {}
}
