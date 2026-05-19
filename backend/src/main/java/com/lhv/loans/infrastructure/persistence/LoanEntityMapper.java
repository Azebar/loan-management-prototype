package com.lhv.loans.infrastructure.persistence;

import com.lhv.loans.domain.model.Loan;

final class LoanEntityMapper {

    private LoanEntityMapper() {}

    static LoanEntity toEntity(Loan loan) {
        return LoanEntity.builder()
                .id(loan.id())
                .borrowerName(loan.borrowerName())
                .type(loan.type())
                .amount(loan.amount())
                .termMonths(loan.termMonths())
                .annualInterestRatePercent(loan.annualInterestRatePercent())
                .scheduleType(loan.scheduleType())
                .startDate(loan.startDate())
                .createdAt(loan.createdAt())
                .updatedAt(loan.updatedAt())
                .build();
    }

    static Loan toDomain(LoanEntity e) {
        return Loan.builder()
                .id(e.getId())
                .borrowerName(e.getBorrowerName())
                .type(e.getType())
                .amount(e.getAmount())
                .termMonths(e.getTermMonths())
                .annualInterestRatePercent(e.getAnnualInterestRatePercent())
                .scheduleType(e.getScheduleType())
                .startDate(e.getStartDate())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
