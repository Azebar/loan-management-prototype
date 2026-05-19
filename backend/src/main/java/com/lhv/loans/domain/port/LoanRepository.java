package com.lhv.loans.domain.port;

import com.lhv.loans.domain.model.Loan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port owned by the domain. Infrastructure (JPA) implements it; the
 * domain never depends on the implementation.
 */
public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(UUID id);

    List<Loan> findAll();

    boolean deleteById(UUID id);
}
