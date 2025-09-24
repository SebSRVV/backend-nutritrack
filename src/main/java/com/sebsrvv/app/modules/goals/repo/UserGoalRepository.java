package com.sebsrvv.app.modules.goals.repo;

import com.sebsrvv.app.modules.goals.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserGoalRepository extends JpaRepository<UserGoal, java.util.UUID> {
    List<UserGoal> findByUser_id(java.util.UUID userId);
}
