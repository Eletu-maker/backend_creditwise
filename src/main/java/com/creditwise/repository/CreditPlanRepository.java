package com.creditwise.repository;

import com.creditwise.entity.CreditPlan;
import com.creditwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CreditPlanRepository extends JpaRepository<CreditPlan, UUID> {
    List<CreditPlan> findByClient(User client);
    List<CreditPlan> findByOfficer(User officer);
    List<CreditPlan> findByClientAndPlanStatus(User client, CreditPlan.PlanStatus status);
}