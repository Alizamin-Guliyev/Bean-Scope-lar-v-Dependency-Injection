package az.iktex.coreplatform.simasignapi.component;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.concurrent.atomic.AtomicLong;

@Component
@ApplicationScope
public class UserStatistics {

    private final AtomicLong createdUsers = new AtomicLong(0);
    private final AtomicLong getUserByIdCalls = new AtomicLong(0);

    public void incrementCreatedUsers() {
        createdUsers.incrementAndGet();
    }

    public void incrementGetUserByIdCalls() {
        getUserByIdCalls.incrementAndGet();
    }

    public long getCreatedUsers() {
        return createdUsers.get();
    }

    public long getGetUserByIdCalls() {
        return getUserByIdCalls.get();
    }
}
