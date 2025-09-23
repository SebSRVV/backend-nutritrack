// modules/users/port/UserProfileRepository.java
package com.sebsrvv.app.modules.users.port;

import reactor.core.publisher.Mono;
import java.util.Map;

public interface UserProfileRepository {
    Mono<Void> insertProfile(String id, String email, Map<String,Object> extra);
}
