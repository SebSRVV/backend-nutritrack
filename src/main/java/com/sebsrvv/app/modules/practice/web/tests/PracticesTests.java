package com.sebsrvv.app.modules.practice.web.tests;

import com.sebsrvv.app.modules.practice.application.PracticesService;
import com.sebsrvv.app.modules.practice.web.dto.PracticesDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PracticesTests {

    @Autowired
    private PracticesService practicesService;

    @Test
    public void test() {
        PracticesDTO body = new PracticesDTO();
        body.setName("test");
        body.setIcon("icon");
        body.setDescription("description");
        body.setPractice_operator("a");
        body.setDays_per_week(2);
        body.setTarget_unit("meters");
        body.setValue_kind("boolean");
        body.setIs_active(true);
        practicesService.createPractice(body, UUID.fromString("641ef3e1-9d56-4487-8e1e-d89733103ed0"));
    }


}
