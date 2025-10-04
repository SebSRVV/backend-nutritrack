package com.sebsrvv.app.modules.practices.service;

import com.sebsrvv.app.modules.practices.dto.*;
import com.sebsrvv.app.modules.practices.entity.HealthyPractice;
import com.sebsrvv.app.modules.practices.entity.PracticeLog;
import com.sebsrvv.app.modules.practices.repo.HealthyPracticeRepository;
import com.sebsrvv.app.modules.practices.repo.PracticeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PracticeService {
    private final HealthyPracticeRepository practiceRepo;
    private final PracticeLogRepository logRepo;

    public List<HealthyPractice> list(UUID userId){
        return practiceRepo.findByUser_id(userId);
    }

    @Transactional
    public HealthyPractice create(UUID userId, CreatePracticeDto dto){
        HealthyPractice p = HealthyPractice.builder()
                .user_id(userId)
                .practice_name(dto.practice_name())
                .description(dto.description())
                .icon(dto.icon())
                .frequency_target(dto.frequency_target())
                .build();
        return practiceRepo.save(p);
    }

    @Transactional
    public PracticeLog check(UUID userId, CheckPracticeDto dto){
        PracticeLog log = PracticeLog.builder()
                .user_id(userId)
                .practice_id(dto.practice_id())
                .logged_at(dto.date().atStartOfDay().atOffset(ZoneOffset.UTC))
                .build();
        return logRepo.save(log);
    }

    public long weeklyCount(UUID userId, UUID practiceId, LocalDate start){
        OffsetDateTime from = start.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = start.plusDays(6).atTime(23,59,59).atOffset(ZoneOffset.UTC);
        return logRepo.findByUser_idAndPractice_idAndLogged_atBetween(userId, practiceId, from, to).size();
    }
}
