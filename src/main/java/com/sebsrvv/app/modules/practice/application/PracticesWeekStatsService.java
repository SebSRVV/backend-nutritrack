package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.PracticesWeekStats;
import com.sebsrvv.app.modules.practice.domain.PracticesWeekStatsRepository;
import com.sebsrvv.app.modules.practice.web.dto.PracticesWeekStatsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sebsrvv.app.modules.practice.web.dto.PracticesWeekStatsResponse;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PracticesWeekStatsService {
    @Autowired
    private PracticesWeekStatsRepository practicesWeekStatsRepository;

    @Transactional
    public void create(PracticesWeekStatsRequest body, UUID practiceId, UUID userId) {
        PracticesWeekStats practicesWeekStats = new PracticesWeekStats();
        practicesWeekStats.setPracticeId(practiceId);
        practicesWeekStats.setUserId(userId);
        practicesWeekStats.setName(body.getName());
        practicesWeekStats.setDaysPerWeek(body.getDaysPerWeek());
        practicesWeekStats.setAchievedDaysLast7(body.getAchievedDaysLast7());
        practicesWeekStats.setLoggedDaysLast7(body.getLoggedDaysLast7());
        practicesWeekStats.setFirstLogInRange(LocalDate.now());
        practicesWeekStats.setLastLogInRange(LocalDate.now());
        practicesWeekStatsRepository.save(practicesWeekStats);
    }

    @Transactional
    public void edit(PracticesWeekStatsRequest dto, UUID id) {
        PracticesWeekStats practicesWeekStats = practicesWeekStatsRepository.findById(id).orElse(null);
        if (practicesWeekStats == null) {
            return;
        } else {
            practicesWeekStats.setName(dto.getName());
            practicesWeekStats.setDaysPerWeek(dto.getDaysPerWeek());
            practicesWeekStats.setLoggedDaysLast7(dto.getLoggedDaysLast7());
            practicesWeekStats.setLastLogInRange(LocalDate.now());
            practicesWeekStatsRepository.save(practicesWeekStats);
            return;
        }
    }

    @Transactional
    public boolean delete(UUID id) {
        if (practicesWeekStatsRepository.existsById(id)) {
            practicesWeekStatsRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
