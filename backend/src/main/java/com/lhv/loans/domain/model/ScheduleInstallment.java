package com.lhv.loans.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ScheduleInstallment(
        int periodNumber,
        LocalDate dueDate,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        BigDecimal totalPayment,
        BigDecimal remainingBalance
) {
}
