package com.beis.subsidy.control.accessmanagementservice.repository;

import com.beis.subsidy.control.accessmanagementservice.model.GrantingAuthority;
import com.beis.subsidy.control.accessmanagementservice.model.SubsidyMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 
 * Interface for Subsidy Measure repository to get subsidy measure details from database 
 *
 */
public interface SubsidyMeasureRepository extends JpaRepository<SubsidyMeasure, String>, JpaSpecificationExecutor<SubsidyMeasure> {

}
