package com.lhv.loans.domain.service;

import static com.lhv.loans.domain.service.CalculatorTestFixtures.loan;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.lhv.loans.domain.model.ScheduleInstallment;
import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AnnuityScheduleCalculatorTest {

    private final AnnuityScheduleCalculator calculator = new AnnuityScheduleCalculator();

    @Test
    void textbook_example_10000_at_12pct_for_12_months() {
        var schedule = calculator.calculate(loan(new BigDecimal("10000.00"), 12, new BigDecimal("12.00"), ScheduleType.ANNUITY));

        assertThat(schedule.installments()).hasSize(12);
        assertThat(schedule.installments().getFirst().totalPayment())
                .isCloseTo(new BigDecimal("888.49"), within(new BigDecimal("0.05")));
        assertThat(schedule.totalPrincipal()).isEqualByComparingTo("10000.00");
        assertThat(schedule.totalInterest())
                .isCloseTo(new BigDecimal("661.85"), within(new BigDecimal("0.10")));
    }

    @Test
    void final_balance_is_zero_and_principal_sums_to_amount() {
        var amount = new BigDecimal("57321.10");
        var schedule = calculator.calculate(loan(amount, 60, new BigDecimal("5.75"), ScheduleType.ANNUITY));

        assertThat(schedule.installments().getLast().remainingBalance()).isEqualByComparingTo("0.00");

        var sumPrincipal = schedule.installments().stream()
                .map(ScheduleInstallment::principalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(sumPrincipal).isEqualByComparingTo(amount);
    }

    @Test
    void zero_interest_means_constant_principal_only_payments() {
        var schedule = calculator.calculate(loan(new BigDecimal("1200.00"), 12, BigDecimal.ZERO, ScheduleType.ANNUITY));

        assertThat(schedule.totalInterest()).isEqualByComparingTo("0.00");
        schedule.installments().forEach(installment ->
                assertThat(installment.totalPayment()).isEqualByComparingTo("100.00"));
    }

    @Test
    void payments_total_equals_principal_plus_interest() {
        var schedule = calculator.calculate(loan(new BigDecimal("250000.00"), 240, new BigDecimal("4.25"), ScheduleType.ANNUITY));

        assertThat(schedule.totalPaid()).isEqualByComparingTo(schedule.totalPrincipal().add(schedule.totalInterest()));
    }
}
