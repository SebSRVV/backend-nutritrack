package com.sebsrvv.app.modules.auth.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Table(name = "users", schema = "auth")
@Immutable
public class UserAccount {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "email", insertable = false, updatable = false)
    private String email;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
