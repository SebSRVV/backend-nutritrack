// practices/domain/port/PracticeCatalogQueryPort.java
package com.sebsrvv.app.modules.practices.domain.port;

import java.util.Map;

public interface PracticeCatalogQueryPort {
    Map<Integer, CatalogItem> getByDefaultIds(Iterable<Integer> defaultIds);
    record CatalogItem(String name, String description, String icon, Integer sortOrder) {}
}
