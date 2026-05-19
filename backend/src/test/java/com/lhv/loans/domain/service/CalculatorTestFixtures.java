package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.LoanType;
import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

final class CalculatorTestFixtures {

    private CalculatorTestFixtures() {}

    static Loan loan(BigDecimal amount, int months, BigDecimal annualRatePct, ScheduleType type) {
        var now = Instant.parse("2026-01-01T00:00:00Z");
        return Loan.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .borrowerName("Test Borrower")
                .type(LoanType.CONSUMER)
                .amount(amount)
                .termMonths(months)
                .annualInterestRatePercent(annualRatePct)
                .scheduleType(type)
                .startDate(LocalDate.parse("2026-01-01"))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
