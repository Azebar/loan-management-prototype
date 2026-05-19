package com.lhv.loans.presentation.rest.dto;

import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ScheduleResponse(
        UUID loanId,
        ScheduleType scheduleType,
        BigDecimal totalPrincipal,
        BigDecimal totalInterest,
        BigDecimal totalPaid,
        List<Installment> installments
) {

    @Builder
    public record Installment(
            int periodNumber,
            LocalDate dueDate,
            BigDecimal principalAmount,
            BigDecimal interestAmount,
            BigDecimal totalPayment,
            BigDecimal remainingBalance
    ) {
    }
}
