package com.foundersrooms.domain.project;

import javax.persistence.Entity;

@Entity
public class InvestmentAndFinance extends Need{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*
	@OneToMany(mappedBy="investment", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectInvestmentAndFinance> projectInvestment=new HashSet<>();

*/
	public InvestmentAndFinance() {
		super();
		// TODO Auto-generated constructor stub
	}

/*
	public Set<ProjectInvestmentAndFinance> getProjectInvestment() {
		return projectInvestment;
	}


	public void setProjectInvestment(Set<ProjectInvestmentAndFinance> projectInvestment) {
		this.projectInvestment = projectInvestment;
	}	
	*/
	
	
}
