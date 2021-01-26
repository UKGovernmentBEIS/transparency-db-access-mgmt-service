package com.beis.subsidy.control.accessmanagementservice.model;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 
 * OPTION Entity Class
 *
 */
@Entity(name = "OPTION")
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "option_seq", sequenceName = "option_seq",
		allocationSize = 1)
@Setter
@Getter
public class Option {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_seq")
	@Column(name="OPTION_ID")
	private Long optionId;

	@Column(name = "OPTION_NAME")
	private String optionName;

	@Column(name = "OPTION_VALUE")
	@NaturalId
	private String optionValue;
	
	@Column(name = "APPROVED_BY")
	private String approvedBy;
	
	@Column(name = "STATUS")
	private String status;
	
	@CreationTimestamp
	@Column(name = "CREATED_TIMESTAMP")
	private Date createdTimestamp;
	
	@UpdateTimestamp
	@Column(name = "LAST_MODIFIED_TIMESTAMP")
	private Date lastModifiedTimestamp;
}
