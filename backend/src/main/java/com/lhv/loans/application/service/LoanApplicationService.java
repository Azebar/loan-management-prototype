package com.lhv.loans.application.service;

import com.lhv.loans.application.LoanNotFoundException;
import com.lhv.loans.application.command.CreateLoanCommand;
import com.lhv.loans.application.command.UpdateLoanCommand;
import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.domain.port.LoanRepository;
import com.lhv.loans.domain.service.RepaymentScheduleService;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanRepository loans;
    private final RepaymentScheduleService schedules;
    private final Clock clock;

    public Loan create(CreateLoanCommand command) {
        var now = clock.instant();
        var loan = Loan.builder()
                .id(UUID.randomUUID())
                .borrowerName(command.borrowerName())
                .type(command.type())
                .amount(command.amount())
                .termMonths(command.termMonths())
                .annualInterestRatePercent(command.annualInterestRatePercent())
                .scheduleType(command.scheduleType())
                .startDate(command.startDate())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return loans.save(loan);
    }

    public Loan update(UUID loanId, UpdateLoanCommand command) {
        var existing = findOrThrow(loanId);
        var updated = existing.toBuilder()
                .borrowerName(command.borrowerName())
                .type(command.type())
                .amount(command.amount())
                .termMonths(command.termMonths())
                .annualInterestRatePercent(command.annualInterestRatePercent())
                .scheduleType(command.scheduleType())
                .startDate(command.startDate())
                .updatedAt(clock.instant())
                .build();
        return loans.save(updated);
    }

    @Transactional(readOnly = true)
    public Loan get(UUID loanId) {
        return findOrThrow(loanId);
    }

    @Transactional(readOnly = true)
    public List<Loan> list() {
        return loans.findAll();
    }

    public void delete(UUID loanId) {
        if (!loans.deleteById(loanId)) {
            throw new LoanNotFoundException(loanId);
        }
    }

    @Transactional(readOnly = true)
    public RepaymentSchedule schedule(UUID loanId) {
        return schedules.scheduleFor(findOrThrow(loanId));
    }

    private Loan findOrThrow(UUID loanId) {
        return loans.findById(loanId).orElseThrow(() -> new LoanNotFoundException(loanId));
    }
}
