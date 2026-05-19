package com.lhv.loans.domain.service;

import static com.lhv.loans.domain.service.CalculatorTestFixtures.loan;
import static org.assertj.core.api.Assertions.assertThat;

import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class EqualPrincipalScheduleCalculatorTest {

    private final EqualPrincipalScheduleCalculator calc = new EqualPrincipalScheduleCalculator();

    @Test
    void principal_share_is_constant_except_for_final_rounding_adjust() {
        var s = calc.calculate(loan(new BigDecimal("12000.00"), 12, new BigDecimal("6.00"), ScheduleType.EQUAL_PRINCIPAL));

        // First 11 are flat 1000.00; final one absorbs any residue.
        for (int i = 0; i < 11; i++) {
            assertThat(s.installments().get(i).principalAmount()).isEqualByComparingTo("1000.00");
        }

        assertThat(s.installments().getLast().remainingBalance()).isEqualByComparingTo("0.00");
    }

    @Test
    void interest_share_strictly_decreases() {
        var s = calc.calculate(loan(new BigDecimal("50000.00"), 24, new BigDecimal("8.00"), ScheduleType.EQUAL_PRINCIPAL));

        for (int i = 1; i < s.installments().size(); i++) {
            var prev = s.installments().get(i - 1).interestAmount();
            var curr = s.installments().get(i).interestAmount();
            assertThat(curr).isLessThan(prev);
        }
    }

    @Test
    void totals_are_self_consistent() {
        var amount = new BigDecimal("75500.00");
        var s = calc.calculate(loan(amount, 36, new BigDecimal("7.25"), ScheduleType.EQUAL_PRINCIPAL));

        assertThat(s.totalPrincipal()).isEqualByComparingTo(amount);
        assertThat(s.totalPaid()).isEqualByComparingTo(s.totalPrincipal().add(s.totalInterest()));
    }
}
