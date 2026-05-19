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

    public Loan create(CreateLoanCommand cmd) {
        var now = clock.instant();
        var loan = Loan.builder()
                .id(UUID.randomUUID())
                .borrowerName(cmd.borrowerName())
                .type(cmd.type())
                .amount(cmd.amount())
                .termMonths(cmd.termMonths())
                .annualInterestRatePercent(cmd.annualInterestRatePercent())
                .scheduleType(cmd.scheduleType())
                .startDate(cmd.startDate())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return loans.save(loan);
    }

    public Loan update(UUID id, UpdateLoanCommand cmd) {
        var existing = findOrThrow(id);
        var updated = existing.toBuilder()
                .borrowerName(cmd.borrowerName())
                .type(cmd.type())
                .amount(cmd.amount())
                .termMonths(cmd.termMonths())
                .annualInterestRatePercent(cmd.annualInterestRatePercent())
                .scheduleType(cmd.scheduleType())
                .startDate(cmd.startDate())
                .updatedAt(clock.instant())
                .build();
        return loans.save(updated);
    }

    @Transactional(readOnly = true)
    public Loan get(UUID id) {
        return findOrThrow(id);
    }

    @Transactional(readOnly = true)
    public List<Loan> list() {
        return loans.findAll();
    }

    public void delete(UUID id) {
        if (!loans.deleteById(id)) {
            throw new LoanNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    public RepaymentSchedule schedule(UUID id) {
        return schedules.scheduleFor(findOrThrow(id));
    }

    private Loan findOrThrow(UUID id) {
        return loans.findById(id).orElseThrow(() -> new LoanNotFoundException(id));
    }
}
