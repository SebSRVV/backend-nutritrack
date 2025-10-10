package com.sebsrvv.app.modules.practices.domain.model;

import java.util.UUID;

public class PracticeSelection {
    private UUID id;
    private UUID userId;
    private Integer defaultId;
    private String practiceName;
    private String description;
    private String icon;
    private Integer frequencyTarget;
    private Boolean isActive;
    private Integer sortOrder;

    public static PracticeSelection of(UUID id, UUID userId, Integer defaultId, String practiceName,
                                       String description, String icon, Integer frequencyTarget,
                                       Boolean isActive, Integer sortOrder) {
        PracticeSelection p = new PracticeSelection();
        p.id = id;
        p.userId = userId;
        p.defaultId = defaultId;
        p.practiceName = practiceName;
        p.description = description;
        p.icon = icon;
        p.frequencyTarget = frequencyTarget;
        p.isActive = isActive;
        p.sortOrder = sortOrder;
        return p;
    }

    // ----- getters -----
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public Integer getDefaultId() { return defaultId; }
    public String getPracticeName() { return practiceName; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public Integer getFrequencyTarget() { return frequencyTarget; }
    public Boolean getIsActive() { return isActive; } // coincide con el Controller
    public Integer getSortOrder() { return sortOrder; }

    // ----- setters -----
    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setDefaultId(Integer defaultId) { this.defaultId = defaultId; }
    public void setPracticeName(String practiceName) { this.practiceName = practiceName; }
    public void setDescription(String description) { this.description = description; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setFrequencyTarget(Integer frequencyTarget) { this.frequencyTarget = frequencyTarget; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
