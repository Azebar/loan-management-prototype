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
        return new Loan(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "Test Borrower",
                LoanType.CONSUMER,
                amount,
                months,
                annualRatePct,
                type,
                LocalDate.parse("2026-01-01"),
                now,
                now
        );
    }
}
