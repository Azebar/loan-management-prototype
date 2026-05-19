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
        return CreateLoanCommand.builder()
                .borrowerName(req.borrowerName())
                .type(req.type())
                .amount(req.amount())
                .termMonths(req.termMonths())
                .annualInterestRatePercent(req.annualInterestRatePercent())
                .scheduleType(req.scheduleType())
                .startDate(req.startDate())
                .build();
    }

    public static UpdateLoanCommand toCommand(UpdateLoanRequest req) {
        return UpdateLoanCommand.builder()
                .borrowerName(req.borrowerName())
                .type(req.type())
                .amount(req.amount())
                .termMonths(req.termMonths())
                .annualInterestRatePercent(req.annualInterestRatePercent())
                .scheduleType(req.scheduleType())
                .startDate(req.startDate())
                .build();
    }

    public static LoanResponse toResponse(Loan loan) {
        return LoanResponse.builder()
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

    public static ScheduleResponse toResponse(RepaymentSchedule s) {
        var installments = s.installments().stream()
                .map(i -> ScheduleResponse.Installment.builder()
                        .periodNumber(i.periodNumber())
                        .dueDate(i.dueDate())
                        .principalAmount(i.principalAmount())
                        .interestAmount(i.interestAmount())
                        .totalPayment(i.totalPayment())
                        .remainingBalance(i.remainingBalance())
                        .build())
                .toList();
        return ScheduleResponse.builder()
                .loanId(s.loanId())
                .scheduleType(s.scheduleType())
                .totalPrincipal(s.totalPrincipal())
                .totalInterest(s.totalInterest())
                .totalPaid(s.totalPaid())
                .installments(installments)
                .build();
    }
}
