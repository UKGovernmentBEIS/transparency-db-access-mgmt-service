package com.beis.subsidy.control.accessmanagementservice.repository;

import com.beis.subsidy.control.accessmanagementservice.model.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Interface for Award repository to get award details from database 
 *
 */
public interface AwardRepository extends JpaRepository<Award, Long>, JpaSpecificationExecutor<Award> {

    Award findByAwardNumber(Long awardNumber);
}
