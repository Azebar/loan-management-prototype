package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.domain.model.ScheduleType;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Dispatches repayment schedule generation to the calculator that supports the
 * loan's schedule type. Adding a new schedule type means registering a new
 * {@link RepaymentScheduleCalculator} bean — no changes here.
 */
@Service
public class RepaymentScheduleService {

    private final Map<ScheduleType, RepaymentScheduleCalculator> calculators;

    public RepaymentScheduleService(List<RepaymentScheduleCalculator> calculators) {
        this.calculators = calculators.stream()
                .collect(Collectors.toUnmodifiableMap(
                        RepaymentScheduleCalculator::supports,
                        Function.identity()));
    }

    public RepaymentSchedule scheduleFor(Loan loan) {
        var calculator = calculators.get(loan.scheduleType());
        if (calculator == null) {
            throw new IllegalStateException("No calculator registered for " + loan.scheduleType());
        }
        return calculator.calculate(loan);
    }
}
