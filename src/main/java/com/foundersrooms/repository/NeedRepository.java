package com.foundersrooms.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.foundersrooms.domain.project.Advice;
import com.foundersrooms.domain.project.Coaching;
import com.foundersrooms.domain.project.InvestmentAndFinance;
import com.foundersrooms.domain.project.Need;



public interface NeedRepository extends CrudRepository<Need, Long> {

	@Query("SELECT invfinance FROM InvestmentAndFinance invfinance")
	Set<InvestmentAndFinance> findAllInvestmentServices();
	
	@Query("SELECT coach FROM Coaching coach")
	Set<Coaching> findAllCoachingServices();
	
	@Query("SELECT adv FROM Advice adv")
	Set<Advice> findAllAdviceServices();
}
