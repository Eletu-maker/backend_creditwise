package com.creditwise.repository;

import com.creditwise.entity.PlanTask;
import com.creditwise.entity.CreditPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanTaskRepository extends JpaRepository<PlanTask, UUID> {
    List<PlanTask> findByPlan(CreditPlan plan);
    List<PlanTask> findByPlanAndTaskStatus(CreditPlan plan, PlanTask.TaskStatus status);
}