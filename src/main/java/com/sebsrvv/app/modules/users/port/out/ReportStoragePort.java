package com.sebsrvv.app.modules.users.port.out;

/** Persiste o publica el PDF y retorna una URL accesible */
public interface ReportStoragePort {
    String publish(String storageKeyOrTempUrl);
}
