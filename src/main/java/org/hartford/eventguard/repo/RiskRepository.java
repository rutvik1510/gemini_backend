package org.hartford.eventguard.repo;

import org.hartford.eventguard.entity.Risk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskRepository extends JpaRepository<Risk, Long> {
}
