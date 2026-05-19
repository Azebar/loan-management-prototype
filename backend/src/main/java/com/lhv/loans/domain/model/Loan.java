package com.lhv.loans.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for a loan. Pure domain model — no JPA, no Spring.
 *
 * <p>Money is held as {@link BigDecimal} with scale 2 (cents). The annual interest
 * rate is a percentage value (e.g. {@code 5.25} for 5.25%), not a fraction.
 */
public record Loan(
        UUID id,
        String borrowerName,
        LoanType type,
        BigDecimal amount,
        int termMonths,
        BigDecimal annualInterestRatePercent,
        ScheduleType scheduleType,
        LocalDate startDate,
        Instant createdAt,
        Instant updatedAt
) {

    public Loan {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(scheduleType, "scheduleType");
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(updatedAt, "updatedAt");
        if (borrowerName == null || borrowerName.isBlank()) {
            throw new IllegalArgumentException("borrowerName must not be blank");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (termMonths <= 0 || termMonths > 600) {
            throw new IllegalArgumentException("termMonths must be in 1..600");
        }
        if (annualInterestRatePercent == null || annualInterestRatePercent.signum() < 0) {
            throw new IllegalArgumentException("annualInterestRatePercent must be >= 0");
        }
    }

    public Loan withChanges(
            String borrowerName,
            LoanType type,
            BigDecimal amount,
            int termMonths,
            BigDecimal annualInterestRatePercent,
            ScheduleType scheduleType,
            LocalDate startDate,
            Instant updatedAt
    ) {
        return new Loan(
                this.id,
                borrowerName,
                type,
                amount,
                termMonths,
                annualInterestRatePercent,
                scheduleType,
                startDate,
                this.createdAt,
                updatedAt
        );
    }
}
