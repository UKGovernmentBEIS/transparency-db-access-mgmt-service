package com.beis.subsidy.control.accessmanagementservice.repository;

import com.beis.subsidy.control.accessmanagementservice.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * Interface for Beneficiary repository to get beneficiary details from database 
 *
 */
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

}
