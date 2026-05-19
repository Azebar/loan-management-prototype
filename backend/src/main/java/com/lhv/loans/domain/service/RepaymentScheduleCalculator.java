package com.lhv.loans.domain.service;

import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.model.RepaymentSchedule;
import com.lhv.loans.domain.model.ScheduleType;

public interface RepaymentScheduleCalculator {

    ScheduleType supports();

    RepaymentSchedule calculate(Loan loan);
}
