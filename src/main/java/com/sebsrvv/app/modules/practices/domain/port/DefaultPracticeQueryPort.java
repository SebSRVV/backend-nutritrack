package com.sebsrvv.app.modules.practices.domain.port;

import com.sebsrvv.app.modules.practices.domain.model.DefaultPractice;
import java.util.List;

public interface DefaultPracticeQueryPort {
    List<DefaultPractice> findAll();
}
