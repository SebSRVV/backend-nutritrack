package com.sebsrvv.app.modules.practice.domain;

import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "practices")
public class Practices {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "icon", nullable = false)
    private String icon;

    @Column(name = "value_kind", nullable = false)
    private String value_kind;

    @Column(name = "target_value", nullable = false)
    private Double target_value;

    @Column(name = "target_unit", nullable = false)
    private String target_unit;

    @Column(name = "operator", nullable = false)
    private String practice_operator;

    @Column(name = "days_per_week", nullable = false)
    private Integer days_per_week;

    @Column(name = "is_active", nullable = false)
    private Boolean is_active;

    // Constructor vacío (sin inicialización manual de IDs)
    public Practices() {
        // Hibernate se encargará de generar el id
        // Si necesitas un userId por defecto, inicialízalo en el servicio
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getValueKind() { return value_kind; }
    public void setValueKind(String valueKind) { this.value_kind = valueKind; }

    public Double getTargetValue() { return target_value; }
    public void setTargetValue(Double targetValue) { this.target_value = targetValue; }

    public String getTargetUnit() { return target_unit; }
    public void setTargetUnit(String targetUnit) { this.target_unit = targetUnit; }

    public String getPracticeOperator() { return practice_operator; }
    public void setPracticeOperator(String practice_operator) { this.practice_operator = practice_operator; }

    public Integer getDaysPerWeek() { return days_per_week; }
    public void setDaysPerWeek(Integer daysPerWeek) { this.days_per_week = daysPerWeek; }

    public Boolean getIsActive() { return is_active; }
    public void setIsActive(Boolean isActive) { this.is_active = isActive; }
}