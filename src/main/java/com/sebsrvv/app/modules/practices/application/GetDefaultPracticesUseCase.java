package com.sebsrvv.app.modules.practices.application;

import com.sebsrvv.app.modules.practices.domain.model.DefaultPractice;
import com.sebsrvv.app.modules.practices.domain.port.DefaultPracticeQueryPort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetDefaultPracticesUseCase {

    private final DefaultPracticeQueryPort repo;

    public GetDefaultPracticesUseCase(DefaultPracticeQueryPort repo) {
        this.repo = repo;
    }

    public List<DefaultPractice> execute() {
        return repo.findAll();
    }
}
