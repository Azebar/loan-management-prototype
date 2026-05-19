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

/**
 * Bullet loan: borrower pays interest only every period; the entire principal
 * is due in the final installment.
 */
@Component
public class BulletScheduleCalculator implements RepaymentScheduleCalculator {

    @Override
    public ScheduleType supports() {
        return ScheduleType.BULLET;
    }

    @Override
    public RepaymentSchedule calculate(Loan loan) {
        BigDecimal principal = loan.amount();
        int n = loan.termMonths();
        BigDecimal r = MoneyMath.monthlyRate(loan.annualInterestRatePercent());
        BigDecimal periodicInterest = MoneyMath.money(principal.multiply(r, MoneyMath.MC));

        List<ScheduleInstallment> installments = new ArrayList<>(n);
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal balance = principal;
        LocalDate dueDate = loan.startDate();

        for (int i = 1; i <= n; i++) {
            dueDate = dueDate.plusMonths(1);
            BigDecimal principalPart = (i == n) ? balance : BigDecimal.ZERO.setScale(MoneyMath.MONEY_SCALE);
            BigDecimal interest = periodicInterest;
            BigDecimal payment = MoneyMath.money(principalPart.add(interest));
            balance = balance.subtract(principalPart);

            totalInterest = totalInterest.add(interest);
            totalPaid = totalPaid.add(payment);

            installments.add(new ScheduleInstallment(
                    i,
                    dueDate,
                    principalPart,
                    interest,
                    payment,
                    MoneyMath.money(balance)
            ));
        }

        return new RepaymentSchedule(
                loan.id(),
                ScheduleType.BULLET,
                installments,
                MoneyMath.money(principal),
                MoneyMath.money(totalInterest),
                MoneyMath.money(totalPaid)
        );
    }
}
