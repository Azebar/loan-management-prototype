package com.lhv.loans.presentation.rest.dto;

import com.lhv.loans.domain.model.LoanType;
import com.lhv.loans.domain.model.ScheduleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UpdateLoanRequest(
        @NotBlank @Size(max = 200) String borrowerName,
        @NotNull LoanType type,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
        @Min(1) @Max(600) int termMonths,
        @NotNull @PositiveOrZero @Digits(integer = 3, fraction = 4) BigDecimal annualInterestRatePercent,
        @NotNull ScheduleType scheduleType,
        @NotNull LocalDate startDate
) {
}
