package com.lhv.loans.infrastructure.persistence;

import com.lhv.loans.domain.model.Loan;
import com.lhv.loans.domain.port.LoanRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

/**
 * Adapter that satisfies the domain port using Spring Data JPA. The domain
 * never references this class directly.
 */
@Repository
public class LoanRepositoryAdapter implements LoanRepository {

    private final LoanJpaRepository jpa;

    public LoanRepositoryAdapter(LoanJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Loan save(Loan loan) {
        var saved = jpa.save(LoanEntityMapper.toEntity(loan));
        return LoanEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Loan> findById(UUID id) {
        return jpa.findById(id).map(LoanEntityMapper::toDomain);
    }

    @Override
    public List<Loan> findAll() {
        return jpa.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(LoanEntityMapper::toDomain)
                .toList();
    }

    @Override
    public boolean deleteById(UUID id) {
        if (!jpa.existsById(id)) {
            return false;
        }
        jpa.deleteById(id);
        return true;
    }
}
