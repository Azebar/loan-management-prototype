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
public class EqualPrincipalScheduleCalculator implements RepaymentScheduleCalculator {

    @Override
    public ScheduleType supports() {
        return ScheduleType.EQUAL_PRINCIPAL;
    }

    @Override
    public RepaymentSchedule calculate(Loan loan) {
        BigDecimal principal = loan.amount();
        int termMonths = loan.termMonths();
        BigDecimal monthlyRate = MoneyMath.monthlyRate(loan.annualInterestRatePercent());
        BigDecimal flatPrincipal = MoneyMath.money(principal.divide(BigDecimal.valueOf(termMonths), MoneyMath.RATE_MATH));

        List<ScheduleInstallment> installments = new ArrayList<>(termMonths);
        BigDecimal balance = principal;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        LocalDate dueDate = loan.startDate();

        for (int period = 1; period <= termMonths; period++) {
            dueDate = dueDate.plusMonths(1);
            BigDecimal interest = MoneyMath.money(balance.multiply(monthlyRate, MoneyMath.RATE_MATH));
            BigDecimal principalPart = (period == termMonths) ? balance : flatPrincipal;
            BigDecimal payment = MoneyMath.money(principalPart.add(interest));
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
                .scheduleType(ScheduleType.EQUAL_PRINCIPAL)
                .installments(installments)
                .totalPrincipal(MoneyMath.money(totalPrincipal))
                .totalInterest(MoneyMath.money(totalInterest))
                .totalPaid(MoneyMath.money(totalPaid))
                .build();
    }
}
