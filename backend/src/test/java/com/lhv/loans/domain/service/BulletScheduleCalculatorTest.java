package com.lhv.loans.domain.service;

import static com.lhv.loans.domain.service.CalculatorTestFixtures.loan;
import static org.assertj.core.api.Assertions.assertThat;

import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BulletScheduleCalculatorTest {

    private final BulletScheduleCalculator calculator = new BulletScheduleCalculator();

    @Test
    void interest_only_payments_then_balloon_at_end() {
        var amount = new BigDecimal("100000.00");
        var schedule = calculator.calculate(loan(amount, 6, new BigDecimal("12.00"), ScheduleType.BULLET));

        for (int period = 0; period < 5; period++) {
            assertThat(schedule.installments().get(period).principalAmount()).isEqualByComparingTo("0.00");
            assertThat(schedule.installments().get(period).interestAmount()).isEqualByComparingTo("1000.00");
            assertThat(schedule.installments().get(period).totalPayment()).isEqualByComparingTo("1000.00");
        }

        var lastInstallment = schedule.installments().getLast();
        assertThat(lastInstallment.principalAmount()).isEqualByComparingTo(amount);
        assertThat(lastInstallment.interestAmount()).isEqualByComparingTo("1000.00");
        assertThat(lastInstallment.totalPayment()).isEqualByComparingTo("101000.00");
        assertThat(lastInstallment.remainingBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void totals_are_self_consistent() {
        var amount = new BigDecimal("250000.00");
        var schedule = calculator.calculate(loan(amount, 60, new BigDecimal("3.75"), ScheduleType.BULLET));

        assertThat(schedule.totalPrincipal()).isEqualByComparingTo(amount);
        assertThat(schedule.totalPaid()).isEqualByComparingTo(schedule.totalPrincipal().add(schedule.totalInterest()));
    }
}
