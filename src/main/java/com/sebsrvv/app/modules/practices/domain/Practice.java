package com.sebsrvv.app.modules.practices.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "practices") //schema = "practices"
public class Practice {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "icon", nullable = false)
    private String icon;

    @Column(name = "value_kind", nullable = false)
    private String valueKind;

    @Column(name = "target_value", nullable = false)
    private Double targetValue;

    @Column(name = "target_unit", nullable = false)
    private String targetUnit;

    // Mapeo del ENUM de PostgreSQL
    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, columnDefinition = "practice_operator")
    private practice_operator operator;

    @Column(name = "days_per_week", nullable = false)
    private Integer daysPerWeek;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // Constructor
    public Practice() {
        //this.id = UUID.randomUUID();
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getValueKind() { return valueKind; }
    public void setValueKind(String valueKind) { this.valueKind = valueKind; }

    public Double getTargetValue() { return targetValue; }
    public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }

    public String getTargetUnit() { return targetUnit; }
    public void setTargetUnit(String targetUnit) { this.targetUnit = targetUnit; }

    public practice_operator getOperator() { return operator; }
    public void setOperator(practice_operator operator) { this.operator = operator; }

    public Integer getDaysPerWeek() { return daysPerWeek; }
    public void setDaysPerWeek(Integer daysPerWeek) { this.daysPerWeek = daysPerWeek; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
