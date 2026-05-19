package com.lhv.loans.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
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
}
