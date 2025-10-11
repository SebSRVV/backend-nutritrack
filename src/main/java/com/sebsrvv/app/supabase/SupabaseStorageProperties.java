package com.sebsrvv.app.supabase;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "supabase")
public class SupabaseStorageProperties {

    private String url;
    private String serviceKey;
    private String bucket = "reports";
    private boolean publicBucket = false;
    private int urlExpirationMinutes = 60;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getServiceKey() { return serviceKey; }
    public void setServiceKey(String serviceKey) { this.serviceKey = serviceKey; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public boolean isPublicBucket() { return publicBucket; }
    public void setPublicBucket(boolean publicBucket) { this.publicBucket = publicBucket; }

    public int getUrlExpirationMinutes() { return urlExpirationMinutes; }
    public void setUrlExpirationMinutes(int urlExpirationMinutes) { this.urlExpirationMinutes = urlExpirationMinutes; }
}
