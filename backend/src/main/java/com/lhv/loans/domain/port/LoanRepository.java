package com.lhv.loans.domain.port;

import com.lhv.loans.domain.model.Loan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(UUID loanId);

    List<Loan> findAll();

    boolean deleteById(UUID loanId);
}
