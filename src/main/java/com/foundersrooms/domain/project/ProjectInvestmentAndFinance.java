package com.foundersrooms.domain.project;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.foundersrooms.domain.people.ServiceField;

@Entity
public class ProjectInvestmentAndFinance implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2043885490049524626L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id", nullable = false, updatable = false)
	private Long id;
	
	@Embedded
	private ServiceField fields;
	
	private BigDecimal minAmount;
	
	private BigDecimal maxAmount;
	
	private int interestRate;
	
	@ManyToOne(fetch=FetchType.EAGER,optional=false)
	@JoinColumn(name="project_id")
	private Project project;
	
	@ManyToOne(fetch=FetchType.EAGER,optional=false)
	@JoinColumn(name="inv_id")
	private InvestmentAndFinance investment;

	public ProjectInvestmentAndFinance() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public int getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(int interestRate) {
		this.interestRate = interestRate;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public InvestmentAndFinance getInvestment() {
		return investment;
	}

	public void setInvestment(InvestmentAndFinance investment) {
		this.investment = investment;
	}
	
	
	
}
