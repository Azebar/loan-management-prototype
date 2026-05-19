package com.lhv.loans.domain.service;

import static com.lhv.loans.domain.service.CalculatorTestFixtures.loan;
import static org.assertj.core.api.Assertions.assertThat;

import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class EqualPrincipalScheduleCalculatorTest {

    private final EqualPrincipalScheduleCalculator calculator = new EqualPrincipalScheduleCalculator();

    @Test
    void principal_share_is_constant_except_for_final_rounding_adjust() {
        var schedule = calculator.calculate(loan(new BigDecimal("12000.00"), 12, new BigDecimal("6.00"), ScheduleType.EQUAL_PRINCIPAL));

        for (int period = 0; period < 11; period++) {
            assertThat(schedule.installments().get(period).principalAmount()).isEqualByComparingTo("1000.00");
        }

        assertThat(schedule.installments().getLast().remainingBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void interest_share_strictly_decreases() {
        var schedule = calculator.calculate(loan(new BigDecimal("50000.00"), 24, new BigDecimal("8.00"), ScheduleType.EQUAL_PRINCIPAL));

        for (int period = 1; period < schedule.installments().size(); period++) {
            var previous = schedule.installments().get(period - 1).interestAmount();
            var current = schedule.installments().get(period).interestAmount();
            assertThat(current).isLessThan(previous);
        }
    }

    @Test
    void totals_are_self_consistent() {
        var amount = new BigDecimal("75500.00");
        var schedule = calculator.calculate(loan(amount, 36, new BigDecimal("7.25"), ScheduleType.EQUAL_PRINCIPAL));

        assertThat(schedule.totalPrincipal()).isEqualByComparingTo(amount);
        assertThat(schedule.totalPaid()).isEqualByComparingTo(schedule.totalPrincipal().add(schedule.totalInterest()));
    }
}
