package com.lhv.loans.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RepaymentSchedule(
        UUID loanId,
        ScheduleType scheduleType,
        List<ScheduleInstallment> installments,
        BigDecimal totalPrincipal,
        BigDecimal totalInterest,
        BigDecimal totalPaid
) {

    public RepaymentSchedule {
        installments = List.copyOf(installments);
    }
}
