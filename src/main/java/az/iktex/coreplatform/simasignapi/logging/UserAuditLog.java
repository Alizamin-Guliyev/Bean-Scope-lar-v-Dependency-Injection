package az.iktex.coreplatform.simasignapi.logging;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Component
@Scope("prototype")
public class UserAuditLog {
    private final String auditId= UUID.randomUUID().toString();
    private final LocalDateTime createdAt = LocalDateTime.now();
}
