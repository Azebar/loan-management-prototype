package com.lhv.loans.application.command;

import com.lhv.loans.domain.model.LoanType;
import com.lhv.loans.domain.model.ScheduleType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UpdateLoanCommand(
        String borrowerName,
        LoanType type,
        BigDecimal amount,
        int termMonths,
        BigDecimal annualInterestRatePercent,
        ScheduleType scheduleType,
        LocalDate startDate
) {
}
