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

    static Loan toDomain(LoanEntity entity) {
        return Loan.builder()
                .id(entity.getId())
                .borrowerName(entity.getBorrowerName())
                .type(entity.getType())
                .amount(entity.getAmount())
                .termMonths(entity.getTermMonths())
                .annualInterestRatePercent(entity.getAnnualInterestRatePercent())
                .scheduleType(entity.getScheduleType())
                .startDate(entity.getStartDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
