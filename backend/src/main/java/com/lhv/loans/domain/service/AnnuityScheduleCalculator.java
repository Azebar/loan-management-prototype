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
        int n = loan.termMonths();
        BigDecimal r = MoneyMath.monthlyRate(loan.annualInterestRatePercent());

        BigDecimal annuityPayment = computeAnnuity(principal, r, n);

        List<ScheduleInstallment> installments = new ArrayList<>(n);
        BigDecimal balance = principal;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        LocalDate dueDate = loan.startDate();

        for (int i = 1; i <= n; i++) {
            dueDate = dueDate.plusMonths(1);
            BigDecimal interest = MoneyMath.money(balance.multiply(r, MoneyMath.MC));
            BigDecimal principalPart;
            BigDecimal payment;

            if (i == n) {
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
                    .periodNumber(i)
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

    private static BigDecimal computeAnnuity(BigDecimal principal, BigDecimal r, int n) {
        if (r.signum() == 0) {
            return MoneyMath.money(principal.divide(BigDecimal.valueOf(n), MoneyMath.MC));
        }
        BigDecimal onePlusR = BigDecimal.ONE.add(r);
        BigDecimal factor = onePlusR.pow(n, MoneyMath.MC);
        BigDecimal denom = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(factor, MoneyMath.MC));
        return MoneyMath.money(principal.multiply(r, MoneyMath.MC).divide(denom, MoneyMath.MC));
    }
}
