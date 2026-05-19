package com.lhv.loans.infrastructure.persistence;

import com.lhv.loans.domain.model.LoanType;
import com.lhv.loans.domain.model.ScheduleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loans")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class LoanEntity {

    @Id
    private UUID id;

    @Column(name = "borrower_name", nullable = false)
    private String borrowerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LoanType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "term_months", nullable = false)
    private int termMonths;

    @Column(name = "annual_interest_rate_percent", nullable = false, precision = 7, scale = 4)
    private BigDecimal annualInterestRatePercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 32)
    private ScheduleType scheduleType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
