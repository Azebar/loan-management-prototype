package com.lhv.loans.presentation.rest.mapper;

import com.lhv.loans.application.command.CreateLoanCommand;
import com.lhv.loans.application.command.UpdateLoanCommand;
import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.presentation.rest.dto.CreateLoanRequest;
import com.lhv.loans.presentation.rest.dto.LoanResponse;
import com.lhv.loans.presentation.rest.dto.ScheduleResponse;
import com.lhv.loans.presentation.rest.dto.UpdateLoanRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoanWebMapper {

    public CreateLoanCommand toCommand(CreateLoanRequest request) {
        return CreateLoanCommand.builder()
                .borrowerName(request.borrowerName())
                .type(request.type())
                .amount(request.amount())
                .termMonths(request.termMonths())
                .annualInterestRatePercent(request.annualInterestRatePercent())
                .scheduleType(request.scheduleType())
                .startDate(request.startDate())
                .build();
    }

    public UpdateLoanCommand toCommand(UpdateLoanRequest request) {
        return UpdateLoanCommand.builder()
                .borrowerName(request.borrowerName())
                .type(request.type())
                .amount(request.amount())
                .termMonths(request.termMonths())
                .annualInterestRatePercent(request.annualInterestRatePercent())
                .scheduleType(request.scheduleType())
                .startDate(request.startDate())
                .build();
    }

    public LoanResponse toResponse(Loan loan) {
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

    public ScheduleResponse toResponse(RepaymentSchedule schedule) {
        var installments = schedule.installments().stream()
                .map(installment -> ScheduleResponse.Installment.builder()
                        .periodNumber(installment.periodNumber())
                        .dueDate(installment.dueDate())
                        .principalAmount(installment.principalAmount())
                        .interestAmount(installment.interestAmount())
                        .totalPayment(installment.totalPayment())
                        .remainingBalance(installment.remainingBalance())
                        .build())
                .toList();
        return ScheduleResponse.builder()
                .loanId(schedule.loanId())
                .scheduleType(schedule.scheduleType())
                .totalPrincipal(schedule.totalPrincipal())
                .totalInterest(schedule.totalInterest())
                .totalPaid(schedule.totalPaid())
                .installments(installments)
                .build();
    }
}
