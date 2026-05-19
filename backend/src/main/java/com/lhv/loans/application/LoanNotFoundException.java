package com.lhv.loans.application;

import java.util.UUID;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(UUID loanId) {
        super("Loan not found: " + loanId);
    }
}
