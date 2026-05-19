package com.lhv.loans.infrastructure.persistence;

import com.lhv.loans.domain.model.Loan;

final class LoanEntityMapper {

    private LoanEntityMapper() {}

    static LoanEntity toEntity(Loan loan) {
        return new LoanEntity(
                loan.id(),
                loan.borrowerName(),
                loan.type(),
                loan.amount(),
                loan.termMonths(),
                loan.annualInterestRatePercent(),
                loan.scheduleType(),
                loan.startDate(),
                loan.createdAt(),
                loan.updatedAt()
        );
    }

    static Loan toDomain(LoanEntity e) {
        return new Loan(
                e.getId(),
                e.getBorrowerName(),
                e.getType(),
                e.getAmount(),
                e.getTermMonths(),
                e.getAnnualInterestRatePercent(),
                e.getScheduleType(),
                e.getStartDate(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
