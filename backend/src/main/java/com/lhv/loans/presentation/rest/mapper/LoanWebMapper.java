package com.lhv.loans.presentation.rest.mapper;

import com.lhv.loans.application.command.CreateLoanCommand;
import com.lhv.loans.application.command.UpdateLoanCommand;
import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.presentation.rest.dto.CreateLoanRequest;
import com.lhv.loans.presentation.rest.dto.LoanResponse;
import com.lhv.loans.presentation.rest.dto.ScheduleResponse;
import com.lhv.loans.presentation.rest.dto.UpdateLoanRequest;

public final class LoanWebMapper {

    private LoanWebMapper() {}

    public static CreateLoanCommand toCommand(CreateLoanRequest req) {
        return new CreateLoanCommand(
                req.borrowerName(),
                req.type(),
                req.amount(),
                req.termMonths(),
                req.annualInterestRatePercent(),
                req.scheduleType(),
                req.startDate()
        );
    }

    public static UpdateLoanCommand toCommand(UpdateLoanRequest req) {
        return new UpdateLoanCommand(
                req.borrowerName(),
                req.type(),
                req.amount(),
                req.termMonths(),
                req.annualInterestRatePercent(),
                req.scheduleType(),
                req.startDate()
        );
    }

    public static LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
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

    public static ScheduleResponse toResponse(RepaymentSchedule s) {
        var installments = s.installments().stream()
                .map(i -> new ScheduleResponse.Installment(
                        i.periodNumber(),
                        i.dueDate(),
                        i.principalAmount(),
                        i.interestAmount(),
                        i.totalPayment(),
                        i.remainingBalance()))
                .toList();
        return new ScheduleResponse(
                s.loanId(),
                s.scheduleType(),
                s.totalPrincipal(),
                s.totalInterest(),
                s.totalPaid(),
                installments
        );
    }
}
