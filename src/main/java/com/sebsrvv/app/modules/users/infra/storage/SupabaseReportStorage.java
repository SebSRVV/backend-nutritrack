package com.sebsrvv.app.modules.users.infra.storage;

import com.sebsrvv.app.modules.users.port.out.ReportStoragePort;
import com.sebsrvv.app.supabase.SupabaseStorageProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class SupabaseReportStorage implements ReportStoragePort {

    private final SupabaseStorageProperties props;
    private final HttpClient http;

    public SupabaseReportStorage(SupabaseStorageProperties props) {
        this.props = props;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    @Override
    public String upload(String pathKeyRaw, byte[] content, String contentType) {
        final String baseUrl = strip(props.getUrl());
        final String pathKey = sanitize(pathKeyRaw);
        final String encodedPath = encode(pathKey);
        try {
            // PUT (upsert)
            HttpRequest put = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/storage/v1/object/" + props.getBucket() + "/" + pathKey))
                    .header("Authorization", "Bearer " + props.getServiceKey())
                    .header("apikey", props.getServiceKey())
                    .header("Content-Type", contentType)
                    .header("x-upsert", "true")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(content))
                    .build();
            HttpResponse<String> putResp = http.send(put, HttpResponse.BodyHandlers.ofString());
            if (putResp.statusCode() >= 300) {
                throw new RuntimeException("Supabase upload failed: " + putResp.statusCode() + " - " + putResp.body());
            }

            if (props.isPublicBucket()) {
                return baseUrl + "/storage/v1/object/public/" + props.getBucket() + "/" + encodedPath;
            } else {
                int expiresInSeconds = Math.max(60, props.getUrlExpirationMinutes() * 60);
                String body = "{\"expiresIn\":" + expiresInSeconds + "}";
                HttpRequest sign = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/storage/v1/object/sign/" + props.getBucket() + "/" + encodedPath))
                        .header("Authorization", "Bearer " + props.getServiceKey())
                        .header("apikey", props.getServiceKey())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                        .build();
                HttpResponse<String> signResp = http.send(sign, HttpResponse.BodyHandlers.ofString());
                if (signResp.statusCode() >= 300) {
                    throw new RuntimeException("Supabase sign failed: " + signResp.statusCode() + " - " + signResp.body());
                }
                String signed = extract(signResp.body(), "signedURL");
                if (signed == null || signed.isBlank()) signed = extract(signResp.body(), "url");
                if (signed == null || signed.isBlank()) {
                    throw new RuntimeException("Malformed sign response: " + signResp.body());
                }
                return signed.startsWith("/") ? baseUrl + signed : signed;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error uploading to Supabase Storage", e);
        }
    }

    // helpers
    private static String strip(String u){ return u.endsWith("/") ? u.substring(0, u.length()-1) : u; }
    private static String sanitize(String p){ return (p!=null && p.startsWith("/")) ? p.substring(1) : p; }
    private static String encode(String path) {
        return java.util.Arrays.stream(path.split("/"))
                .map(s -> java.net.URLEncoder.encode(s, StandardCharsets.UTF_8))
                .reduce((a,b)->a+"/"+b).orElse("");
    }
    private static String extract(String json, String field) {
        String q = '"' + field + '"' + ':';
        int i = json.indexOf(q); if (i<0) return null;
        int s = json.indexOf('"', i+q.length()); if (s<0) return null;
        int e = json.indexOf('"', s+1); if (e<0) return null;
        return json.substring(s+1, e);
    }
}
