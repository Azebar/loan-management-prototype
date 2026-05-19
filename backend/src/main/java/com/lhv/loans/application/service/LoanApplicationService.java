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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates the loan-management use cases on top of the domain model. This
 * layer holds no business rules — those live in the domain — only workflow.
 */
@Service
@Transactional
public class LoanApplicationService {

    private final LoanRepository loans;
    private final RepaymentScheduleService schedules;
    private final Clock clock;

    public LoanApplicationService(
            LoanRepository loans,
            RepaymentScheduleService schedules,
            Clock clock) {
        this.loans = loans;
        this.schedules = schedules;
        this.clock = clock;
    }

    public Loan create(CreateLoanCommand cmd) {
        var now = clock.instant();
        var loan = new Loan(
                UUID.randomUUID(),
                cmd.borrowerName(),
                cmd.type(),
                cmd.amount(),
                cmd.termMonths(),
                cmd.annualInterestRatePercent(),
                cmd.scheduleType(),
                cmd.startDate(),
                now,
                now
        );
        return loans.save(loan);
    }

    public Loan update(UUID id, UpdateLoanCommand cmd) {
        var existing = loans.findById(id).orElseThrow(() -> new LoanNotFoundException(id));
        var updated = existing.withChanges(
                cmd.borrowerName(),
                cmd.type(),
                cmd.amount(),
                cmd.termMonths(),
                cmd.annualInterestRatePercent(),
                cmd.scheduleType(),
                cmd.startDate(),
                clock.instant()
        );
        return loans.save(updated);
    }

    @Transactional(readOnly = true)
    public Loan get(UUID id) {
        return loans.findById(id).orElseThrow(() -> new LoanNotFoundException(id));
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
        var loan = get(id);
        return schedules.scheduleFor(loan);
    }
}
