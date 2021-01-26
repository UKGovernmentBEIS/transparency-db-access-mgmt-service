package com.beis.subsidy.control.accessmanagementservice.repository;

import com.beis.subsidy.control.accessmanagementservice.model.Award;
import com.beis.subsidy.control.accessmanagementservice.model.GrantingAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 
 *  * Interface for Granting Authority repository to get granting authority details from database 
 */
public interface GrantingAuthorityRepository extends JpaRepository<GrantingAuthority, Long>, JpaSpecificationExecutor<GrantingAuthority> {
	
	/**
	 * To get Granting authority details based on granting authority name 
	 * @param Name - Granting authority name
	 * @return GrantingAuthority - Object of GrantingAuthority
	 */
	GrantingAuthority findByGrantingAuthorityName(String Name);
	

}
