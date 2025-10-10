// practices/domain/model/PracticeSelection.java
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
        p.id = id; p.userId = userId; p.defaultId = defaultId;
        p.practiceName = practiceName; p.description = description; p.icon = icon;
        p.frequencyTarget = frequencyTarget; p.isActive = isActive; p.sortOrder = sortOrder;
        return p;
    }
    // getters y setters…
}
