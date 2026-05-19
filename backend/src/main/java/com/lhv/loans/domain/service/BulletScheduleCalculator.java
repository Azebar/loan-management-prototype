package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.domain.model.ScheduleInstallment;
import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BulletScheduleCalculator implements RepaymentScheduleCalculator {

    @Override
    public ScheduleType supports() {
        return ScheduleType.BULLET;
    }

    @Override
    public RepaymentSchedule calculate(Loan loan) {
        BigDecimal principal = loan.amount();
        int termMonths = loan.termMonths();
        BigDecimal monthlyRate = MoneyMath.monthlyRate(loan.annualInterestRatePercent());
        BigDecimal periodicInterest = MoneyMath.money(principal.multiply(monthlyRate, MoneyMath.RATE_MATH));

        List<ScheduleInstallment> installments = new ArrayList<>(termMonths);
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal balance = principal;
        LocalDate dueDate = loan.startDate();

        for (int period = 1; period <= termMonths; period++) {
            dueDate = dueDate.plusMonths(1);
            BigDecimal principalPart = (period == termMonths) ? balance : BigDecimal.ZERO.setScale(MoneyMath.MONEY_SCALE, RoundingMode.HALF_UP);
            BigDecimal payment = MoneyMath.money(principalPart.add(periodicInterest));
            balance = balance.subtract(principalPart);

            totalInterest = totalInterest.add(periodicInterest);
            totalPaid = totalPaid.add(payment);

            installments.add(ScheduleInstallment.builder()
                    .periodNumber(period)
                    .dueDate(dueDate)
                    .principalAmount(principalPart)
                    .interestAmount(periodicInterest)
                    .totalPayment(payment)
                    .remainingBalance(MoneyMath.money(balance))
                    .build());
        }

        return RepaymentSchedule.builder()
                .loanId(loan.id())
                .scheduleType(ScheduleType.BULLET)
                .installments(installments)
                .totalPrincipal(MoneyMath.money(principal))
                .totalInterest(MoneyMath.money(totalInterest))
                .totalPaid(MoneyMath.money(totalPaid))
                .build();
    }
}
