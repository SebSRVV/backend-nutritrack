package com.sebsrvv.app.modules.users.port.out;

public interface ReportStoragePort {
    String upload(String pathKey, byte[] content, String contentType);
}
