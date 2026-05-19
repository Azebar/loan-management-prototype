package com.lhv.loans.application;

import java.util.UUID;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(UUID id) {
        super("Loan not found: " + id);
    }
}
