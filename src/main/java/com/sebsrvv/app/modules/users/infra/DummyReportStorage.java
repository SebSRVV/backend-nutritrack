package com.sebsrvv.app.modules.users.infra;

import com.sebsrvv.app.modules.users.port.out.ReportStoragePort;
import org.springframework.stereotype.Component;

@Component
public class DummyReportStorage implements ReportStoragePort {
    @Override
    public String publish(String storageKeyOrTempUrl) {
        // TODO: subir a S3/GCS/Supabase Storage, firmar URL, etc.
        return "https://cdn.example.com/" + storageKeyOrTempUrl;
    }
}
