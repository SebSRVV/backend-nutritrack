package com.sebsrvv.app.modules.practices.domain.model;

public class DefaultPractice {
    private Integer id;
    private String practiceName;
    private String description;
    private String icon;
    private Integer frequencyTarget;
    private Boolean isActive;

    public DefaultPractice() {}

    public DefaultPractice(Integer id, String practiceName, String description, String icon,
                           Integer frequencyTarget, Boolean isActive) {
        this.id = id;
        this.practiceName = practiceName;
        this.description = description;
        this.icon = icon;
        this.frequencyTarget = frequencyTarget;
        this.isActive = isActive;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public String getPracticeName() { return practiceName; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public Integer getFrequencyTarget() { return frequencyTarget; }
    public Boolean getIsActive() { return isActive; }

    public void setId(Integer id) { this.id = id; }
    public void setPracticeName(String practiceName) { this.practiceName = practiceName; }
    public void setDescription(String description) { this.description = description; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setFrequencyTarget(Integer frequencyTarget) { this.frequencyTarget = frequencyTarget; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
