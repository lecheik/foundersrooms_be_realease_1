package com.foundersrooms.domain;

import java.util.HashSet;
import java.util.Set;

import com.foundersrooms.domain.project.Advice;
import com.foundersrooms.domain.project.Coaching;
import com.foundersrooms.domain.project.InvestmentAndFinance;

public class NeedTemp {
	private Set<InvestmentAndFinance> investment=new HashSet<>();
	private Set<Coaching> coaching=new HashSet<>();
	private Set<Advice> advice=new HashSet<>();
	public Set<InvestmentAndFinance> getInvestment() {
		return investment;
	}
	public void setInvestment(Set<InvestmentAndFinance> investment) {
		this.investment = investment;		
	}
	public Set<Coaching> getCoaching() {
		return coaching;		
	}
	public void setCoaching(Set<Coaching> coaching) {
		this.coaching = coaching;		
	}
	public Set<Advice> getAdvice() {
		return advice;
	}
	public void setAdvice(Set<Advice> advice) {
		this.advice = advice;
	}
	
	
}
