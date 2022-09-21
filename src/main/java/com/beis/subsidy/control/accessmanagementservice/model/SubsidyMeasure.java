package com.beis.subsidy.control.accessmanagementservice.model;


import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * 
 * Subsidy Measure entity class
 *
 */
@Entity(name = "SUBSIDY_MEASURE")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SubsidyMeasure {

	@Id
	@Column(name="SC_NUMBER")
	private String scNumber;

	@OneToMany(mappedBy="subsidyMeasure", cascade = CascadeType.ALL)
	private List<Award> awards;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "ga_id", nullable = false, insertable = false, updatable = false)
	private GrantingAuthority grantingAuthority;

	@OneToOne(mappedBy="subsidyMeasure")
	private LegalBasis legalBases;

	@Column(name = "SUBSIDY_MEASURE_TITLE")
	private String subsidyMeasureTitle;

	@Column(name = "START_DATE")
	private LocalDate startDate;

	@Column(name = "END_DATE")
	private LocalDate endDate;

	@Column(name = "DURATION")
	private BigInteger duration;

	@Column(name = "BUDGET")
	private String budget;

	@Column(name = "ADHOC")
	private boolean adhoc;

	@Column(name = "GA_SUBSIDY_WEBLINK")
	private String gaSubsidyWebLink;

	@Column(name = "PUBLISHED_MEASURE_DATE")
	private LocalDate publishedMeasureDate;

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

	@Column(name = "HAS_NO_END_DATE")
	private boolean hasNoEndDate;
}
