package com.lhv.loans.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface LoanJpaRepository extends JpaRepository<LoanEntity, UUID> {
}
