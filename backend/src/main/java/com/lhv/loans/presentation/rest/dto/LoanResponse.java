package com.lhv.loans.presentation.rest.dto;

import com.lhv.loans.domain.model.LoanType;
import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record LoanResponse(
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
}
