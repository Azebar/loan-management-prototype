package com.lhv.loans.domain.service;

import static com.lhv.loans.domain.service.CalculatorTestFixtures.loan;
import static org.assertj.core.api.Assertions.assertThat;

import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BulletScheduleCalculatorTest {

    private final BulletScheduleCalculator calc = new BulletScheduleCalculator();

    @Test
    void interest_only_payments_then_balloon_at_end() {
        var amount = new BigDecimal("100000.00");
        var s = calc.calculate(loan(amount, 6, new BigDecimal("12.00"), ScheduleType.BULLET));

        for (int i = 0; i < 5; i++) {
            assertThat(s.installments().get(i).principalAmount()).isEqualByComparingTo("0.00");
            assertThat(s.installments().get(i).interestAmount()).isEqualByComparingTo("1000.00");
            assertThat(s.installments().get(i).totalPayment()).isEqualByComparingTo("1000.00");
        }

        var last = s.installments().getLast();
        assertThat(last.principalAmount()).isEqualByComparingTo(amount);
        assertThat(last.interestAmount()).isEqualByComparingTo("1000.00");
        assertThat(last.totalPayment()).isEqualByComparingTo("101000.00");
        assertThat(last.remainingBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void totals_are_self_consistent() {
        var amount = new BigDecimal("250000.00");
        var s = calc.calculate(loan(amount, 60, new BigDecimal("3.75"), ScheduleType.BULLET));

        assertThat(s.totalPrincipal()).isEqualByComparingTo(amount);
        assertThat(s.totalPaid()).isEqualByComparingTo(s.totalPrincipal().add(s.totalInterest()));
    }
}
