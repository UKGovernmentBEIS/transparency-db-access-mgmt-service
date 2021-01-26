package com.beis.subsidy.control.accessmanagementservice.model;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 
 * Granting Authority Entity Class 
 *
 */
@Entity(name = "GRANTING_AUTHORITY")
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "granting_authority_read_seq", sequenceName = "granting_authority_read_seq",
		allocationSize = 1)
@Setter
@Getter
public class GrantingAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "granting_authority_read_seq")
	@Column(name="GA_ID")
	private Long gaId;

	@OneToMany(mappedBy="grantingAuthority", cascade = CascadeType.ALL)
	private List<Award> awards;

	@OneToMany(mappedBy="grantingAuthority")
	private List<SubsidyMeasure> subsidyMeasure;

	@Column(name = "GA_NAME")
	private String grantingAuthorityName;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "APPROVED_BY")
	private String approvedBy;

	@Column(name = "STATUS")
	private String status;

	@CreationTimestamp
	@Column(name = "CREATED_TIMESTAMP")
	private LocalDate createdTimestamp;

	@UpdateTimestamp
	@Column(name = "LAST_MODIFIED_TIMESTAMP")
	private LocalDate lastModifiedTimestamp;
}
